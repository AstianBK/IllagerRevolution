package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulMissile;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumCrossbowItem;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;

public class AcolyteEntity extends SpellcasterKnight implements IAnimatable, InventoryCarrier, RangedAttackMob, CrossbowAttackMob {
    private final AnimationFactory factory= GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(1);

    private static final EntityDataAccessor<Integer> ID_PROFESSION =
            SynchedEntityData.defineId(AcolyteEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW =
            SynchedEntityData.defineId(AcolyteEntity.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 40.D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D).build();

    }

    public AcolyteEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);
    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
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
        this.setIdProfession(this.random.nextInt(1,4));
        if(this.getWeaponForProfession()!=null){
            this.setItemSlot(EquipmentSlot.MAINHAND,this.getWeaponForProfession());
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
        this.goalSelector.addGoal(1,new SoulMissileSpellGoal());
        this.goalSelector.addGoal(2,new SummonSoulEaterSpellGoal());
        this.goalSelector.addGoal(1,new AcolyteAttack(this,1.0D,true));
        this.goalSelector.addGoal(1, new AcolyteRangedCrossbowAttackGoal<AcolyteEntity>(this, 0.5D, 20) {
        });
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

    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(is -> is.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
        } else if(this.isAggressive()){
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }else {
            return this.isCastingSpell() ? IllagerArmPose.SPELLCASTING : IllagerArmPose.NEUTRAL;
        }
    }

    public ItemStack getWeaponForProfession(){
        ItemStack weapon=null;
        switch (this.getIdProfession()){
            case 1 ->weapon = new ItemStack(ModItems.ILLAGIUM_SWORD.get());
            case 3 ->weapon = new ItemStack(ModItems.ILLAGIUM_CROSSBOW.get());
        }
        return weapon;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public int getIdProfession(){
        return this.entityData.get(ID_PROFESSION);
    }

    public ProfessionTier getProfession(){
        return ProfessionTier.byId(this.getIdProfession() & 255);
    }

    public void setIdProfession(int pId){
        this.entityData.set(ID_PROFESSION,pId);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_PROFESSION,0);
        this.entityData.define(IS_CHARGING_CROSSBOW,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("idProfession",this.getIdProfession());
        pCompound.putBoolean("isChargingCrossbow",this.isChargingCrossbow());
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.setChargingCrossbow(pCompound.getBoolean("isChargingCrossbow"));
        this.setIdProfession(pCompound.getInt("idProfession"));
        super.readAdditionalSaveData(pCompound);
    }
    @Override
    public void setChargingCrossbow(boolean pIsCharging) {
        this.entityData.set(IS_CHARGING_CROSSBOW,pIsCharging);
    }

    public boolean isChargingCrossbow(){
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }


    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity p_33275_, ItemStack p_33276_, Projectile p_33277_, float p_33278_) {
        this.shootCrossbowProjectile(this, p_33275_, p_33277_, p_33278_, 1.6F);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    class SummonSoulEaterSpellGoal extends SpellcasterUseSpellGoal {
        public boolean canUse() {
            int i = AcolyteEntity.this.level.getEntitiesOfClass(SoulEaterEntity.class,
                AcolyteEntity.this.getBoundingBox().inflate(20.0D),e->e.getOwner()==AcolyteEntity.this).size();
            if(!super.canUse()) {
                return false;
            }
            return AcolyteEntity.this.getProfession() == ProfessionTier.MAGE && i<1 ;

        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 300;
        }

        @Override
        public void start() {
            super.start();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_CAST_SPELL;
        }

        public void stop() {
            super.stop();
            AcolyteEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }

        protected void performSpellCasting() {
            Mob owner = AcolyteEntity.this;
            LivingEntity target = AcolyteEntity.this.getTarget();
            
            SoulEaterEntity soulEater = new SoulEaterEntity(ModEntityTypes.SOUL_EATER.get(),owner.level);
            BlockPos blockpos = owner.blockPosition().offset(-2 + owner.getRandom().nextInt(5), 1, -2 + owner.getRandom().nextInt(5));
            soulEater.setOwner(owner);
            soulEater.moveTo(blockpos,0.0F,0.0F);
            soulEater.setLimitedLife(100);
            owner.level.addFreshEntity(soulEater);
        }
    }

    class SoulMissileSpellGoal extends SpellcasterUseSpellGoal {
        public boolean canUse() {
            return AcolyteEntity.this.getProfession() == ProfessionTier.MAGE && super.canUse();
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 300;
        }

        @Override
        public void start() {
            super.start();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_CAST_SPELL;
        }

        public void stop() {
            super.stop();
            AcolyteEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }

        protected void performSpellCasting() {
            LivingEntity owner = AcolyteEntity.this;
            LivingEntity target = AcolyteEntity.this.getTarget();
            if(target != null){
                BlockPos posTarget1=target.getOnPos();
                AreaFireColumnEntity areaFireColumn = new AreaFireColumnEntity(ModEntityTypes.AREA_FIRE_COLUMN.get(),owner.level,true);
                areaFireColumn.setPowerLevel(0);
                areaFireColumn.setOwner(owner);
                areaFireColumn.setDuration(100,0);
                areaFireColumn.setRadius(5.0F);
                areaFireColumn.moveTo(posTarget1,0.0F,0.0F);
                owner.level.addFreshEntity(areaFireColumn);
                target.level.playSound(null,owner,SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE,1.0F,1.0F);
            }

        }
    }


    public static class AcolyteRangedCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends Goal {
        public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
        private final T mob;
        private AcolyteRangedCrossbowAttackGoal.CrossbowState crossbowState = AcolyteRangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int seeTime;
        private int attackDelay;
        private int updatePathDelay;

        public AcolyteRangedCrossbowAttackGoal(T pMob, double pSpeedModifier, float pAttackRadius) {
            this.mob = pMob;
            this.speedModifier = pSpeedModifier;
            this.attackRadiusSqr = pAttackRadius * pAttackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return this.isValidTarget() && this.isHoldingCrossbow() && this.mob instanceof AcolyteEntity acolyte && acolyte.getProfession()==ProfessionTier.RANGED;
        }

        private boolean isHoldingCrossbow() {
            return this.mob.isHolding(is -> is.getItem() instanceof IllagiumCrossbowItem);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
        }

        private boolean isValidTarget() {
            return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.mob.setTarget((LivingEntity)null);
            this.seeTime = 0;
            if (this.mob.isUsingItem()) {
                this.mob.stopUsingItem();
                this.mob.setChargingCrossbow(false);
                IllagiumCrossbowItem.setCharged(this.mob.getUseItem(), false);
            }

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

                double d0 = this.mob.distanceToSqr(livingentity);
                boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
                if (flag2) {
                    --this.updatePathDelay;
                    if (this.updatePathDelay <= 0) {
                        this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
                        this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
                    }
                } else {
                    this.updatePathDelay = 0;
                    this.mob.getNavigation().stop();
                }

                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                if (this.crossbowState == AcolyteRangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
                    if (!flag2) {
                        this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof IllagiumCrossbowItem));
                        this.crossbowState = AcolyteRangedCrossbowAttackGoal.CrossbowState.CHARGING;
                        this.mob.setChargingCrossbow(true);
                    }
                } else if (this.crossbowState == AcolyteRangedCrossbowAttackGoal.CrossbowState.CHARGING) {
                    if (!this.mob.isUsingItem()) {
                        this.crossbowState = AcolyteRangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
                    }

                    int i = this.mob.getTicksUsingItem();
                    ItemStack itemstack = this.mob.getUseItem();
                    if (i >= CrossbowItem.getChargeDuration(itemstack)) {
                        this.mob.releaseUsingItem();
                        this.crossbowState = AcolyteRangedCrossbowAttackGoal.CrossbowState.CHARGED;
                        this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                        this.mob.setChargingCrossbow(false);
                    }
                } else if (this.crossbowState == AcolyteRangedCrossbowAttackGoal.CrossbowState.CHARGED) {
                    --this.attackDelay;
                    if (this.attackDelay == 0) {
                        this.crossbowState = AcolyteRangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
                    }
                } else if (this.crossbowState == AcolyteRangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && flag) {
                    this.mob.performRangedAttack(livingentity, 1.0F);
                    ItemStack itemstack1 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                    CrossbowItem.setCharged(itemstack1, false);
                    this.crossbowState = AcolyteRangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
                }

            }
        }

        private boolean canRun() {
            return this.crossbowState == AcolyteRangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
        }

        static enum CrossbowState {
            UNCHARGED,
            CHARGING,
            CHARGED,
            READY_TO_ATTACK;
        }
    }
    static class AcolyteAttack extends MeleeAttackGoal {
        private final AcolyteEntity goalOwner;

        public AcolyteAttack(AcolyteEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity)+2.0D;
            if (distance <= d0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.goalOwner.playSound(SoundEvents.STRAY_HURT, 1.0F, -1.0F);
                this.goalOwner.swing(InteractionHand.MAIN_HAND);
                this.goalOwner.doHurtTarget(entity);
            }
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.goalOwner.getProfession() == ProfessionTier.FIGHTER;
        }

    }

    public enum ProfessionTier {
        NONE(0),
        FIGHTER(1),
        MAGE(2),
        RANGED(3);

        private static final ProfessionTier[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ProfessionTier::getId)).toArray(ProfessionTier[]::new);
        private final int id;

        ProfessionTier(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static ProfessionTier byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }

    }
}
