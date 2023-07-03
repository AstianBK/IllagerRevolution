package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketWhistle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.projectile.ArrowBeast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class IllagerBeastTamerEntity extends SpellcasterKnight implements IAnimatable, InventoryCarrier, RangedAttackMob {
    private final AnimationFactory factory= GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(1);

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 40.D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D).build();

    }
    public boolean isWieldingTwoHandedWeapon() {
        return// Bow and crossbows
                (this.getMainHandItem().getItem() instanceof ProjectileWeaponItem
                        || this.getOffhandItem().getItem() instanceof ProjectileWeaponItem
                        || this.getMainHandItem().getUseAnimation() == UseAnim.BOW
                        || this.getOffhandItem().getUseAnimation() == UseAnim.BOW);
    }

    public IllagerBeastTamerEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);

    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isAggressive() && !this.isCastingSpell() && !this.isPassenger()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.walk", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isWieldingTwoHandedWeapon() && event.isMoving() && !this.isPassenger() && !this.isCastingSpell()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (this.isWieldingTwoHandedWeapon() && !event.isMoving() && this.isAggressive() && !this.isCastingSpell() && !this.isPassenger()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.attack1", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (this.isCastingSpell()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager."+(this.isPassenger() ? "sit2" : "summon"), ILoopType.EDefaultLoopTypes.LOOP));
        }
        else event.getController().setAnimation(new AnimationBuilder().addAnimation(this.isPassenger()?"animation.beasttamerillager.sit" : "animation.beasttamerillager.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;

    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        if(this.level.random.nextInt(0,8)==0){
            ItemStack stack=new ItemStack(ModItems.ARROW_BEAST.get());
            stack.setCount(this.level.random.nextInt(1,2));
            this.spawnAtLocation(stack);
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    @Override
    public boolean isPassenger() {
        if(this.getVehicle()!=null){
            return this.getVehicle().isAlive();
        }
        return false;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        if(this.level.random.nextFloat()>0.85f){
            WildRavagerEntity ravager=new WildRavagerEntity(ModEntityTypes.WILD_RAVAGER.get(),this.level);
            ravager.setPos(this.getX(),this.getY(),this.getZ());
            ravager.finalizeSpawn((ServerLevelAccessor) this.level,this.level.getCurrentDifficultyAt(this.blockPosition()),MobSpawnType.MOB_SUMMONED,null,null);
            this.level.addFreshEntity(ravager);
            this.startRiding(ravager);
            ravager.setOwner(this);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset()+0.5d;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(1,new AvoidEntityGoal<>(this,Player.class,5.0f,1.3D,1.3D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
        this.goalSelector.addGoal(7, new BreakDoorGoal(this, e -> true));
        this.goalSelector.addGoal(0,new BeastTamerSummonSpellGoal());
        this.goalSelector.addGoal(2, new IllagerBeastTamerBowAttackGoal<IllagerBeastTamerEntity>(this, 0.5D, 20, 15.0f) {
        });
    }
    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        ArrowBeast entityarrow = new ArrowBeast(this.level, this);
        double d0 = target.getY() + target.getEyeHeight() - 1.1;
        double d1 = target.getX() - this.getX();
        double d3 = target.getZ() - this.getZ();
        entityarrow.shoot(d1, d0 - entityarrow.getY() + Math.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, 1.6F, 0.1F);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        level.addFreshEntity(entityarrow);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }


    @Override
    public SimpleContainer getInventory() {
        return inventory;
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_37845_) {

    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    class BeastTamerSummonSpellGoal extends SpellcasterUseSpellGoal {

        private final TargetingConditions beastCountTargeting = TargetingConditions.forCombat().range(20.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = IllagerBeastTamerEntity.this.level.getNearbyEntities(IllagerBeastEntity.class, this.beastCountTargeting, IllagerBeastTamerEntity.this, IllagerBeastTamerEntity.this.getBoundingBox().inflate(20.0D)).size();
                return i<4;
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 500;
        }

        @Override
        public void start() {
            super.start();
            PacketHandler.sendToAllTracking(new PacketWhistle(IllagerBeastTamerEntity.this),IllagerBeastTamerEntity.this);
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.TAMER_WHISTLE.get();
        }

        public void stop() {
            super.stop();
            IllagerBeastTamerEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }

        protected void performSpellCasting() {
            ServerLevel serverlevel = (ServerLevel)IllagerBeastTamerEntity.this.level;
            if(IllagerBeastTamerEntity.this.level.random.nextFloat()<0.33f){
                BlockPos blockpos = IllagerBeastTamerEntity.this.blockPosition().offset(-2 + IllagerBeastTamerEntity.this.random.nextInt(5), 1, -2 + IllagerBeastTamerEntity.this.random.nextInt(5));
                RakerEntity raker = ModEntityTypes.RAKER.get().create(IllagerBeastTamerEntity.this.level);
                raker.moveTo(blockpos, 0.0F, 0.0F);
                raker.finalizeSpawn(serverlevel, IllagerBeastTamerEntity.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                serverlevel.addFreshEntityWithPassengers(raker);
                raker.setOwner(IllagerBeastTamerEntity.this);

            }else if(IllagerBeastTamerEntity.this.level.random.nextFloat()<0.33f){
                BlockPos blockpos = IllagerBeastTamerEntity.this.blockPosition().offset(-2 + IllagerBeastTamerEntity.this.random.nextInt(5), 1, -2 + IllagerBeastTamerEntity.this.random.nextInt(5));
                MaulerEntity mauler = ModEntityTypes.MAULER.get().create(IllagerBeastTamerEntity.this.level);
                mauler.moveTo(blockpos, 0.0F, 0.0F);
                mauler.finalizeSpawn(serverlevel, IllagerBeastTamerEntity.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                serverlevel.addFreshEntityWithPassengers(mauler);
                mauler.setOwner(IllagerBeastTamerEntity.this);
            }else {
                List<AbstractIllager> illagerList = IllagerBeastTamerEntity.this.level.getEntitiesOfClass(AbstractIllager.class,IllagerBeastTamerEntity.this.getBoundingBox().inflate(40.0D));
                int cc=0;
                for(AbstractIllager illager : illagerList){
                    if(illager.isAlive()){
                        BlockPos blockpos = illager.blockPosition().offset(-2 + illager.level.random.nextInt(5), 1, -2 + illager.level.random.nextInt(5));
                        ScroungerEntity scrounger = ModEntityTypes.SCROUNGER.get().create(IllagerBeastTamerEntity.this.level);
                        scrounger.moveTo(blockpos, 0.0F, 0.0F);
                        scrounger.finalizeSpawn(serverlevel, IllagerBeastTamerEntity.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                        serverlevel.addFreshEntityWithPassengers(scrounger);
                        scrounger.setOnCombat(true);
                        scrounger.setOwnerIllager(illager);
                        cc++;
                    }
                    if (cc>2){
                        break;
                    }
                }
            }
        }
    }


    static class IllagerBeastTamerBowAttackGoal<T extends net.minecraft.world.entity.Mob & RangedAttackMob> extends Goal {
        private final T mob;
        private final double speedModifier;
        private int attackIntervalMin;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public <M extends Monster & RangedAttackMob> IllagerBeastTamerBowAttackGoal(M pMob, double pSpeedModifier, int pAttackIntervalMin, float pAttackRadius){
            this((T) pMob, pSpeedModifier, pAttackIntervalMin, pAttackRadius);
        }

        public IllagerBeastTamerBowAttackGoal(T pMob, double pSpeedModifier, int pAttackIntervalMin, float pAttackRadius) {
            this.mob = pMob;
            this.speedModifier = pSpeedModifier;
            this.attackIntervalMin = pAttackIntervalMin;
            this.attackRadiusSqr = pAttackRadius * pAttackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public void setMinAttackInterval(int pAttackCooldown) {
            this.attackIntervalMin = pAttackCooldown;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return this.mob.getTarget() == null ? false : this.isHoldingBow();
        }

        protected boolean isHoldingBow() {
            return this.mob.isHolding(is -> is.getItem() instanceof BowItem);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingBow();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.seeTime = 0;
            this.attackTime = -1;
            this.mob.stopUsingItem();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) {
                    this.seeTime = 0;
                }

                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d0 > (double)(this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }

                    //this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }

                if (this.mob.isUsingItem()) {
                    if (!flag && this.seeTime < -60) {
                        this.mob.stopUsingItem();
                    } else if (flag) {
                        int i = this.mob.getTicksUsingItem();
                        if (i >= 20) {
                            this.mob.stopUsingItem();
                            this.mob.performRangedAttack(livingentity, BowItem.getPowerForTime(i));
                            this.attackTime = this.attackIntervalMin;
                        }
                    }
                } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
                }

            }
        }
    }
}
