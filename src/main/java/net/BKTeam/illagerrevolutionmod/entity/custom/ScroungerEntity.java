package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ScroungerEntity extends IllagerBeastEntity implements IAnimatable, FlyingAnimal, IHasInventory {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;

    private int animThrow;
    private final SimpleContainer inventory = new SimpleContainer(7);

    private static final EntityDataAccessor<Boolean> THROW_ITEM =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ID_POTION_INTENT =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.INT);

    public ScroungerEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.animThrow=0;
        this.moveControl=new FlyingMoveControl(this,20,true);
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 13.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 0.42f)
                .add(Attributes.FLYING_SPEED,0.45D)
                .build();

    }

    private   <E extends IAnimatable> PlayState predicateAttack(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    private   <E extends IAnimatable> PlayState predicateMaster(AnimationEvent<E> event) {
        if(event.isMoving() && this.isThrowItem()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.fly", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isThrowItem()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.attack1", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else if(this.isSitting()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.idle1", ILoopType.EDefaultLoopTypes.LOOP));
        }else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.idle"+(!this.isFlying() ? "1" : "2"), ILoopType.EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
    }
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3,new ScroungerWanderGoal(this,0.30D));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        //this.goalSelector.addGoal(2, new ThrownPotionGoal(this));
        this.goalSelector.addGoal(1,new FollowOwnerGoal(this,1.5d,2.0F,1.0F,true));
        this.targetSelector.addGoal(1,new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2,new OwnerHurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isThrowItem()){
            this.animThrow--;
        }
        if(this.animThrow==9){
            if(this.getOwner()!=null){
                this.shootPotion(this.getOwner(),this.getContainer().getItem(1));
            }
        }

        if(this.animThrow<0){
            this.setIsThrowItem(false,1);
        }

        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (float)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapping * 2.0F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand pHand) {
        ItemStack itemstack = player.getItemInHand(pHand);
        if(!itemstack.isEmpty()){
            if(itemstack.getItem() instanceof DyeItem dyeItem){
                this.setPainted(true);
                if (dyeItem.getDyeColor()!=this.getColor()){
                    this.setColor(dyeItem.getDyeColor());
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    playSound(SoundEvents.INK_SAC_USE, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
            else if(itemstack.is(Items.WATER_BUCKET) && this.isPainted()){
                this.setPainted(false);
                this.setColor(DyeColor.WHITE);
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    itemstack=new ItemStack(Items.BUCKET);
                    player.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
                    player.setItemSlot(EquipmentSlot.MAINHAND,itemstack);
                }
                playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
            if(itemstack.is(Items.SPIDER_EYE)){
                if (this.level.isClientSide) {
                    return InteractionResult.CONSUME;
                } else {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (!ForgeEventFactory.onAnimalTame(this, player)) {
                        if (!this.level.isClientSide) {
                            super.tame(player);
                            this.navigation.recomputePath();
                            this.setTarget(null);
                            this.level.broadcastEntityEvent(this, (byte)7);
                            this.setSitting(true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if(itemstack.is(Items.BONE)){
                this.openInventory(player);
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
                this.updateContainerEquipment();
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, pHand);
        }else {
            if(player.isSecondaryUseActive()){
                this.setSitting(!this.isSitting());
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, pHand);
    }

    @Override
    protected void updateContainerEquipment() {
        if(!this.level.isClientSide){
            this.updateInventory();
            if(!this.getContainer().getItem(2).isEmpty()){
                ItemStack stack=this.getContainer().getItem(2).copy();
                this.getContainer().setItem(1,stack);
            }
        }
        super.updateContainerEquipment();
    }
    private void updateInventory(){
        for(int i=0;i<7;i++){
            this.getContainer().setItem(i,this.getContainer().getItem(i));
        }
    }
    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack chest=this.getContainer().getItem(0);
        this.updateContainerEquipment();
        ItemStack chest1=this.getContainer().getItem(0);
        if(this.tickCount>20 && chest1!=chest && this.isArmor(chest1)){
            this.playSound(SoundEvents.HORSE_SADDLE);
        }
    }

    private boolean isArmor(ItemStack chest1) {
        return chest1.getItem() instanceof BeastArmorItem beastArmor && beastArmor.getBeast() == Beast.SCROUNGER;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isThrowItem",this.isThrowItem());
        ItemStack chest=this.getContainer().getItem(0);
        if(!chest.isEmpty()){
            CompoundTag nbt=new CompoundTag();
            chest.save(nbt);
            compound.put("chestItem",nbt);
        }
        for(int i=2;i<this.getContainer().getContainerSize();i++){
            ItemStack potion=this.getContainer().getItem(i);
            if(!potion.isEmpty()){
                CompoundTag nbt=new CompoundTag();
                potion.save(nbt);
                compound.put("potion"+i,nbt);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIsThrowItem(compound.getBoolean("isThrowItem"),1);
        CompoundTag nbtChest=compound.getCompound("chestItem");
        if(!nbtChest.isEmpty()){
            ItemStack stack = ItemStack.of(nbtChest);
            if(!stack.isEmpty()){
                this.getContainer().setItem(0,stack);
            }
        }
        if(!this.getContainer().getItem(0).isEmpty()){
            for (int i=2;i<this.getContainer().getContainerSize();i++){
                CompoundTag nbtPotion=compound.getCompound("potion"+i);
                if(!nbtPotion.isEmpty()){
                    ItemStack potion= ItemStack.of(nbtPotion);
                    if(!potion.isEmpty()){
                        this.getContainer().setItem(i,potion);
                    }
                }
            }
        }
        this.updateContainerEquipment();
    }

    public void openInventory(Player player) {
        ScroungerEntity scrounger = (ScroungerEntity) ((Object) this);
        if (!this.level.isClientSide && player instanceof IOpenBeatsContainer) {
            ((IOpenBeatsContainer)player).openScroungerInventory(scrounger, this.inventory);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(THROW_ITEM,false);
        this.entityData.define(ID_POTION_INTENT,0);
        super.defineSynchedData();
    }

    @Override
    public Beast getTypeBeast() {
        return Beast.SCROUNGER;
    }

    public boolean isThrowItem(){
        return this.entityData.get(THROW_ITEM);
    }

    public void setIsThrowItem(boolean pBoolean,int pIdSlot){
        this.entityData.set(THROW_ITEM,pBoolean);
        this.setNextPotion(pIdSlot);
        this.animThrow=pBoolean ? 20 : 0;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<ScroungerEntity>(this, "controller_attack",
                0, this::predicateAttack));
        data.addAnimationController(new AnimationController<ScroungerEntity>(this, "controller_body",
                10, this::predicateMaster));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }
    public PotionIntent getIdPotionIntent(){
        return ScroungerEntity.PotionIntent.byId(this.getPotionIntent() & 255);
    }

    public void nextIntent(){
        LivingEntity owner = this.getOwner();
        LivingEntity target = this.getTarget();
        if(owner!=null){
            float healt=owner.getHealth();
            float maxHealt=owner.getMaxHealth();
            if(target!=null){
                if(target.getMaxHealth()*100*target.getHealth()>maxHealt*100*healt){
                    this.setIdPotionIntent(1);
                }
            }
            if(maxHealt*0.30<healt){
                this.setIdPotionIntent(0);
            }
        }
    }

    public int getPotionIntent(){
        return this.entityData.get(ID_POTION_INTENT);
    }

    public ItemStack getNextPotion(){
        PotionIntent intent=this.getIdPotionIntent();
        if(intent==PotionIntent.BUFF_OWNER){
            if(this.getOwner()!=null){
                return this.getContainer().getItem(2);
            }
        }
        return null;
    }

    public int getSlotIdItemStack(ItemStack stack){
        for(int i=2;i<7;i++){
            if(this.getContainer().getItem(i)==stack){
                return i;
            }
        }
        return -1;
    }
    public void ordenThrow(){
        if(!this.isThrowItem() && this.isFlying() && !this.isSitting() && !this.getContainer().getItem(0).isEmpty()){
            if(this.level.random.nextFloat()>0.5F){
                this.nextIntent();
                ItemStack stack=this.getNextPotion();
                int cc=this.getSlotIdItemStack(stack);
                if(cc!=-1){
                    this.setIsThrowItem(true,cc);
                }
            }
        }
    }

    public void setNextPotion(int pIdSlot){
        ItemStack stack = this.getContainer().getItem(pIdSlot).copy();
        this.getContainer().setItem(pIdSlot,ItemStack.EMPTY);
        this.updateInventory();
        this.getContainer().setItem(1,stack);
    }

    public void shootPotion(LivingEntity pTarget,ItemStack potion){
        if(potion!=null){
            Vec3 vec3 = pTarget.getDeltaMovement();
            double d0 = pTarget.getX() + vec3.x - this.getX();
            double d1 = pTarget.getEyeY() - (double)1.1F - this.getY();
            double d2 = pTarget.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            ThrownPotion potionItem =new ThrownPotion(this.level,this);
            potionItem.setItem(potion);
            potionItem.setXRot(potionItem.getXRot() - -20.0F);
            potionItem.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);

            this.level.addFreshEntity(potionItem);
        }
    }
    private void setIdPotionIntent(int i) {
        this.entityData.set(ID_POTION_INTENT,i);
    }

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    public boolean hasInventoryChanged(Container maulerContainer) {
        return this.inventory!=maulerContainer;
    }

    public static class ThrownPotionGoal extends Goal{

        private final ScroungerEntity scrounger;
        ThrownPotionGoal(ScroungerEntity scrounger){
            this.scrounger=scrounger;
        }

        @Override
        public boolean canUse() {
            return !this.scrounger.isThrowItem() && !this.scrounger.isSitting();
        }

        @Override
        public void start() {

        }
    }
    static class ScroungerWanderGoal extends WaterAvoidingRandomFlyingGoal {
        public ScroungerWanderGoal(PathfinderMob p_186224_, double p_186225_) {
            super(p_186224_, p_186225_);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.mob.isOnGround();
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vec3 = null;
            if (this.mob.isInWater()) {
                vec3 = LandRandomPos.getPos(this.mob, 15, 15);
            }

            if (this.mob.getRandom().nextFloat() >= this.probability) {
                vec3 = this.getTreePos();
            }

            return vec3 == null ? super.getPosition() : vec3;
        }

        @Nullable
        private Vec3 getTreePos() {
            BlockPos blockpos = this.mob.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

            for(BlockPos blockpos1 : BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0D), Mth.floor(this.mob.getY() - 6.0D), Mth.floor(this.mob.getZ() - 3.0D), Mth.floor(this.mob.getX() + 3.0D), Mth.floor(this.mob.getY() + 6.0D), Mth.floor(this.mob.getZ() + 3.0D))) {
                if (!blockpos.equals(blockpos1)) {
                    BlockState blockstate = this.mob.level.getBlockState(blockpos$mutableblockpos1.setWithOffset(blockpos1, Direction.DOWN));
                    boolean flag = blockstate.getBlock() instanceof LeavesBlock || blockstate.is(BlockTags.LOGS);
                    if (flag && this.mob.level.isEmptyBlock(blockpos1) && this.mob.level.isEmptyBlock(blockpos$mutableblockpos.setWithOffset(blockpos1, Direction.UP))) {
                        return Vec3.atBottomCenterOf(blockpos1);
                    }
                }
            }

            return null;
        }
    }


    public enum PotionIntent{
        BUFF_OWNER(0),
        HURT_TARGET(1);

        private static final PotionIntent[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(PotionIntent::getId)).toArray(PotionIntent[]::new);
        private final int id;
        PotionIntent (int pId){
            this.id=pId;
        }

        public int getId() {
            return this.id;
        }

        public static PotionIntent byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }
    }
}
