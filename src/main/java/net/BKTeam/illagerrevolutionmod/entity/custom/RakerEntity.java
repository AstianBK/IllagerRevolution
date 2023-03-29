package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.api.IOpenRakerContainer;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.RakerArmorItem;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
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

public class RakerEntity extends TamableAnimal implements IAnimatable, IHasInventory,ContainerListener {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory= new SimpleContainer(3);
    private int attackTimer;
    Mob owner;
    private static final UUID RAKER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final UUID RAKER_ATTACK_DAMAGE_UUID= UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.PUFFERFISH);
    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(RakerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(RakerEntity.class, EntityDataSerializers.BOOLEAN);

    public RakerEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.attackTimer = 0;
    }

    @Override
    protected void dropEquipment() {
        ItemStack itemStack=new ItemStack(ModItems.SCRAPER_CLAW.get());
        itemStack.setCount(this.level.getRandom().nextInt(1,5));
        this.spawnAtLocation(itemStack);
        if(this.hasArmor()){
            if(!this.getItemBySlot(EquipmentSlot.LEGS).isEmpty()){
                this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.LEGS));
                this.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);
            }
            if(!this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()){
                this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.CHEST));
                this.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);
            }
        }
    }
    private boolean hasArmor(){
        return !this.getItemBySlot(EquipmentSlot.LEGS).isEmpty() || !this.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }
    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 13.0D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.41f)
                .build();

    }
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        if (!super.doHurtTarget(pEntity)) {
            return false;
        } else {
            if (pEntity instanceof LivingEntity livingEntity) {
                ItemStack armor;
                int timeBleeding=60;
                int ampliEffect=livingEntity.hasEffect(init_effect.BLEEDING.get()) ? livingEntity.getEffect(init_effect.BLEEDING.get()).getAmplifier() : 0;
                int ampliBleeding=0;
                if(!this.getItemBySlot(EquipmentSlot.LEGS).isEmpty()){
                    armor=this.getItemBySlot(EquipmentSlot.LEGS);
                    armor.hurtAndBreak(20,this,e->broadcastBreakEvent(EquipmentSlot.LEGS));
                    if(armor.getItem() instanceof  RakerArmorItem rakerArmorItem){
                        timeBleeding+=rakerArmorItem.getAddBleeding();
                    }
                }
                if(livingEntity.hasEffect(init_effect.BLEEDING.get()) && ampliEffect==1){
                    ampliBleeding=2;
                }else if(livingEntity.hasEffect(init_effect.BLEEDING.get()) && ampliEffect==0){
                    ampliBleeding=1;
                }
                livingEntity.addEffect(new MobEffectInstance(init_effect.BLEEDING.get(), timeBleeding,ampliBleeding));
            }
            return super.doHurtTarget(pEntity);
        }


    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (event.isMoving() && !isAggressive() && !this.isAttacking() && !this.isSitting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.walk1", ILoopType.EDefaultLoopTypes.LOOP));

        }
        else if(this.isAggressive() && event.isMoving() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.walk2", ILoopType.EDefaultLoopTypes.LOOP));

        }

        else if(this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }

        else if(this.isSitting() && this.isTame()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.sit", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ScrapperAttackGoal(this,1,false));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
}
    public void aiStep() {
        super.aiStep();
        if (this.isAttacking()) {
            this.attackTimer--;
        }
        if(this.attackTimer==0){
            this.setAttacking(false);
        }
    }
    @Override
    public InteractionResult mobInteract(Player player,  InteractionHand hand) {
        int health = (int) getHealth();
        int maxhealth = (int) getMaxHealth();
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = Items.PUFFERFISH;

        if (this.isTame()) {
            if (this.isFood(itemstack) && health < maxhealth) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.heal(3.0f);
                playSound(SoundEvents.CAT_EAT, 1.0F, -1.0F);
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                return InteractionResult.SUCCESS;
            }
        }
        if (item == itemForTaming && !isTame() && health <= maxhealth*30/100) {
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
        if (this.isTame() && this.isOwnedBy(player) && !player.isSecondaryUseActive() ) {
            if(player instanceof IOpenRakerContainer){
                this.openInventory(player);
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                this.updateContainerEquipment();
                return InteractionResult.SUCCESS;
            }
        }
        if(this.isTame() && player.isSecondaryUseActive() && !this.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.setSitting(!isSitting());
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
    public boolean isArmor(ItemStack stack){
        return stack.getItem() instanceof RakerArmorItem;
    }

    public boolean isAlliedTo(@NotNull Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof LivingEntity && ((LivingEntity) pEntity).getMobType() == MobType.ILLAGER && !isTame()) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }
    public void openInventory(Player player) {
        RakerEntity raker = (RakerEntity) ((Object) this);
        if (!this.level.isClientSide && player instanceof  IOpenRakerContainer) {
            ((IOpenRakerContainer)player).openRakerInventory(raker, this.inventory);
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isSitting", this.isSitting());
        compound.putBoolean("isAttacking",this.isAttacking());
        ItemStack itemStackChest = this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack itemStackHead = this.getItemBySlot(EquipmentSlot.LEGS);
        if(!itemStackChest.isEmpty()) {
            CompoundTag chestCompoundNBT = new CompoundTag();
            itemStackChest.save(chestCompoundNBT);
            compound.put("ChestRakerArmor", chestCompoundNBT);
        }
        if(!itemStackHead.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            itemStackHead.save(headCompoundNBT);
            compound.put("LegsRakerArmor", headCompoundNBT);
        }
    }
    
    public boolean hasInventoryChanged(Container container){
        return this.inventory!=container;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSitting(compound.getBoolean("isSitting"));
        setAttacking(compound.getBoolean("isAttacking"));
        CompoundTag compoundNBT = compound.getCompound("ChestRakerArmor");
        CompoundTag compoundNBT1 = compound.getCompound("LegsRakerArmor");
        if(!compoundNBT.isEmpty()) {
            ItemStack stack=ItemStack.of(compound.getCompound("ChestRakerArmor"));
            if(this.isArmor(stack)){
                this.setItemSlot(EquipmentSlot.CHEST,stack);
            }
        }
        if(!compoundNBT1.isEmpty()){
            ItemStack stack=ItemStack.of(compound.getCompound("LegsRakerArmor"));
            if(this.isArmor(stack)){
                this.setItemSlot(EquipmentSlot.LEGS,stack);
            }
        }
        this.updateContainerEquipment();
    }

    private void updateContainerEquipment() {
        this.setArmorEquipment(this.inventory.getItem(2));
        this.setArmorCrawsEquipment(this.inventory.getItem(1));

    }

    private void setArmorEquipment(ItemStack item) {
        if (!this.level.isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(RAKER_ARMOR_UUID);
            if(!this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()){
                int i = ((RakerArmorItem)item.getItem()).getArmorValue();
                if (i != 0) {
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(RAKER_ARMOR_UUID, "Raker armor bonus", i, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

    private void setArmorCrawsEquipment(ItemStack item) {
        if (!this.level.isClientSide) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(RAKER_ATTACK_DAMAGE_UUID);
            if(!this.getItemBySlot(EquipmentSlot.LEGS).isEmpty()){
                double i=((RakerArmorItem)item.getItem()).getDamageValue();
                if (i != 0){
                    this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(RAKER_ATTACK_DAMAGE_UUID, "Raker attack bonus", i, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, false);
        this.entityData.define(ATTACKING,false);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }
    public void setAttacking(boolean attacking){
        this.entityData.set(ATTACKING,attacking);
        this.attackTimer = isAttacking() ? 7 : 0;
    }
    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }
    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
    }


    @Override
    public Team getTeam() {
        return super.getTeam();
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
    public void setItemSlot(EquipmentSlot slotIn, ItemStack itemStack) {
        switch (slotIn.getType()){
            case ARMOR :
                this.inventory.setItem(slotIn.getIndex(),itemStack);
            default:
                super.setItemSlot(slotIn,itemStack);
        }
    }

    public void setOwner(Mob pOwner) {
        this.owner = pOwner;
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
        ItemStack chest= this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack chest1= this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs= this.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack legs1= this.getItemBySlot(EquipmentSlot.LEGS);
        if(this.tickCount >20){
            if ((this.isArmor(chest1) && chest!=chest1) || (this.isArmor(legs1) && legs!=legs1)){
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);
                this.updateContainerEquipment();
            }
        }
    }

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    static class ScrapperAttackGoal extends MeleeAttackGoal {
        private final RakerEntity goalOwner;

        public ScrapperAttackGoal(RakerEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && this.goalOwner.attackTimer <= 0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.goalOwner.playSound(SoundEvents.CAT_HURT, 1.2F, -3.0F);
                this.goalOwner.doHurtTarget(entity);
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);
        }

    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));

    }

    public boolean isFood(@NotNull ItemStack pStack) {
        return TEMPT_INGREDIENT.test(pStack);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
        this.playSound(SoundEvents.WOLF_STEP, 1.0F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return this.isTame() ? ModSounds.RAKER_MEOW.get() : SoundEvents.CAT_HISS;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.RAKER_HISS.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.CAT_DEATH;
    }


    protected float getSoundVolume() {
        return 0.2F;
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
    }

}

