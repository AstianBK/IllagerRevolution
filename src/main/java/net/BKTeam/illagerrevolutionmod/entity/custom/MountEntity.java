package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


public class MountEntity extends IllagerBeastEntity implements IHasInventory, PlayerRideableJumping, Saddleable, ContainerListener {

    private static final EntityDataAccessor<Boolean> STANDING =
            SynchedEntityData.defineId(MountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SADDLED =
            SynchedEntityData.defineId(MountEntity.class, EntityDataSerializers.BOOLEAN);
    public int sprintCounter;
    protected boolean isJumping;

    protected SimpleContainer inventory = new SimpleContainer(3);
    protected float playerJumpPendingScale;

    public boolean allowStandSliding;
    private int standCounter;



    protected MountEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }


    @Override
    public void onPlayerJump(int pJumpPower) {
        if (this.isSaddled()) {
            if (pJumpPower < 0) {
                pJumpPower = 0;
            }else {
                this.allowStandSliding = true;
                this.stand();
            }

            if (pJumpPower >= 90) {
                this.playerJumpPendingScale = 1.0F;
            } else {
                this.playerJumpPendingScale = 0.4F + 0.4F * (float)pJumpPower / 90.0F;
            }

        }
    }
    private void stand() {
        if (this.isControlledByLocalInstance() || this.isEffectiveAi()) {
            this.standCounter = 1;
            this.setStanding(true);
        }

    }

    @Override
    public boolean canJump() {
        return false;
    }

    public boolean onClimbable() {
        return false;
    }

    public boolean isPushable() {
        return !this.isVehicle();
    }

    @Override
    public void handleStartJump(int pJumpPower) {
        this.allowStandSliding = true;
        this.stand();
        //this.playJumpSound();
    }

    @Override
    public void handleStopJump() {

    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (p_21748_ != null) {
            this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }
    }
    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setIsSaddled(boolean pBoolean){
        this.entityData.set(SADDLED,pBoolean);
        if(pBoolean){
            this.equipSaddle(SoundSource.AMBIENT);
        }
    }

    public void travel(Vec3 pTravelVector) {
        super.travel(pTravelVector);
    }

    @javax.annotation.Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            for(Entity entity:this.getPassengers()){
                if(entity==this.getOwner()){
                    return (LivingEntity) entity;
                }
            }
        }else {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }
        return null;
    }
    @javax.annotation.Nullable
    private Vec3 getDismountLocationInDirection(Vec3 p_30562_, LivingEntity p_30563_) {
        double d0 = this.getX() + p_30562_.x;
        double d1 = this.getBoundingBox().minY;
        double d2 = this.getZ() + p_30562_.z;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(Pose pose : p_30563_.getDismountPoses()) {
            blockpos$mutableblockpos.set(d0, d1, d2);
            double d3 = this.getBoundingBox().maxY + 0.75D;

            while(true) {
                double d4 = this.level.getBlockFloorHeight(blockpos$mutableblockpos);
                if ((double)blockpos$mutableblockpos.getY() + d4 > d3) {
                    break;
                }

                if (DismountHelper.isBlockFloorValid(d4)) {
                    AABB aabb = p_30563_.getLocalBoundsForPose(pose);
                    Vec3 vec3 = new Vec3(d0, (double)blockpos$mutableblockpos.getY() + d4, d2);
                    if (DismountHelper.canDismountTo(this.level, p_30563_, aabb.move(vec3))) {
                        p_30563_.setPose(pose);
                        return vec3;
                    }
                }

                blockpos$mutableblockpos.move(Direction.UP);
                if (!((double)blockpos$mutableblockpos.getY() < d3)) {
                    break;
                }
            }
        }

        return null;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity pLivingEntity) {
        Vec3 vec3 = getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)pLivingEntity.getBbWidth(), this.getYRot() + (pLivingEntity.getMainArm() == HumanoidArm.RIGHT ? 90.0F : -90.0F));
        Vec3 vec31 = this.getDismountLocationInDirection(vec3, pLivingEntity);
        if (vec31 != null) {
            return vec31;
        } else {
            Vec3 vec32 = getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)pLivingEntity.getBbWidth(), this.getYRot() + (pLivingEntity.getMainArm() == HumanoidArm.LEFT ? 90.0F : -90.0F));
            Vec3 vec33 = this.getDismountLocationInDirection(vec32, pLivingEntity);
            return vec33 != null ? vec33 : this.position();
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    public void tick() {
        super.tick();
        if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }

        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }

        if (this.isStanding()) {
        } else {
            this.allowStandSliding = false;
        }
    }

    protected void doPlayerRide(Player pPlayer) {
        this.setSitting(false);
        this.setStanding(false);
        if (!this.level.isClientSide) {
            pPlayer.setYRot(this.getYRot());
            pPlayer.setXRot(this.getXRot());
            pPlayer.startRiding(this);
        }

    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STANDING, false);
        this.entityData.define(SADDLED,false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("isSaddled",this.isSaddled());
        pCompound.putBoolean("isStanding",this.isStanding());
        if (!this.inventory.getItem(0).isEmpty()) {
            pCompound.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
        }

    }
    public void attackC(Player player){
    }

    public void attackV(Player player){

    }

    public void attackG(Player player){

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setIsSaddled(pCompound.getBoolean("isSaddled"));
        this.setStanding(pCompound.getBoolean("isStanding"));
        this.updateContainerEquipment();
    }
    public void setStanding(boolean pBoolean){
        this.entityData.set(STANDING,pBoolean);
    }
    public boolean isStanding(){
        return this.entityData.get(STANDING);
    }

    public boolean isJumping(){
        return this.isJumping;
    }
    public double getCustomJump() {
        return this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    public void setIsJumping(boolean pBoolean){
        this.isJumping=pBoolean;
    }
    public void containerChanged(Container pInvBasic) {
        boolean flag = this.isSaddled();
        this.updateContainerEquipment();
        if (this.tickCount > 20 && !flag && this.isSaddled()) {
            this.playSound(SoundEvents.HORSE_SADDLE, 0.5F, 1.0F);
        }

    }
}