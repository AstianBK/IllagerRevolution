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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
        return
                // Bow and crossbows
                (this.getMainHandItem().getItem() instanceof ProjectileWeaponItem
                        || this.getOffhandItem().getItem() instanceof ProjectileWeaponItem
                        || this.getMainHandItem().getUseAnimation() == UseAnim.BOW
                        || this.getOffhandItem().getUseAnimation() == UseAnim.BOW);
    }

    public IllagerBeastTamerEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);

    }
    private   <E extends IAnimatable> PlayState predicateSit(AnimationEvent<E> event) {
        if (this.isWieldingTwoHandedWeapon() && !event.isMoving() && isAggressive()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.attack1", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else if (this.isCastingSpell()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.summon", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else {
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;

    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (event.isMoving() && !this.isAggressive() && !this.isCastingSpell() && !this.isPassenger()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.walk", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isWieldingTwoHandedWeapon() && event.isMoving() && !this.isPassenger()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.beasttamerillager.walk2", ILoopType.EDefaultLoopTypes.LOOP));
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
        if(this.level.random.nextFloat()<0.70f){
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
        return super.getMyRidingOffset()+0.3d;
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
        this.goalSelector.addGoal(2, new RangedBowAttackGoal(this, 0.5D, 20, 15.0f) {
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
    class BeastTamerSummonSpellGoal extends SpellcasterUseSpellGoal {

        private final TargetingConditions vexCountTargeting = TargetingConditions.forCombat().range(20.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = IllagerBeastTamerEntity.this.level.getNearbyEntities(IllagerBeastEntity.class, this.vexCountTargeting, IllagerBeastTamerEntity.this, IllagerBeastTamerEntity.this.getBoundingBox().inflate(20.0D)).size();
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
                if(serverlevel.random.nextFloat()<0.50f){
                    IllagerBeastTamerEntity.this.startRiding(mauler);
                }
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


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
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
        return null;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller",
                0, this::predicate));
        data.addAnimationController(new AnimationController<>(this, "controller_Attack",
                0, this::predicateSit));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}
