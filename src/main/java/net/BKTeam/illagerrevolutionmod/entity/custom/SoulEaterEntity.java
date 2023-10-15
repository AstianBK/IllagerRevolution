package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SoulEaterEntity extends Monster implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET_0 =
            SynchedEntityData.defineId(SoulEaterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DRAIN_SOUL =
            SynchedEntityData.defineId(SoulEaterEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(SoulEaterEntity.class,
            EntityDataSerializers.BYTE);

    private int drainDuration;
    public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907F);
    @Nullable
    Mob owner;
    @Nullable
    private BlockPos boundOrigin;
    @javax.annotation.Nullable
    private LivingEntity clientSideDrainTarget0;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    private int cooldownDrain;

    public SoulEaterEntity(EntityType<? extends SoulEaterEntity> p_33984_, Level p_33985_) {
        super(p_33984_, p_33985_);
        this.moveControl = new SoulEaterEntity.SoulEaterEntityMoveControl(this);
        this.xpReward = 3;
        this.drainDuration = 0;
        this.cooldownDrain = 0;
    }

    public boolean isFlapping() {
        return this.tickCount % TICKS_PER_FLAP == 0;
    }

    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(DamageSource.STARVE, 1.0F);
        }
        if(this.isDrainSoul()){
            LivingEntity target = this.getActiveAttackTarget0();
            if(this.drainDuration>0){
                if(this.checkIsAlive(target)){
                    if(this.tickCount%20==0){
                        if(this.tickCount%40==0){
                            target.level.playSound(null,this, ModSounds.SOUL_SAGE_DRAIN.get(), SoundSource.HOSTILE,1.0F,1.0F);
                        }
                        if(target.hurt(DamageSource.MAGIC,1.0F)){
                            this.heal(1.0F);
                        }
                    }
                }else {
                    this.setDrainSoul(false);
                    this.setActiveAttackTarget(0);
                }
                this.drainDuration--;
                if(this.drainDuration==0){
                    this.setDrainSoul(false);
                    this.setActiveAttackTarget(0);
                }
            }
        }else {
            if(this.cooldownDrain>0){
                this.cooldownDrain--;
            }
        }


    }


    public boolean checkIsAlive(LivingEntity living){
        if (living!=null){
            if(living.isAlive()){
                double dist = this.distanceTo(living);
                if(dist<=9){
                    return true;
                }else if (living instanceof Player player && !player.isCreative()){
                    return true;
                }else if (this.getSensing().hasLineOfSight(living)){
                    return true;
                }
            }
        }
        //this.stopDrainSound(living);
        this.setActiveAttackTarget(0);
        this.setDrainSoul(false);
        return false;
    }

    void setActiveAttackTarget(int pEntityId) {
        this.entityData.set(DATA_ID_ATTACK_TARGET_0, pEntityId);
    }
    private   <E extends IAnimatable> PlayState predicateMaster(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.souleater.idle",ILoopType.EDefaultLoopTypes.LOOP));
        if(this.swinging && this.isDrainSoul()){
            if (event.isMoving()) {
                event.getController().setAnimationSpeed(2.0F);
            } else {
                event.getController().setAnimationSpeed(1.0F);
            }
        }
        return PlayState.CONTINUE;
    }

    private   <E extends IAnimatable> PlayState predicateHead(AnimationEvent<E> event) {
        if(this.isDrainSoul() && !this.swinging && !event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.souleater.drain",ILoopType.EDefaultLoopTypes.LOOP));
        }else if(!this.isDrainSoul() && this.swinging && !event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.souleater.bite",ILoopType.EDefaultLoopTypes.LOOP));
        }else{
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new SoulEaterEntity.SoulEaterEntityChargeAttackGoal());
        this.goalSelector.addGoal(8, new SoulEaterEntity.SoulEaterEntityRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new SoulEaterEntity.SoulEaterEntityCopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
    }
    public LivingEntity getActiveAttackTarget0() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideDrainTarget0 != null) {
                return this.clientSideDrainTarget0;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_0));

                if (entity instanceof LivingEntity) {
                    this.clientSideDrainTarget0 = (LivingEntity)entity;
                    return this.clientSideDrainTarget0;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET_0) != 0;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (DATA_ID_ATTACK_TARGET_0.equals(pKey)) {
            this.clientSideDrainTarget0 = null;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_ID_ATTACK_TARGET_0, 0);
        this.entityData.define(DRAIN_SOUL,false);

    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(pCompound.getInt("BoundX"), pCompound.getInt("BoundY"), pCompound.getInt("BoundZ"));
        }

        if (pCompound.contains("LifeTicks")) {
            this.setLimitedLife(pCompound.getInt("LifeTicks"));
        }

    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.boundOrigin != null) {
            pCompound.putInt("BoundX", this.boundOrigin.getX());
            pCompound.putInt("BoundY", this.boundOrigin.getY());
            pCompound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife) {
            pCompound.putInt("LifeTicks", this.limitedLifeTicks);
        }

    }
    public void setDrainSoul(boolean pBoolean){
        this.entityData.set(DRAIN_SOUL,pBoolean);
        this.drainDuration = pBoolean ? 10 : 0;
        this.cooldownDrain = pBoolean ? 0 : 600;
    }
    public boolean isDrainSoul(){
        return this.entityData.get(DRAIN_SOUL);
    }

    @Nullable
    public Mob getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pBoundOrigin) {
        this.boundOrigin = pBoundOrigin;
    }

    private boolean getSoulEaterEntityFlag(int pMask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & pMask) != 0;
    }

    private void setSoulEaterEntityFlag(int pMask, boolean pValue) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (pValue) {
            i |= pMask;
        } else {
            i &= ~pMask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.getSoulEaterEntityFlag(1);
    }

    public void setIsCharging(boolean pCharging) {
        this.setSoulEaterEntityFlag(1, pCharging);
    }

    public void setOwner(Mob pOwner) {
        this.owner = pOwner;
    }

    public void setLimitedLife(int pLimitedLifeTicks) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = pLimitedLifeTicks;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.VEX_HURT;
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219135_, DifficultyInstance p_219136_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this,"predicate_master",
                0,this::predicateMaster));
        data.addAnimationController(new AnimationController<>(this,"predicate_head",
                0,this::predicateHead));
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==60){
            this.setDrainSoul(true);
        }else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.isDrainSoul()){
            this.setDrainSoul(false);
            this.setActiveAttackTarget(0);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    class SoulEaterEntityChargeAttackGoal extends Goal {

        public SoulEaterEntityChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            LivingEntity livingentity = SoulEaterEntity.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && !SoulEaterEntity.this.getMoveControl().hasWanted() && SoulEaterEntity.this.random.nextInt(reducedTickDelay(7)) == 0) {
                return SoulEaterEntity.this.distanceToSqr(livingentity) > 4.0D;
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return SoulEaterEntity.this.getMoveControl().hasWanted() && SoulEaterEntity.this.isCharging() && SoulEaterEntity.this.getTarget() != null && SoulEaterEntity.this.getTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            LivingEntity livingentity = SoulEaterEntity.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                SoulEaterEntity.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            }

            SoulEaterEntity.this.setIsCharging(true);
            SoulEaterEntity.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            SoulEaterEntity.this.setIsCharging(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = SoulEaterEntity.this.getTarget();
            if (livingentity != null) {
                double d0 = SoulEaterEntity.this.distanceToSqr(livingentity);
                AttackMelee attackMelee =  d0 > 9.0D && SoulEaterEntity.this.cooldownDrain<=0 ? AttackMelee.RANGED : AttackMelee.MELEE;
                if (SoulEaterEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()) && attackMelee == AttackMelee.MELEE) {
                    SoulEaterEntity.this.swing(InteractionHand.MAIN_HAND);
                    SoulEaterEntity.this.doHurtTarget(livingentity);
                    SoulEaterEntity.this.setIsCharging(false);
                } else {
                    if (d0 < 9.0D && !SoulEaterEntity.this.isDrainSoul()) {
                        Vec3 vec3 = livingentity.getEyePosition();
                        SoulEaterEntity.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
                    }else if(SoulEaterEntity.this.isDrainSoul()){
                        SoulEaterEntity.this.getNavigation().stop();
                    }
                }
                if (attackMelee == AttackMelee.RANGED){
                    if(SoulEaterEntity.this.drainDuration <= 0 && SoulEaterEntity.this.hasLineOfSight(livingentity)){
                        SoulEaterEntity.this.setActiveAttackTarget(livingentity.getId());
                        SoulEaterEntity.this.setDrainSoul(true);
                    }
                }

            }
        }

        enum AttackMelee{
            MELEE,
            RANGED;
        }
    }

    class SoulEaterEntityCopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public SoulEaterEntityCopyOwnerTargetGoal(PathfinderMob p_34056_) {
            super(p_34056_, false);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return SoulEaterEntity.this.owner != null && SoulEaterEntity.this.owner.getTarget() != null && this.canAttack(SoulEaterEntity.this.owner.getTarget(), this.copyOwnerTargeting);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            SoulEaterEntity.this.setTarget(SoulEaterEntity.this.owner.getTarget());
            super.start();
        }
    }

    class SoulEaterEntityMoveControl extends MoveControl {
        public SoulEaterEntityMoveControl(SoulEaterEntity p_34062_) {
            super(p_34062_);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO && !SoulEaterEntity.this.isDrainSoul()) {
                Vec3 vec3 = new Vec3(this.wantedX - SoulEaterEntity.this.getX(), this.wantedY - SoulEaterEntity.this.getY(), this.wantedZ - SoulEaterEntity.this.getZ());
                double d0 = vec3.length();
                if (d0 < SoulEaterEntity.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    SoulEaterEntity.this.setDeltaMovement(SoulEaterEntity.this.getDeltaMovement().scale(0.5D));
                } else {
                    SoulEaterEntity.this.setDeltaMovement(SoulEaterEntity.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
                    if (SoulEaterEntity.this.getTarget() == null) {
                        Vec3 vec31 = SoulEaterEntity.this.getDeltaMovement();
                        SoulEaterEntity.this.setYRot(-((float)Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
                        SoulEaterEntity.this.yBodyRot = SoulEaterEntity.this.getYRot();
                    } else {
                        double d2 = SoulEaterEntity.this.getTarget().getX() - SoulEaterEntity.this.getX();
                        double d1 = SoulEaterEntity.this.getTarget().getZ() - SoulEaterEntity.this.getZ();
                        SoulEaterEntity.this.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                        SoulEaterEntity.this.yBodyRot = SoulEaterEntity.this.getYRot();
                    }
                }

            }else {
                SoulEaterEntity.this.getNavigation().stop();
            }
        }
    }

    class SoulEaterEntityRandomMoveGoal extends Goal {
        public SoulEaterEntityRandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return !SoulEaterEntity.this.getMoveControl().hasWanted() && SoulEaterEntity.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            BlockPos blockpos = SoulEaterEntity.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = SoulEaterEntity.this.blockPosition();
            }

            if(!SoulEaterEntity.this.isDrainSoul()){
                for(int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.offset(SoulEaterEntity.this.random.nextInt(15) - 7, SoulEaterEntity.this.random.nextInt(11) - 5, SoulEaterEntity.this.random.nextInt(15) - 7);
                    if (SoulEaterEntity.this.level.isEmptyBlock(blockpos1)) {
                        SoulEaterEntity.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                        if (SoulEaterEntity.this.getTarget() == null) {
                            SoulEaterEntity.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                        }
                        break;
                    }
                }
            }
        }
    }
}
