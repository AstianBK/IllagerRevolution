package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
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
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.UUID;

public class MaulerEntity extends MountEntity implements IAnimatable {

    private static final UUID MAULER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private static final UUID MAULER_ATTACK_DAMAGE_UUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");


    public final AnimationFactory factory= GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> MAULED =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);

    public int attackTimer;

    public int mauledTimer;

    private int mauledAttackTimer;

    private int catchedTimer;

    public MaulerEntity(EntityType<? extends MountEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.attackTimer=0;
        this.mauledTimer=0;
        this.mauledAttackTimer=0;
        this.catchedTimer=0;
    }
    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.38f)
                .add(Attributes.JUMP_STRENGTH,0.60d)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1,new TemptGoal(this,1.5d,Ingredient.of(Items.ROTTEN_FLESH),false){
            @Override
            public boolean canUse() {
                return super.canUse() && ((TamableAnimal)this.mob).isTame();
            }
        });
        this.targetSelector.addGoal(1,new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2,new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1,new MaulerMauled(this));
        this.goalSelector.addGoal(2,new MaulerAttackGoal(this,1.2d,true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        super.registerGoals();
    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !isAggressive() && !this.isSitting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.walk"+(!this.isVehicle() ? "1" : "2"), ILoopType.EDefaultLoopTypes.LOOP));
        }else if(event.isMoving() && this.isAggressive() && !this.isSitting()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isSitting() && this.isTame()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.sit", ILoopType.EDefaultLoopTypes.LOOP));
        }else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    private  <E extends IAnimatable> PlayState predicateHead(AnimationEvent<E> event) {
        if (this.isMauled()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.attack2", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else {
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;
    }

    private  <E extends IAnimatable> PlayState predicateAttack(AnimationEvent<E> event) {
        if(this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.attack1", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else{
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;
    }
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.ROTTEN_FLESH);
    }

    @Override
    public double getPassengersRidingOffset() {
        return ((double)this.getBbHeight());
    }

    @Override
    public Beast getTypeBeast() {
        return Beast.MAULER;
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
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        if(this.level.random.nextFloat() > 0.85F){
            ItemStack itemStack=new ItemStack(ModItems.MAULER_PELT.get());
            itemStack.setCount(1);
            this.spawnAtLocation(itemStack);
        }
        if(this.hasArmor() || this.isSaddled()){
            for (int i = 0 ; i < this.getInventorySize() ; i++){
                ItemStack stack = this.inventory.getItem(i);
                if(!stack.isEmpty()){
                    this.spawnAtLocation(stack);
                    this.inventory.setItem(i,ItemStack.EMPTY);
                }
            }
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    public boolean hasArmor(){
        return !this.inventory.getItem(1).isEmpty() && this.isArmor(this.inventory.getItem(1));
    }

    @Override
    public void attackC() {
        if(this.attackTimer<=0 && !this.isMauled()){
            if(this.getPassengers().size()<2){
                boolean flag=false;
                this.setAttacking(true);
                this.level.broadcastEntityEvent(this, (byte) 8);
                this.level.playSound(null,this.getOnPos(),SoundEvents.WOLF_HURT, SoundSource.HOSTILE,1.0f,1.0f);
                float f = this.yBodyRot * ((float)Math.PI / 180F);
                float f1 = Mth.sin(f);
                float f2 = Mth.cos(f);
                float f3 = 0.5f;
                BlockPos pos = new BlockPos(this.getX()-(f3*f1),this.getY()+1.5d,this.getZ()+(f3*f2));
                for(LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class,new AABB(pos).inflate(2.5d))){
                    if(living!=this && living!=this.getOwner() && !flag){
                        flag=true;
                        this.catchedTarget(living);
                        living.doHurtTarget(living);
                    }else if(flag){
                        break;
                    }
                }
            }else {
                this.setAttacking(true);
                this.level.broadcastEntityEvent(this, (byte) 8);
                this.level.playSound(null,this.getOnPos(),SoundEvents.WOLF_HURT, SoundSource.HOSTILE,1.0f,1.0f);
                if(this.getCatchedEntity()!=null){
                    this.getCatchedEntity().addEffect(new MobEffectInstance(InitEffect.MAULED.get(),100,0));
                    this.releaseTarget(this.getCatchedEntity());
                }
            }
        }
        super.attackC();
    }

    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    public void attackG() {
        if(this.mauledTimer<=0 && this.attackTimer<=0){
            this.setIsMauled(true);
            this.level.broadcastEntityEvent(this, (byte) 4);
        }
        super.attackG();
    }

    public LivingEntity getCatchedEntity(){
        for(Entity entity:this.getPassengers()){
            if(entity!=this.getOwner() && entity instanceof LivingEntity){
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==4){
            this.setIsMauled(true);
        }else if(pId == 8){
            this.setAttacking(true);
        }
        super.handleEntityEvent(pId);
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
        boolean flag = this.isSaddled();
        ItemStack legs= this.getItemBySlot(EquipmentSlot.LEGS);
        this.updateContainerEquipment();
        ItemStack legs1= this.getItemBySlot(EquipmentSlot.LEGS);
        if(this.tickCount >20){
            if ((this.isArmor(legs1) && legs!=legs1) || (!flag && flag!=this.isSaddled())){
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);
            }
        }
    }

    @Override
    protected void updateContainerEquipment(){
        if (!this.level.isClientSide) {
            ItemStack stack = this.getItemBySlot(EquipmentSlot.LEGS);
            boolean flag = !stack.isEmpty();
            this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(MAULER_ATTACK_DAMAGE_UUID);
            this.getAttribute(Attributes.ARMOR).removeModifier(MAULER_ARMOR_UUID);
            if(flag) {
                double i = ((BeastArmorItem) stack.getItem()).getDamageValue();
                int d = (((BeastArmorItem) stack.getItem()).getArmorValue());
                if (i != 0) {
                    this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(MAULER_ATTACK_DAMAGE_UUID, "mauler attack bonus", i, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                if(d!=0){
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(MAULER_ARMOR_UUID,"mauler armor bonus",d, AttributeModifier.Operation.ADDITION));
                }
            }
            this.setIsSaddled(!this.getContainer().getItem(0).isEmpty());
        }
        super.updateContainerEquipment();
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
            if (this.isVehicle() && !this.canAddPassenger(pPlayer)) {
                return super.mobInteract(pPlayer, pHand);
            }
        }
        if (!itemstack.isEmpty()) {
            if(itemstack.getItem() instanceof DyeItem dyeItem){
                this.setPainted(true);
                if (dyeItem.getDyeColor()!=this.getColor()){
                    this.setColor(dyeItem.getDyeColor());
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    playSound(SoundEvents.INK_SAC_USE, 1.0F, 1.0F);
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
                playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
            if(itemstack.is(ModItems.BEAST_STAFF.get())){
                if(pPlayer instanceof IOpenBeatsContainer){
                    this.openInventory(pPlayer);
                    this.gameEvent(GameEvent.ENTITY_INTERACT, pPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
            if (this.isFood(itemstack)) {
                if(!this.isTame()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if(this.level.random.nextFloat()>0.90){
                        if (!ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                            if (!this.level.isClientSide) {
                                super.tame(pPlayer);
                                this.navigation.recomputePath();
                                this.setTarget(null);
                                this.level.broadcastEntityEvent(this, (byte)7);
                                this.setSitting(true);
                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }else if(this.getHealth()!=this.getMaxHealth()){
                    if(!pPlayer.getAbilities().instabuild){
                        itemstack.shrink(1);
                    }
                    if(!this.level.isClientSide){
                        this.heal(this.getMaxHealth()*0.10f);
                    }
                }
                return InteractionResult.CONSUME;
            }
            boolean flag = !this.isSaddled() && itemstack.is(Items.SADDLE);
            if (this.isArmor(itemstack) || flag) {
                if(itemstack.getItem() instanceof BeastArmorItem armorItem){
                    this.setItemSlot(armorItem.getEquipmetSlot(),itemstack);
                }else {
                    this.setIsSaddled(true);
                    this.inventory.setItem(0,itemstack);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.updateContainerEquipment();
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
        }
    }


    public void releaseTarget(LivingEntity entity){
        if(entity!=null && !this.canAddPassenger(entity)){
            if (!this.level.isClientSide) {
                entity.stopRiding();
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        if (this.horizontalCollision && ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
            boolean flag = false;
            AABB aabb = this.getBoundingBox().inflate(0.2D);

            for(BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                BlockState blockstate = this.level.getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (block instanceof LeavesBlock) {
                    flag = this.level.destroyBlock(blockpos, true, this) || flag;
                }
            }

            if (!flag && this.onGround) {
                this.jumpFromGround();
            }
        }
        LivingEntity target = this.getCatchedEntity();

        if (this.isAttacking()) {
            this.attackTimer--;
        }

        if(this.attackTimer==0){
            this.setAttacking(false);
        }
        if(this.catchedTimer>0){
            this.catchedTimer--;
        }
        if(this.isMauled()){
            this.mauledTimer--;
            this.mauledAttackTimer--;
            if(this.mauledTimer!=0){
                if(this.mauledAttackTimer==0){
                    this.mauledAttackTimer=10;
                    if(target!=null && target.isAlive()){
                        target.hurt(DamageSource.mobAttack(this),5.0f);
                    }
                }
            }else {
                this.setIsMauled(false);
            }
        }

    }

    public void setAttacking(boolean pBoolean) {
        this.entityData.set(ATTACKING,pBoolean);
        this.attackTimer=pBoolean ? 10 : 0;
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }
    public void travel(Vec3 pTravelVector) {
        LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
        if (this.isAlive() ) {
            if (this.isVehicle() && livingentity!=null && this.isTame() && !this.isSitting() && this.isSaddled()) {
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
                    this.setSpeed(!this.hasCatched() ?(float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)/2:(float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)/3);
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
        return this.getCatchedEntity()!=null;
    }

    public void positionRider(Entity pPassenger) {
        super.positionRider(pPassenger);
        if(pPassenger==this.getOwner() || this.getControllingPassenger()==pPassenger){
            if (pPassenger instanceof Mob mob) {
                this.yBodyRot = mob.yBodyRot;
            }
            float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            float f1 = 0.1F;
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
        return stack.getItem() instanceof BeastArmorItem armorItem && armorItem.getBeast() == Beast.MAULER;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if(this.level.random.nextFloat() > 0.75F && this.catchedTimer<=0){
            pEntity.startRiding(this);
            this.catchedTimer=400;
        }
        return super.doHurtTarget(pEntity);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Attacking",this.isAttacking());
        pCompound.putBoolean("isMauled",this.isMauled());
        ItemStack saddle = this.getContainer().getItem(0);
        ItemStack itemStackHead = this.getItemBySlot(EquipmentSlot.LEGS);
        if(!itemStackHead.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            itemStackHead.save(headCompoundNBT);
            pCompound.put("ChestMaulerArmor", headCompoundNBT);
        }
        if(!saddle.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            saddle.save(headCompoundNBT);
            pCompound.put("saddleItem", headCompoundNBT);
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

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAttacking(pCompound.getBoolean("Attacking"));
        this.setIsMauled(pCompound.getBoolean("isMauled"));
        CompoundTag compoundNBT1 = pCompound.getCompound("saddleItem");
        CompoundTag compoundNBT = pCompound.getCompound("ChestMaulerArmor");
        if(!compoundNBT.isEmpty()) {
            if(this.isArmor(ItemStack.of(compoundNBT))){
                ItemStack stack=ItemStack.of(compoundNBT);
                this.setItemSlot(EquipmentSlot.LEGS,stack);
            }
        }
        if(!compoundNBT1.isEmpty()) {
            if(ItemStack.of(compoundNBT1).is(Items.SADDLE)){
                ItemStack stack=ItemStack.of(compoundNBT1);
                this.inventory.setItem(0,stack);
            }
        }
        this.updateContainerEquipment();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
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

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (p_21748_ != null) {
            this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.MAULER_HURT.get();
    }

    @javax.annotation.Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
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

    static class MaulerMauled extends Goal {
        private final MaulerEntity mauler;

        MaulerMauled (MaulerEntity mauler){
            this.mauler=mauler;
        }

        @Override
        public void start() {
            this.mauler.setIsMauled(true);
            this.mauler.level.broadcastEntityEvent(this.mauler, (byte) 4);
            super.start();
        }

        @Override
        public boolean canUse() {
            return this.mauler.getCatchedEntity()!=null && this.mauler.level.random.nextFloat() > 0.80F && !this.mauler.isMauled();
        }
    }
}
