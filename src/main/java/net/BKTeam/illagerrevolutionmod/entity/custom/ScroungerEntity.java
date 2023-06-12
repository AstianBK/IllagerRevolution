package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.entity.projectile.ArrowBeast;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ScroungerEntity extends IllagerBeastEntity implements IAnimatable, FlyingAnimal, IHasInventory, RangedAttackMob {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;

    private int animThrow;

    private int helpOwnerTimer;

    public int nextAttack;

    LivingEntity targetIntent;

    LivingEntity ownerIllager;
    private final SimpleContainer inventory = new SimpleContainer(7);

    private static final EntityDataAccessor<Boolean> THROW_ITEM =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ID_POTION_INTENT =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.BOOLEAN);

    public ScroungerEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.animThrow=0;
        this.nextAttack=0;
        this.helpOwnerTimer=0;
        this.moveControl=new FlyingMoveControl(this,20,true);
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 1.0d)
                .add(Attributes.FLYING_SPEED,3.0D)
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
        this.goalSelector.addGoal(3,new ScroungerWanderGoal(this,1.0D));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1,new FollowOwnerGoal(this,1.5d,20.0F,10.0F,true));
        this.targetSelector.addGoal(1,new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new SearchOwnerGoal(this));
        this.targetSelector.addGoal(2, new HelpOwnerGoal(this));
        this.targetSelector.addGoal(2,new OwnerHurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isAttacking()){
            this.nextAttack--;
        }
        if(this.nextAttack<0){
            this.setIsAttacking(false);
        }
        if(this.isThrowItem()){
            this.animThrow--;
        }
        if(this.animThrow==9){
            if(this.getTargetIntent()!=null){
                this.shootPotion(this.targetIntent,this.getContainer().getItem(1));
            }
        }
        if(this.helpOwnerTimer>0){
            this.helpOwnerTimer--;
        }
        if(this.animThrow<0){
            this.setIsThrowItem(false);
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
    public LivingEntity getTargetIntent(){
        return this.targetIntent;
    }

    public void setTargetIntent(LivingEntity targetIntent) {
        this.targetIntent = targetIntent;
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
                            this.setOwnerIllager(null);
                            this.setTarget(null);
                            this.setSitting(true);
                            this.level.broadcastEntityEvent(this, (byte)7);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if(itemstack.is(ModItems.BEAST_STAFF.get())){
                this.openInventory(player);
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
                this.updateContainerEquipment();
                this.getOwner().sendSystemMessage(Component.nullToEmpty("Tiene :"+this.getContainer().getItem(0)));
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, pHand);
        }else {
            if(player.isSecondaryUseActive() && this.isTame()){
                this.setSitting(!this.isSitting());
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, pHand);
    }

    @Override
    protected void updateContainerEquipment() {
        super.updateContainerEquipment();
    }

    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack chest=this.inventory.getItem(0);
        this.updateContainerEquipment();
        ItemStack chest1=this.inventory.getItem(0);
        if(this.tickCount>20 && chest1!=chest && this.isArmor(chest1)){
            this.playSound(SoundEvents.HORSE_SADDLE);
        }
    }

    public LivingEntity getOwnerIllager() {
        return this.ownerIllager;
    }

    public void setOwnerIllager(LivingEntity ownerIllager) {
        this.ownerIllager = ownerIllager;
    }

    private boolean isArmor(ItemStack chest1) {
        return chest1.getItem() instanceof BeastArmorItem beastArmor && beastArmor.getBeast() == Beast.SCROUNGER;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isThrowItem",this.isThrowItem());
        compound.putBoolean("isAttacking",this.isAttacking());
        ItemStack itemStackChest = this.getItemBySlot(EquipmentSlot.FEET);
        if(!itemStackChest.isEmpty()) {
            CompoundTag chestCompoundNBT = new CompoundTag();
            itemStackChest.save(chestCompoundNBT);
            compound.put("ChestScroungerArmor", chestCompoundNBT);
            for(int i = 2 ; i<7;i++){
                CompoundTag nbt=new CompoundTag();
                ItemStack stack = this.inventory.getItem(i);
                stack.save(nbt);
                compound.put("Potion"+i,nbt);
            }

        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIsThrowItem(compound.getBoolean("isThrowItem"));
        this.setIsAttacking(compound.getBoolean("isAttacking"));
        CompoundTag compoundNBT = compound.getCompound("ChestScroungerArmor");
        if(!compoundNBT.isEmpty()) {
            if (this.isArmor(ItemStack.of(compound.getCompound("ChestScroungerArmor")))) {
                ItemStack stack=ItemStack.of(compound.getCompound("ChestScroungerArmor"));
                this.setItemSlot(EquipmentSlot.FEET,stack);
            }
            for (int i = 2 ; i < 7; i++){
                if(!compound.getCompound("Potion"+i).isEmpty()){
                    ItemStack stack = ItemStack.of(compound.getCompound("Potion"+i));
                    this.inventory.setItem(i,stack);
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
        this.entityData.define(ATTACKING,false);
        this.entityData.define(ID_POTION_INTENT,0);
        super.defineSynchedData();
    }

    public void setIsAttacking(boolean pBoolean){
        this.entityData.set(ATTACKING,pBoolean);
        this.nextAttack= pBoolean ? 20 : 0;
    }

    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
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
    public Beast getTypeBeast() {
        return Beast.SCROUNGER;
    }

    public boolean isThrowItem(){
        return this.entityData.get(THROW_ITEM);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        ItemStack itemStack=new ItemStack(ModItems.SCROUNGER_FEATHER.get());
        itemStack.setCount(this.level.getRandom().nextInt(1,4));
        this.spawnAtLocation(itemStack);
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    public void setIsThrowItem(boolean pBoolean){
        this.entityData.set(THROW_ITEM,pBoolean);
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
        LivingEntity owner = this.isTame() ? this.getOwner() : this.getOwnerIllager();
        LivingEntity target = this.getTarget();
        if(owner!=null){
            float healt=owner.getHealth();
            float maxHealt=owner.getMaxHealth();
            if(target!=null){
                if(target.getMaxHealth()*100*target.getHealth()>maxHealt*100*healt && this.intentPotion(PotionIntent.HURT_TARGET,target)!=null){
                    if(target.getMobType()==MobType.UNDEAD){
                        this.setIdPotionIntent(0);
                    }else {
                        this.setIdPotionIntent(2);
                    }
                    this.setTargetIntent(target);
                }
            }

            if(this.intentPotion(PotionIntent.BUFF_OWNER,owner)!=null && maxHealt*0.70F>healt){
                this.setIdPotionIntent(1);
                this.setTargetIntent(owner);
            }

            if(maxHealt*0.30F>healt && this.intentPotion(PotionIntent.HEAL_OWNER,owner)!=null){
                this.setIdPotionIntent(0);
                this.setTargetIntent(owner);
            }
        }
    }

    public int getPotionIntent(){
        return this.entityData.get(ID_POTION_INTENT);
    }
    public boolean canThrowItem(LivingEntity target,LivingEntity owner){
        double d0=this.distanceTo(target);
        double d1=target.distanceTo(owner);
        if(this.getIdPotionIntent()==PotionIntent.HURT_TARGET){
            return d0>10.0D && d1>10.0D;
        }else {
            return true;
        }
    }

    public ItemStack intentPotion(PotionIntent intent,LivingEntity pTarget){
        int r=0;
        int b=0;
        int h=0;
        if(pTarget!=null){
            for(int i=2;i<7;i++){
                ItemStack stack=this.inventory.getItem(i);
                if(!stack.isEmpty()){
                    int i1=PotionUtils.getPotion(stack).getEffects().size();
                    int i2=0;
                    for(MobEffectInstance effect : PotionUtils.getPotion(stack).getEffects()){
                        MobEffect effect1 =effect.getEffect();
                        int pAmpli=effect.getAmplifier()+1;
                        if(effect1.getCategory() == MobEffectCategory.BENEFICIAL){
                            if(effect1 == MobEffects.HEAL || effect1 == MobEffects.REGENERATION || effect1 == MobEffects.ABSORPTION){
                                r+=Math.min(pAmpli*3,15);
                            }else if(effect1 == MobEffects.FIRE_RESISTANCE || effect1 == MobEffects.DAMAGE_RESISTANCE) {
                                b+=Math.min(pAmpli*2,15);
                            }else {
                                b+=Math.min(pAmpli,15);
                            }
                        }else if (effect1.getCategory() == MobEffectCategory.HARMFUL){
                            if(effect1 == MobEffects.HARM){
                                h+=Math.min(pAmpli*3,15);
                            }else {
                                h+=Math.min(pAmpli,15);
                            }
                        }
                        if(pTarget.hasEffect(effect1)){
                            i2++;
                        }
                    }
                    int cc = r + b;
                    if(intent==PotionIntent.HEAL_OWNER){
                        if(cc>h){
                            if(r!=0){
                                return stack;
                            }
                        }
                    }else if(intent == PotionIntent.BUFF_OWNER){
                        if(cc>h && i1*0.5f>i2){
                            return stack;
                        }
                    }else if(intent == PotionIntent.HURT_TARGET){
                        if(h>cc && i1*0.70f>i2){
                            return stack;
                        }
                    }

                }
            }
        }
        return null;
    }

    public int getSlotIdItemStack(ItemStack stack) {
        for (int i = 2; i < 7; i++) {
            if (this.getContainer().getItem(i) == stack) {
                return i;
            }
        }
        return -1;
    }
    public void ordenThrow(){
        if(!this.isThrowItem() && this.isFlying() && !this.isSitting() && !this.inventory.getItem(0).isEmpty()){
            LivingEntity owner = !this.isTame() ? this.getOwnerIllager() : this.getOwner();
            this.nextIntent();
            if(this.getTargetIntent()!=null && owner!=null){
                if(this.canThrowItem(this.getTargetIntent(),owner)){
                    ItemStack stack=this.intentPotion(this.getIdPotionIntent(),this.getTargetIntent());
                    int cc=this.getSlotIdItemStack(stack);
                    if(cc!=-1){
                        this.setIsThrowItem(true);
                        this.setNextPotion(cc);
                    }
                }
            }
        }
    }

    public void setNextPotion(int pIdSlot){
        ItemStack stack = this.getContainer().getItem(pIdSlot).copy();
        this.inventory.setItem(pIdSlot,ItemStack.EMPTY);
        this.inventory.setItem(1,stack);
    }

    public void shootPotion(LivingEntity pTarget,ItemStack potion){
        if(!potion.isEmpty()){
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
            potion.shrink(1);
            this.inventory.setItem(1,ItemStack.EMPTY);
            this.setTargetIntent(null);
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

    @Override
    public void performRangedAttack(LivingEntity target, float pDistanceFactor) {
        Level levelAccessor=this.level;
        ArrowBeast entityarrow = new ArrowBeast(levelAccessor, this);
        if(this.random.nextFloat() > 0.9){
            entityarrow.addEffect(new MobEffectInstance(MobEffects.POISON,300,0));
        }
        double d0 = target.getY() + target.getEyeHeight() - 1.1;
        double d1 = target.getX() - this.getX();
        double d3 = target.getZ() - this.getZ();
        entityarrow.shoot(d1, d0 - entityarrow.getY() + Math.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, 1.6F, 0.1F);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        levelAccessor.addFreshEntity(entityarrow);
    }

    public void ordenAttack(LivingEntity pTarget,float distance){
        this.performRangedAttack(pTarget,distance);
        this.setIsAttacking(true);
        this.level.broadcastEntityEvent(this,(byte) 4);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==4){
            this.setIsAttacking(true);
        }
        super.handleEntityEvent(pId);
    }

    public static class SearchOwnerGoal extends Goal{
        private final ScroungerEntity scrounger;
        SearchOwnerGoal(ScroungerEntity scrounger){
            this.scrounger=scrounger;
        }

        @Override
        public boolean canUse() {
            return !this.scrounger.isTame() && this.scrounger.getOwnerIllager()==null ;
        }

        @Override
        public void start() {
            List<AbstractIllager> illagers=this.scrounger.level.getEntitiesOfClass(AbstractIllager.class,this.scrounger.getBoundingBox().inflate(30.0D));
            boolean flag = false;
            for(AbstractIllager illager : illagers){
                if(!flag){
                    if(illager.isAlive()){
                        this.scrounger.setOwnerIllager(illager);
                        flag=true;
                    }
                }else {
                    break;
                }
            }
        }
    }

    public static class HelpOwnerGoal extends Goal{

        private final ScroungerEntity scrounger;
        HelpOwnerGoal(ScroungerEntity scrounger){
            this.scrounger=scrounger;
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.scrounger.isTame() ? this.scrounger.getOwner() : this.scrounger.getOwnerIllager();
            if(owner!=null){
                  return !this.scrounger.isThrowItem() && !this.scrounger.isSitting() && this.scrounger.helpOwnerTimer<=0 && owner.getMaxHealth()*0.3f>owner.getHealth();
            }
            return false;
        }

        @Override
        public void start() {
            LivingEntity owner = this.scrounger.isTame() ? this.scrounger.getOwner() : this.scrounger.getOwnerIllager();
            if(owner!=null){
                this.scrounger.setTarget(owner.getLastHurtByMob());
                this.scrounger.ordenThrow();
                this.scrounger.helpOwnerTimer=1200;
            }
        }
    }
    static class ScroungerWanderGoal extends WaterAvoidingRandomFlyingGoal {
        public ScroungerWanderGoal(PathfinderMob p_186224_, double p_186225_) {
            super(p_186224_, p_186225_);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && (this.mob instanceof ScroungerEntity scrounger && !scrounger.isSitting());
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
        HEAL_OWNER(0),
        BUFF_OWNER(1),
        HURT_TARGET(2);

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
