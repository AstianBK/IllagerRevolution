package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.api.IRelatedEntity;
import net.BKTeam.illagerrevolutionmod.entity.goals.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

public class FallenKnight extends ReanimatedEntity implements IAnimatable, IHasInventory {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory=new SimpleContainer(1);
    public int attackTimer;
    public int unarmedTimer;
    public int rearmedTimer;
    public int reviveTimer;
    public int dispawnTimer;
    public boolean isEndless;
    private static final EntityDataAccessor<Boolean> LINKED =
            SynchedEntityData.defineId(FallenKnight.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(FallenKnight.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> UNARMED =
            SynchedEntityData.defineId(FallenKnight.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ON_GROUND_UNARMED =
            SynchedEntityData.defineId(FallenKnight.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> REARMED =
            SynchedEntityData.defineId(FallenKnight.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARMED=
            SynchedEntityData.defineId(FallenKnight.class,EntityDataSerializers.BOOLEAN);

    public FallenKnight(EntityType<? extends Monster> p_33570_, Level p_33571_) {
        super(p_33570_, p_33571_);
        this.dispawnTimer=0;
        this.attackTimer=0;
        this.unarmedTimer=0;
        this.rearmedTimer=0;
        this.reviveTimer=0;
        this.isEndless=false;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ARMOR,25.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 45.D)
                .add(Attributes.MOVEMENT_SPEED, 0.25f).build();
    }
    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    public void setDispawnTimer(int dispawnTimer,@Nullable Player player,boolean isInfinite) {
        if(!isInfinite){
            this.dispawnTimer=dispawnTimer;
            this.isEndless=true;
        }
    }

    public int getDispawnTimer() {
        return this.dispawnTimer;
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(this.level.random.nextFloat() < 0.5 ? Items.STONE_SWORD : Items.STONE_AXE));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0,new UnarmedFallenGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2,new FallenKnightAttack(this,1.5D,true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(4, new RandomLookAroundFallenKnightGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(1,new Owner_Defend(this,false){
            @Override
            public boolean canUse() {
                if(this.mob instanceof FallenKnight fallenKnight){
                    return super.canUse() && fallenKnight.isArmed();
                }
                return super.canUse();
            }
        });
        this.targetSelector.addGoal(2,new Owner_Attacking(this){
            @Override
            public boolean canUse() {
                if(this.mob instanceof FallenKnight fallenKnight){
                    return super.canUse() && fallenKnight.isArmed();
                }
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(3,new FollowOwnerGoalReanimate(this,1.0d,10.0f,3.0f,false));
    }
    public boolean isArmed(){
        return this.entityData.get(ARMED);
    }

    public void setIsArmed(boolean b){
        this.entityData.set(ARMED,b);
        this.reviveTimer= !b ? 200 : 0;
        if(b){
            this.setInvulnerable(false);
        }
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setIsAttacking(boolean pBoolean){
        this.entityData.set(ATTACKING,pBoolean);
        this.attackTimer= pBoolean ? 10 : 0;
    }

    public LivingEntity getLinkOwner(){
        if(this.getIdOwner()!=null){
            return this.itIsLinked() ? this.getOwner() : null;
        }
        return null;
    }

    public boolean isOnGroundUnarmed() {
        return this.entityData.get(ON_GROUND_UNARMED);
    }

    public void setOnGroundUnarmed(boolean b){
        this.entityData.set(ON_GROUND_UNARMED,b);
        this.reviveTimer= b ? 200 : 0;
    }

    public boolean isUnarmed() {
        return this.entityData.get(UNARMED);
    }

    public void setUnarmed(boolean b){
        this.entityData.set(UNARMED,b);
        this.unarmedTimer= b ? 10 : 0;
    }
    public boolean itIsLinked() {
        return this.entityData.get(LINKED);
    }

    public void setLink(boolean b){
        this.entityData.set(LINKED,b);
    }

    private void setIsRearmed(boolean b) {
        this.entityData.set(REARMED,b);
        this.rearmedTimer= b ? 30 : 0;
    }

    public boolean isRearmed() {
        return this.entityData.get(REARMED);
    }
    @Override
    public void aiStep() {
        if(this.isEndless && this.dispawnTimer>0){
            this.dispawnTimer--;
        }
        if (this.dispawnTimer==0 && this.isEndless){
            if(this.getOwner() instanceof IRelatedEntity entity && this.itIsLinked()){
                entity.getBondedMinions().remove(this);
            }
            this.hurt(DamageSource.MAGIC.bypassMagic().bypassArmor(),this.getMaxHealth());
        }
        if(this.isAttacking()){
            this.attackTimer--;
        }
        if(this.attackTimer==0 && this.isAttacking()){
            this.setIsAttacking(false);
        }
        if(this.isUnarmed()){
            if(this.getOwner() instanceof IRelatedEntity entity && this.itIsLinked()){
                entity.getBondedMinions().remove(this);
            }
            this.unarmedTimer--;
        }
        if(this.unarmedTimer==0 && this.isUnarmed()){
            this.setUnarmed(false);
            this.setOnGroundUnarmed(true);
        }
        if(this.isOnGroundUnarmed()){
            this.reviveTimer--;
        }
        if(this.reviveTimer==0 && this.isOnGroundUnarmed()){
            this.setOnGroundUnarmed(false);
            this.setIsRearmed(true);
        }
        if(this.isRearmed()){
            this.rearmedTimer--;
        }
        if (this.rearmedTimer==0 && this.isRearmed()){
            this.setIsRearmed(false);
            this.setIsArmed(true);
            this.heal(this.getMaxHealth());
            if(this.getOwner() instanceof IRelatedEntity entity && this.itIsLinked()){
                entity.getBondedMinions().add(this);
            }
        }
        super.aiStep();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("isUnarmed",this.isUnarmed());
        pCompound.putBoolean("isAttacking",this.isAttacking());
        pCompound.putBoolean("isRearmed",this.isRearmed());
        pCompound.putBoolean("isArmed",this.isArmed());
        pCompound.putBoolean("isOnGround",this.isOnGroundUnarmed());
        pCompound.putBoolean("ItIsLinked",this.itIsLinked());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setIsRearmed(pCompound.getBoolean("isRearmed"));
        this.setUnarmed(pCompound.getBoolean("isUnarmed"));
        this.setIsAttacking(pCompound.getBoolean("isAttacking"));
        this.setIsArmed(pCompound.getBoolean("isArmed"));
        this.setOnGroundUnarmed(pCompound.getBoolean("isOnGround"));
        this.setLink(pCompound.getBoolean("ItIsLinked"));
        this.updateListLinked();
        this.updateTimerDispawn();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING,false);
        this.entityData.define(UNARMED,false);
        this.entityData.define(REARMED,false);
        this.entityData.define(ARMED,true);
        this.entityData.define(ON_GROUND_UNARMED,false);
        this.entityData.define(LINKED,false);
    }

    private void updateListLinked(){
        if(this.getOwner()!=null){
            if(this.itIsLinked()){
                if(this.getOwner() instanceof IRelatedEntity entity){
                    entity.getBondedMinions().add(this);
                }
            }
        }
        if(this.getIdNecromancer()!=null){
            if(this.getNecromancer() instanceof Blade_KnightEntity bk){
                bk.getKnights().add(this);
            }
        }
    }
    private void updateTimerDispawn(){
        if(this.getOwner()!=null){
            this.dispawnTimer=300;
            this.isEndless=true;
        }
    }
    private <E extends IAnimatable>PlayState predicate(AnimationEvent<E> event) {
        if(this.isArmed()){
            if (event.isMoving() && !this.isAttacking() && !this.isAggressive()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.walk1", ILoopType.EDefaultLoopTypes.LOOP));
            }else if(event.isMoving() && this.isAggressive() && !this.isAttacking()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.walk2", ILoopType.EDefaultLoopTypes.LOOP));
            } else if (this.isAttacking()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.attack1", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }else   {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }else{
            if(this.isUnarmed() && !this.isOnGroundUnarmed()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.death1", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            } else if (this.isRearmed() && !this.isOnGroundUnarmed()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.revive1", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            }else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallenknight.death2", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }

        return PlayState.CONTINUE;
    }


    static class FallenKnightAttack extends MeleeAttackGoal {
        private final FallenKnight goalOwner;

        public FallenKnightAttack(FallenKnight entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && this.goalOwner.attackTimer <= 0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.goalOwner.playSound(SoundEvents.SKELETON_HURT, 1.0F, 4.0F);
                this.goalOwner.doHurtTarget(entity);
                this.goalOwner.getNavigation().stop();
            }
        }

        @Override
        public boolean canUse() {
            return super.canUse()  && this.goalOwner.isArmed();
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setIsAttacking(true);
        }

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
