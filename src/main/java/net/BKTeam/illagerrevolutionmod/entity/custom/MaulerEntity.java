package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
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

import java.util.Arrays;
import java.util.Comparator;

public class MaulerEntity extends MountEntity implements IAnimatable {

    public final AnimationFactory factory= GeckoLibUtil.createFactory(this);
    private static final EntityDataAccessor<Integer> ID_VARIANT =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> MAULED =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);
    public int attackTimer;
    public int mauledTimer;

    private int mauledAttackTimer;

    public MaulerEntity(EntityType<? extends MountEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.attackTimer=0;
        this.mauledTimer=0;
        this.mauledAttackTimer=0;
    }
    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 13.0D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.41f)
                .add(Attributes.JUMP_STRENGTH,0.70d)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2,new MaulerAttackGoal(this,1.2d,true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        super.registerGoals();
    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !isAggressive() && !this.isSitting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.walk"+(!this.isVehicle() ? "1" : "2"), ILoopType.EDefaultLoopTypes.LOOP));
        }else if(event.isMoving() && this.isAggressive() && !this.isSitting() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isSitting() && this.isTame()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.sit", ILoopType.EDefaultLoopTypes.LOOP));
        }else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    private  <E extends IAnimatable> PlayState predicateHead(AnimationEvent<E> event) {
        if (this.isMauled()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.attack2", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
        }else {
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;
    }

    private  <E extends IAnimatable> PlayState predicateAttack(AnimationEvent<E> event) {
        if(this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.attack1", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
        }else {
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;
    }
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.BEEF);
    }

    @Override
    public double getPassengersRidingOffset() {
        return ((double)this.getBbHeight());
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.isVehicle()){
            for (Entity entity : this.getPassengers()){
                if(entity==pSource.getEntity()){
                    return false;
                }
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void attackC(Player player) {
        if(this.attackTimer<=0 && !this.isMauled()){
            if(this.getPassengers().size()<2){
                boolean flag=false;
                this.setAttacking(true);
                this.level.broadcastEntityEvent(this, (byte)4);
                this.level.playSound(player,this.getOnPos(),SoundEvents.WOLF_HURT, SoundSource.HOSTILE,1.0f,1.0f);
                float f = this.yBodyRot * ((float)Math.PI / 180F);
                float f1 = Mth.sin(f);
                float f2 = Mth.cos(f);
                float f3 = 0.5f;
                BlockPos pos = new BlockPos(this.getX()-(f3*f1),this.getY()+1.5d,this.getZ()+(f3*f2));
                for(LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class,new AABB(pos).inflate(2.5d))){
                    if(living!=this && living!=player && !flag){
                        flag=true;
                        this.catchedTarget(living);
                        living.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                    }else if(flag){
                        break;
                    }
                }
            }else {
                this.setAttacking(true);
                this.level.broadcastEntityEvent(this, (byte)4);
                this.level.playSound(player,this.getOnPos(),SoundEvents.WOLF_HURT, SoundSource.HOSTILE,1.0f,1.0f);
                if(this.getCatchedEntity()!=null){
                    this.getCatchedEntity().addEffect(new MobEffectInstance(InitEffect.MAULED.get(),100,0));
                    this.releaseTarget(this.getCatchedEntity());
                }
            }
        }
        super.attackC(player);
    }

    @Override
    public void attackG(Player player) {
        if(!this.level.isClientSide){
            if(this.mauledTimer<=0){
                this.setIsMauled(true);
            }
        }
        super.attackG(player);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot){
        switch (pSlot.getType()){
            case ARMOR :
                return this.inventory.getItem(pSlot.getIndex());
            default:
                return super.getItemBySlot(pSlot);
        }
    }

    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack legs= this.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack legs1= this.getItemBySlot(EquipmentSlot.LEGS);
        if(this.tickCount >20){
            if ((this.isArmor(legs1) && legs!=legs1)){
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);
                this.updateContainerEquipment();
            }
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack itemStack) {
        switch (slotIn.getType()){
            case ARMOR :
                this.inventory.setItem(slotIn.getIndex(),itemStack);
            default:
                super.setItemSlot(slotIn,itemStack);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.isBaby()) {
            if (this.isTame() && pPlayer.isSecondaryUseActive()) {
                this.setSitting(!this.isSitting());
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (this.isVehicle()) {
                return super.mobInteract(pPlayer, pHand);
            }
        }
        if (!itemstack.isEmpty() && !pPlayer.isSecondaryUseActive()) {
            if(itemstack.getItem() instanceof DyeItem dyeItem){
                this.setPainted(true);
                if (dyeItem.getDyeColor()!=this.getColor()){
                    this.setColor(dyeItem.getDyeColor());
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            else if(itemstack.is(Items.WATER_BUCKET) && this.isPainted()){
                this.setPainted(false);
                this.setColor(DyeColor.WHITE);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    itemstack=new ItemStack(Items.BUCKET);
                    pPlayer.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
                    pPlayer.setItemSlot(EquipmentSlot.MAINHAND,itemstack);
                }
                return InteractionResult.CONSUME;
            }
            if(itemstack.is(Items.BONE)){
                if(pPlayer instanceof IOpenBeatsContainer){
                    this.openInventory(pPlayer);
                    this.gameEvent(GameEvent.ENTITY_INTERACT, pPlayer);
                    this.updateContainerEquipment();
                    return InteractionResult.SUCCESS;
                }
            }
            if (this.isFood(itemstack)) {
                if(!this.isTame()){
                    if (this.level.isClientSide) {
                        return InteractionResult.CONSUME;
                    } else {
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        if (!ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                            if (!this.level.isClientSide) {
                                super.tame(pPlayer);
                                this.navigation.recomputePath();
                                this.setTarget(null);
                                this.level.broadcastEntityEvent(this, (byte)7);
                                this.setSitting(true);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
                //return this.fedFood(pPlayer, itemstack);
            }
            boolean flag = !this.isSaddled() && itemstack.is(Items.SADDLE);
            if (this.isArmor(itemstack) || flag) {
                this.setIsSaddled(true);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
            InteractionResult interactionresult = itemstack.interactLivingEntity(pPlayer, this, pHand);
            if (interactionresult.consumesAction()) {
                return interactionresult;
            }
        }

        if (this.isBaby() || !this.isTame()) {
            return super.mobInteract(pPlayer, pHand);
        } else{
            this.doPlayerRide(pPlayer);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
    }

    public void catchedTarget(LivingEntity entity){
        if(entity!=null && this.canAddPassenger(entity)){
            if (!this.level.isClientSide) {
                entity.startRiding(this);
            }
            if(this.level.isClientSide){
                entity.setPose(Pose.SLEEPING);
            }
        }
    }

    public void releaseTarget(LivingEntity entity){
        if(entity!=null && !this.canAddPassenger(entity)){
            if (!this.level.isClientSide) {
                entity.stopRiding();
            }
        }
    }

    public LivingEntity getCatchedEntity(){
        return this.getPassengers().size()>1? (LivingEntity) this.getPassengers().get(1) : null;
    }

    public void aiStep() {
        super.aiStep();
        LivingEntity target = this.getCatchedEntity();
        if (this.isAttacking()) {
            this.attackTimer--;
        }
        if(this.attackTimer==0){
            this.setAttacking(false);
        }
        if(this.isMauled()){
            this.mauledTimer--;
            this.mauledAttackTimer--;
        }
        if(this.mauledAttackTimer<0 && this.isMauled()){
            this.mauledAttackTimer=10;
            if(target!=null && target.isAlive()){
                target.hurt(DamageSource.mobAttack(this),5.0f);
            }
        }
        if(this.mauledTimer<0){
            this.setIsMauled(false);
        }
    }

    public void setAttacking(boolean pBoolean) {
        this.entityData.set(ATTACKING,pBoolean);
        this.attackTimer=pBoolean ? 20 : 0;
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
            if (this.isVehicle() && livingentity != null && this.isTame() && !this.isSitting() && this.isSaddled()) {
                this.setYRot(livingentity.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(livingentity.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float f = livingentity.xxa * 0.5F;
                float f1 = livingentity.zza;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                if (this.onGround && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
                    f = 0.0F;
                    f1 = 0.0F;
                }

                if (this.playerJumpPendingScale > 0.0F && !this.isJumping() && this.onGround) {
                    double d0 = this.getCustomJump() * (double) this.playerJumpPendingScale * (double) this.getBlockJumpFactor();
                    double d1 = d0 + this.getJumpBoostPower();
                    Vec3 vec3 = this.getDeltaMovement();
                    this.setDeltaMovement(vec3.x, d1, vec3.z);
                    this.setIsJumping(true);
                    this.hasImpulse = true;
                    net.minecraftforge.common.ForgeHooks.onLivingJump(this);
                    if (f1 > 0.0F) {
                        float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                        float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                        this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * this.playerJumpPendingScale), 0.0D, (double) (0.4F * f3 * this.playerJumpPendingScale)));
                    }

                    this.playerJumpPendingScale = 0.0F;
                }

                this.flyingSpeed = this.getSpeed() * 0.1F;
                if (this.isControlledByLocalInstance()) {
                    this.setSpeed(this.hasCatched() ?(float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)-0.1f:(float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)-0.25f);
                    super.travel(new Vec3((double) f, pTravelVector.y, (double) f1));
                } else if (livingentity instanceof Player) {
                    this.setDeltaMovement(Vec3.ZERO);
                }

                if (this.onGround) {
                    this.playerJumpPendingScale = 0.0F;
                    this.setIsJumping(false);
                }
                this.calculateEntityAnimation(this, false);
                this.tryCheckInsideBlocks();
            } else {
                this.flyingSpeed = 0.02F;
                super.travel(pTravelVector);
            }
        }
    }
    public boolean hasCatched(){
        return this.getPassengers().size()<2;
    }

    public void positionRider(Entity pPassenger) {
        super.positionRider(pPassenger);
        if(pPassenger==this.getOwner() || this.getControllingPassenger()==pPassenger){
            if (pPassenger instanceof Mob mob) {
                this.yBodyRot = mob.yBodyRot;
            }
            float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            float f1 = 0.30F;
            float f2 = 0.15F;
            pPassenger.setPos(this.getX() + (double)(f1 * f3), this.getY()+ this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset() - (double)f2, this.getZ() - (double)(f1 * f));
            ((LivingEntity)pPassenger).yBodyRot = this.yBodyRot;

        }else {
            float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            float f1 = 1.7F;
            pPassenger.setPos(this.getX() - (f3*f1), this.getY()+ this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset()-1.0d, this.getZ() + (f*f1));
            if (pPassenger instanceof LivingEntity) {
                ((LivingEntity)pPassenger).yBodyRot = this.yBodyRot;
            }
        }
    }

    public void openInventory(Player player) {
        MaulerEntity mauler = (MaulerEntity) ((Object) this);
        if (!this.level.isClientSide && player instanceof IOpenBeatsContainer) {
            ((IOpenBeatsContainer)player).openMaulerInventory(mauler, this.inventory);
        }
    }

    public boolean canBeLeashed(@NotNull Player player) {
        return super.canBeLeashed(player);
    }

    @Override
    public void setTame(boolean tamed) {
        super.setTame(tamed);
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.40f);
        }
    }

    public boolean isArmor(ItemStack stack){
        return stack.getItem() instanceof BeastArmorItem;
    }
    public int getTypeIdVariant(){
        return this.entityData.get(ID_VARIANT);
    }

    public Variant getIdVariant(){
        return Variant.byId(this.getTypeIdVariant() & 255);
    }

    public void setIdVariant(int pId){
        this.entityData.set(ID_VARIANT,pId);
    }



    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Attacking",this.isAttacking());
        pCompound.putInt("Variant", this.getTypeIdVariant());
        pCompound.putBoolean("isMauled",this.isMauled());
        ItemStack itemStackHead = this.getItemBySlot(EquipmentSlot.LEGS);
        if(!itemStackHead.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            itemStackHead.save(headCompoundNBT);
            pCompound.put("ChestMaulerArmor", headCompoundNBT);
        }
    }

    public boolean isMauled() {
        return this.entityData.get(MAULED);
    }

    public void setIsMauled(boolean pBoolean){
        this.entityData.set(MAULED,pBoolean);
        this.mauledTimer= pBoolean ? 40 : 0 ;
        this.mauledAttackTimer = pBoolean ? 10 : 0;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if(this.random.nextFloat()>0.99){
            this.setIdVariant(4);
        }else {
            this.setIdVariant(this.random.nextInt(0,3));
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAttacking(pCompound.getBoolean("Attacking"));
        this.setIdVariant(pCompound.getInt("Variant"));
        this.setIsMauled(pCompound.getBoolean("isMauled"));
        CompoundTag compoundNBT = pCompound.getCompound("ChestMaulerArmor");
        if(!compoundNBT.isEmpty()) {
            if(this.isArmor(ItemStack.of(pCompound.getCompound("ChestMaulerArmor")))){
                ItemStack stack=ItemStack.of(pCompound.getCompound("ChestMaulerArmor"));
                this.setItemSlot(EquipmentSlot.LEGS,stack);
            }
        }
        this.updateContainerEquipment();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_VARIANT, 0);
        this.entityData.define(ATTACKING,false);
        this.entityData.define(MAULED,false);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<MaulerEntity>(this, "controller_attack",
                0, this::predicateAttack));
        data.addAnimationController(new AnimationController<MaulerEntity>(this, "controller_mauled",
                10, this::predicateHead));
        data.addAnimationController(new AnimationController<MaulerEntity>(this, "controller_body",
                10, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public boolean hasInventoryChanged(Container container){
        return this.inventory!=container;
    }

    static class MaulerAttackGoal extends MeleeAttackGoal {
        private final MaulerEntity goalOwner;

        public MaulerAttackGoal(MaulerEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && this.goalOwner.attackTimer <= 0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.goalOwner.playSound(SoundEvents.WOLF_HURT, 1.2F, -3.0F);
                this.goalOwner.doHurtTarget(entity);
            }
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !(this.goalOwner.isTame() && this.goalOwner.isVehicle());
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);
        }

    }

    public enum Variant {
        BROWN(0),
        BLONDE(1),
        GINGER(2),
        BLUE(3),
        ALBINO(4);

        private static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final int id;

        Variant(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static Variant byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }
    }

}
