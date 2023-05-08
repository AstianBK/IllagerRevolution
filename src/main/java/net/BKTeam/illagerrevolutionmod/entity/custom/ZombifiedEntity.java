package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.BKTeam.illagerrevolutionmod.entity.goals.FollowOwnerGoalReanimate;
import net.BKTeam.illagerrevolutionmod.entity.goals.Owner_Attacking;
import net.BKTeam.illagerrevolutionmod.entity.goals.Owner_Defend;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpawnReanimatedGoal;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Entity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Projectile;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSpawnedZombified;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.procedures.Event_Death;
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


public class ZombifiedEntity extends ReanimatedEntity implements IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(ZombifiedEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> HASSOUL =
            SynchedEntityData.defineId(ZombifiedEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<String> ID_SOUL =
            SynchedEntityData.defineId(ZombifiedEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> IS_SPAWNED =
            SynchedEntityData.defineId(ZombifiedEntity.class, EntityDataSerializers.BOOLEAN);

    private int attackTimer;

    private int animSpawnTimer;


    public ZombifiedEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.attackTimer = 0;
        this.animSpawnTimer = 0;
    }


    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 0.22f).build();
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(!this.getIsSpawned()){
            if (event.isMoving() && !this.isAttacking()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("zombified.illager.walk", ILoopType.EDefaultLoopTypes.LOOP));
            }else if(this.isAttacking()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("zombified.illager.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("zombified.illager.idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("zombified.illager.revive", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
        }
        return PlayState.CONTINUE;

    }

    public String getIdSoul() {
        return this.entityData.get(ID_SOUL);
    }

    public void setIdSoul(String idSoul) {
        this.entityData.set(ID_SOUL, idSoul);
    }

    public boolean getIsSpawned() {
        return this.entityData.get(IS_SPAWNED);
    }

    public void setIsSpawned(boolean isSpawned) {
        this.entityData.set(IS_SPAWNED, isSpawned);
        this.animSpawnTimer = isSpawned ? 28 : 0;
        this.setInvulnerable(isSpawned);

    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
        this.attackTimer = isAttacking() ? 10 : 0;
    }

    public String getnameSoul() {
        return this.getIdSoul();
    }

    public boolean isHasSoul(){
        return this.entityData.get(HASSOUL);
    }

    public void setHasSoul(boolean pBoolean){
        this.entityData.set(HASSOUL,pBoolean);
    }

    @Override
    public void die(DamageSource pCause) {
        this.removeEntityOfList();
        super.die(pCause);
    }

    public void removeEntityOfList(){
        if(this.getOwner() instanceof INecromancerEntity entity){
            entity.getInvocations().remove(this);
        }
    }
    public void addEntityOfList(){
        if(this.getOwner() instanceof INecromancerEntity entity){
            entity.getInvocations().add(this);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasSoul",isHasSoul());
        pCompound.putBoolean("isSpawned",getIsSpawned());
        pCompound.putBoolean("isAttacking",isAttacking());
        pCompound.putString("idSoul",getIdSoul());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setHasSoul(pCompound.getBoolean("hasSoul"));
        setAttacking(pCompound.getBoolean("isAttacking"));
        setIsSpawned(pCompound.getBoolean("isSpawned"));
        setIdSoul(pCompound.getString("idSoul"));
        this.updateListOwner();

    }

    private void updateListOwner(){
        if(this.getOwner()!=null){
            if(this.getOwner() instanceof INecromancerEntity entity){
                entity.getInvocations().add(this);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING,false);
        this.entityData.define(HASSOUL,false);
        this.entityData.define(ID_SOUL,"pillager");
        this.entityData.define(IS_SPAWNED,false);
    }



    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0,new SpawnReanimatedGoal(this));
        this.targetSelector.addGoal(1,new Owner_Defend(this,false){
            @Override
            public boolean canUse() {
                return super.canUse() && !((ZombifiedEntity)this.mob).getIsSpawned();
            }
        });
        this.targetSelector.addGoal(2,new Owner_Attacking(this){
            @Override
            public boolean canUse() {
                return super.canUse() && !((ZombifiedEntity)this.mob).getIsSpawned();
            }
        });
        this.goalSelector.addGoal(3,new FollowOwnerGoalReanimate(this,1.0d,34.0f,2.0f,false));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1,new Zombiefied_Attack(this,1.1D,true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

    }
    static class Zombiefied_Attack extends MeleeAttackGoal {
        private final ZombifiedEntity goalOwner;

        public Zombiefied_Attack(ZombifiedEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && this.goalOwner.attackTimer <= 0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.goalOwner.playSound(SoundEvents.HUSK_HURT, 1.0F, 1.0F);
                this.goalOwner.doHurtTarget(entity);
                this.goalOwner.getNavigation().stop();
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            boolean flag = this.isSunBurnTick();
            if (flag) {
                ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setSecondsOnFire(8);
                }
            }
        }
        if(Event_Death.hasNameSoul(this.getnameSoul())){
            this.setHasSoul(true);
        }
        if (this.isAttacking()) {
            this.attackTimer--;
        }
        if(this.attackTimer==0 && this.isAttacking()){
            this.setAttacking(false);
        }
        if(this.getIsSpawned()){
            if(!this.level.isClientSide){
                PacketHandler.sendToAllTracking(new PacketSpawnedZombified(this),this);
            }
            this.animSpawnTimer--;
        }
        if(this.getIsSpawned() && this.animSpawnTimer==0){
            this.setIsSpawned(false);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.spawnAnim();
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void spawnAnim() {
        this.setIsSpawned(true);
        super.spawnAnim();
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller",
                0, this::predicate));
    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

   
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
        this.playSound(SoundEvents.HUSK_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.HUSK_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }

}
