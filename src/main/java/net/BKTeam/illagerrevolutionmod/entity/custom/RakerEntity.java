package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.Patreon;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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
import java.util.UUID;

public class RakerEntity extends IllagerBeastEntity implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int nextAssaultTimer;
    private int prepareTimer;
    private int attackTimer;

    private boolean isLeftAttack;

    private boolean isJump;
    Mob owner;
    private static final UUID RAKER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final UUID RAKER_ATTACK_DAMAGE_UUID= UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(Items.COD, Items.PUFFERFISH);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(RakerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ASSAULT =
            SynchedEntityData.defineId(RakerEntity.class, EntityDataSerializers.INT);


    public RakerEntity(EntityType<? extends IllagerBeastEntity> entityType, Level level) {
        super(entityType, level);
        this.nextAssaultTimer = 0;
        this.attackTimer = 0;
        this.prepareTimer = 0;
        this.isJump = false;
        this.isLeftAttack = false;
        this.maxUpStep=1.0F;
    }

    @Override
    protected void dropEquipment() {
        ItemStack itemStack=new ItemStack(ModItems.RAKER_CLAW.get());
        itemStack.setCount(this.level.getRandom().nextInt(1,5));
        this.spawnAtLocation(itemStack);
        if(this.hasArmor()){
            if(!this.getItemBySlot(EquipmentSlot.LEGS).isEmpty()){
                this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.LEGS));
                this.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);
            }
            if(!this.getItemBySlot(EquipmentSlot.FEET).isEmpty()){
                this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.FEET));
                this.setItemSlot(EquipmentSlot.FEET,ItemStack.EMPTY);
            }
        }
    }

    private boolean hasArmor(){
        return !this.getItemBySlot(EquipmentSlot.LEGS).isEmpty() || !this.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }
    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 13.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 0.42f)
                .build();

    }
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        if (!super.doHurtTarget(pEntity)) {
            return false;
        } else {
            if (pEntity instanceof LivingEntity livingEntity) {
                ItemStack armor;
                int timeBleeding = 60;
                int ampliEffect = livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) ? livingEntity.getEffect(InitEffect.DEEP_WOUND.get()).getAmplifier() : 0;
                int ampliBleeding = 0;
                if (!this.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
                    armor = this.getItemBySlot(EquipmentSlot.LEGS);
                    armor.hurtAndBreak(20, this, e -> broadcastBreakEvent(EquipmentSlot.LEGS));
                    if (armor.getItem() instanceof BeastArmorItem rakerArmorItem) {
                        timeBleeding += rakerArmorItem.getAddBleeding();
                    }
                }
                if (livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) && ampliEffect == 1) {
                    ampliBleeding = 2;
                } else if (livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) && ampliEffect == 0) {
                    ampliBleeding = 1;
                }
                livingEntity.addEffect(new MobEffectInstance(InitEffect.DEEP_WOUND.get(), timeBleeding, ampliBleeding));
            }
            return super.doHurtTarget(pEntity);
        }


    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (this.isAssaultMode() && this.isOnGround()) {

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.prepare", ILoopType.EDefaultLoopTypes.LOOP));

        } else if(this.isAssaultMode() && !this.isOnGround()) {

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.jump", ILoopType.EDefaultLoopTypes.LOOP));

        } else if(event.isMoving() && !isAggressive() && !this.isAttacking() && !this.isSitting()) {

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.walk1", ILoopType.EDefaultLoopTypes.LOOP));

        } else if(this.isAggressive() && event.isMoving() && !this.isAttacking()){

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.walk2", ILoopType.EDefaultLoopTypes.LOOP));

        } else if(this.isAttacking()) {

            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.attack"+(this.isLeftAttack ? "" : "2"), ILoopType.EDefaultLoopTypes.PLAY_ONCE));

        } else if(this.isSitting() && this.isTame()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.sit", ILoopType.EDefaultLoopTypes.LOOP));
        } else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrapper.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RakerAttackGoal(this,1,false));
        this.goalSelector.addGoal(3,new TemptGoal(this,1.5d,Ingredient.of(Items.PUFFERFISH),false){
            @Override
            public boolean canUse() {
                return super.canUse() && ((RakerEntity)this.mob).isTame() && !((RakerEntity)this.mob).isSitting();
            }
        });
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new RakerFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    }

    public boolean isAssaultMode(){
        return this.getAssaultState() == AssaultStates.ASSAULT || this.getAssaultState() == AssaultStates.PREPARE;
    }

    @Override
    public double getSpeedBase() {
        return this.isTame() ? 0.40D : 0.42D;
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAttacking()) {
            this.attackTimer--;
        }

        if(this.attackTimer==0){
            this.setAttacking(false);
        }

        if(this.getAssaultState() == AssaultStates.PREPARE){
            this.prepareTimer--;
            if(this.prepareTimer==0){
                this.setAssaultMode(2);
            }
            if(this.getTarget()!=null){
                if(!this.getTarget().isAlive()){

                }
            }

        }
        if(this.getAssaultState() == AssaultStates.ASSAULT){
            if(this.getTarget()!=null){
                if(!this.getTarget().isAlive()){
                    this.setAssaultMode(0);
                }
            }

        }

        if(this.getAssaultState() == AssaultStates.FINISH){
            this.nextAssaultTimer--;
            if(this.nextAssaultTimer<0){
                this.setAssaultMode(0);
            }
        }
    }
    public boolean isScrapper(){
        return this.getOwner() != null && this.getCustomName() != null && Patreon.isPatreon((Player) this.getOwner(), IllagerRevolutionMod.ACOLYTES_SKIN_UUID) && this.getCustomName().getString().equals("Scrapper");
    }
    @Override
    public InteractionResult mobInteract(Player player,  InteractionHand hand) {
        int health = (int) getHealth();
        int maxhealth = (int) getMaxHealth();
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = Items.PUFFERFISH;

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
        }

        if (this.isTame()) {
            if (this.isFood(itemstack) && health < maxhealth) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.heal(3.0f);
                this.playSound(SoundEvents.CAT_EAT, 1.0F, -1.0F);
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
                return InteractionResult.SUCCESS;
            }
        }
        if((this.isArmor(itemstack)) && this.isTame() && this.isOwnedBy(player)){
            ItemStack stack = itemstack.copy();
            if(!this.getItemBySlot(((BeastArmorItem)item).getEquipmetSlot()).isEmpty()) {
                this.spawnAtLocation(this.getItemBySlot(((BeastArmorItem) item).getEquipmetSlot()));
                this.setItemSlot(((BeastArmorItem) item).getEquipmetSlot(),ItemStack.EMPTY);
            }
            this.playSound(SoundEvents.ARMOR_EQUIP_IRON);
            this.setItemSlot(((BeastArmorItem) item).getEquipmetSlot(),stack);
            itemstack.shrink(1);
            return InteractionResult.CONSUME;
        }
        if (item == itemForTaming && !this.isTame() && health <= maxhealth*30/100) {
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
        if (this.isTame() && this.isOwnedBy(player) && !player.isSecondaryUseActive() ) {
            if(player instanceof IOpenBeatsContainer && itemstack.is(ModItems.BEAST_STAFF.get())){
                this.openInventory(player);
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
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
        return stack.getItem() instanceof BeastArmorItem armor && armor.getBeast()==this.getTypeBeast();
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

    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    public Beast getTypeBeast() {
        return Beast.RAKER;
    }

    public void setAssaultMode(int pId){
        this.entityData.set(ASSAULT,pId);
        switch (pId){
            case 1 -> this.prepareTimer = 20;
            case 3 -> this.nextAssaultTimer = 500;
        }
    }

    public AssaultStates getAssaultState(){
        return AssaultStates.byId(this.getAssaultId() & 255);
    }

    public int getAssaultId(){
        return this.entityData.get(ASSAULT);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isAttacking",this.isAttacking());
        compound.putInt("isAssault",this.getAssaultId());
        compound.putInt("nextAssaultTimer",this.nextAssaultTimer);
        ItemStack itemStackFeet = this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack itemStackHead = this.getItemBySlot(EquipmentSlot.LEGS);
        if(!itemStackFeet.isEmpty()) {
            CompoundTag FeetCompoundNBT = new CompoundTag();
            itemStackFeet.save(FeetCompoundNBT);
            compound.put("FeetRakerArmor", FeetCompoundNBT);
        }
        if(!itemStackHead.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            itemStackHead.save(headCompoundNBT);
            compound.put("LegsRakerArmor", headCompoundNBT);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.nextAssaultTimer = compound.getInt("nextAssault");
        this.setAttacking(compound.getBoolean("isAttacking"));
        this.setAssaultMode(compound.getInt("isAssault"));
        CompoundTag compoundNBT = compound.getCompound("FeetRakerArmor");
        CompoundTag compoundNBT1 = compound.getCompound("LegsRakerArmor");
        if(!compoundNBT.isEmpty()) {
            if (this.isArmor(ItemStack.of(compound.getCompound("FeetRakerArmor")))) {
                ItemStack stack=ItemStack.of(compound.getCompound("FeetRakerArmor"));
                this.setItemSlot(EquipmentSlot.FEET,stack);
            }

        }
        if(!compoundNBT1.isEmpty()){
            if(this.isArmor(ItemStack.of(compound.getCompound("LegsRakerArmor")))){
                ItemStack stack=ItemStack.of(compound.getCompound("LegsRakerArmor"));
                this.setItemSlot(EquipmentSlot.LEGS,stack);
            }
        }
        this.updateContainerEquipment();
    }

    @Override
    public boolean canEquipOnFeet(ItemStack p_39690_) {
        return super.canEquipOnFeet(p_39690_) && ((BeastArmorItem)p_39690_.getItem()).getEquipmetSlot()==EquipmentSlot.FEET;
    }

    @Override
    public boolean canEquipOnLegs(ItemStack p_39690_) {
        return super.canEquipOnLegs(p_39690_) && ((BeastArmorItem)p_39690_.getItem()).getEquipmetSlot()==EquipmentSlot.LEGS;
    }

    protected void updateContainerEquipment() {
        this.setArmorEquipment(this.inventory.getItem(0));
        this.setArmorCrawsEquipment(this.inventory.getItem(1));
    }

    private void setArmorEquipment(ItemStack item) {
        if (!this.level.isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(RAKER_ARMOR_UUID);
            if(!this.getItemBySlot(EquipmentSlot.FEET).isEmpty()){
                int i = ((BeastArmorItem)item.getItem()).getArmorValue();
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
                double i=((BeastArmorItem)item.getItem()).getDamageValue();
                if (i != 0){
                    this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(RAKER_ATTACK_DAMAGE_UUID, "Raker attack bonus", i, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING,false);
        this.entityData.define(ASSAULT,0);
    }
    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
        this.attackTimer = isAttacking() ? 7 : 0;
        this.isLeftAttack = this.level.random.nextBoolean();
    }

    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
    }

    @Override
    public void setTame(boolean tamed) {
        super.setTame(tamed);
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.40f);
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

    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack Feet= this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack Feet1= this.getItemBySlot(EquipmentSlot.FEET);
        this.updateContainerEquipment();
        ItemStack legs= this.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack legs1= this.getItemBySlot(EquipmentSlot.LEGS);
        if(this.tickCount > 20){
            if ((this.isArmor(Feet1) && Feet!=Feet1) || (this.isArmor(legs1) && legs!=legs1)){
                this.playSound(SoundEvents.ARMOR_EQUIP_IRON,1.0f,1.0f);
                this.updateContainerEquipment();
            }
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==4){
            this.setAttacking(true);
        }else {
            super.handleEntityEvent(pId);
        }
    }

    static class RakerAttackGoal extends MeleeAttackGoal {
        private final RakerEntity goalOwner;

        public RakerAttackGoal(RakerEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
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
        public void tick() {
            if(this.goalOwner.getTarget()!=null){
                LivingEntity target = this.goalOwner.getTarget();
                float dx = this.goalOwner.distanceTo(this.goalOwner.getTarget());
                if(this.goalOwner.getAssaultState() == AssaultStates.CAN_ASSAULT){
                    if(this.goalOwner.getTarget()!=null){
                        if(dx<15 && this.goalOwner.isOnGround()){
                            this.goalOwner.setAssaultMode(1);
                            this.goalOwner.setOnCombat(true);
                        }
                    }
                }
                if (this.goalOwner.getAssaultId() > 0 && this.goalOwner.getAssaultId() < 3 && target!=null) {
                    this.goalOwner.getNavigation().stop();
                    Vec3 vec = target.position().subtract(this.goalOwner.position());
                    this.goalOwner.setYRot(-((float) Mth.atan2(vec.x, vec.z)) * (180F / (float) Math.PI));
                    this.goalOwner.yBodyRot = this.goalOwner.getYRot();
                    this.goalOwner.getLookControl().setLookAt(target);
                    if(this.goalOwner.getAssaultState() == AssaultStates.ASSAULT && this.goalOwner.isOnGround() && !this.goalOwner.isJump){
                        Vec3 vector3d1 = new Vec3(target.getX() - this.goalOwner.getX(), 0.0D, target.getZ() - this.goalOwner.getZ());
                        if (vector3d1.lengthSqr() > 1.0E-7D) {
                            vector3d1 = vector3d1.normalize().scale(Math.min(dx, 15) * 0.2F);
                        }
                        this.goalOwner.isJump=true;
                        this.goalOwner.setDeltaMovement(vector3d1.x, vector3d1.y + 0.6F + 0.1F * Mth.clamp(target.getEyeY() - this.goalOwner.getY(), 0, 2), vector3d1.z);
                    }else if(this.goalOwner.isOnGround() && this.goalOwner.isJump){
                        this.goalOwner.isJump=false;
                        this.goalOwner.setAssaultMode(3);
                    }

                    if (dx < target.getBbWidth() + 3) {
                        this.goalOwner.doHurtTarget(target);
                        this.goalOwner.setAssaultMode(3);
                    }
                } else {
                    this.goalOwner.setAssaultMode(3);
                    super.tick();
                }
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);
            this.goalOwner.level.broadcastEntityEvent(this.goalOwner,(byte) 4);
        }

    }

    static class  RakerFollowOwnerGoal extends FollowOwnerGoal{

        private final IllagerBeastEntity beast;

        public RakerFollowOwnerGoal(TamableAnimal pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
            super(pTamable, pSpeedModifier, pStartDistance, pStopDistance, pCanFly);
            this.beast = (IllagerBeastEntity) pTamable;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.beast.onCombat();
        }
    }

    public enum AssaultStates {
        CAN_ASSAULT(0),
        PREPARE(1),
        ASSAULT(2),
        FINISH(3);

        private static final AssaultStates[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(AssaultStates::getId)).toArray(AssaultStates[]::new);
        private final int id;

        AssaultStates(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static AssaultStates byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }
    }
}

