package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.entity.projectile.FeatherProjectile;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
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
import java.util.*;
import java.util.function.Predicate;

public class ScroungerEntity extends IllagerBeastEntity implements FlyingAnimal, RangedAttackMob{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.BOOLEAN);
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private int helpOwnerTimer;

    public int nextAttack;

    LivingEntity ownerIllager;

    private static final EntityDataAccessor<Integer> ID_POTION_INTENT =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(ScroungerEntity.class, EntityDataSerializers.BOOLEAN);

    public ScroungerEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.nextAttack=0;
        this.helpOwnerTimer=0;
        this.moveControl=new ScroungerFlyingMoveControl(this,5,false);
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 40.D)
                .add(Attributes.MOVEMENT_SPEED, 1.0d)
                .add(Attributes.FLYING_SPEED,3.0D)
                .build();

    }

    private   <E extends IAnimatable> PlayState predicateMaster(AnimationEvent<E> event) {
        if(this.isFlying()){
            if(event.isMoving() && !this.isAttacking()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.fly", ILoopType.EDefaultLoopTypes.LOOP));
            }else if(this.isAttacking()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.attack1", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            }else if(!this.isAttacking()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.idle2", ILoopType.EDefaultLoopTypes.LOOP));
            }
        }else if(this.isSitting()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.sit", ILoopType.EDefaultLoopTypes.LOOP));
        }else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scrounger.idle1", ILoopType.EDefaultLoopTypes.LOOP));
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
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1,new TemptGoal(this,1.5d,Ingredient.of(Items.FERMENTED_SPIDER_EYE),false){
            @Override
            public boolean canUse() {
                return super.canUse() && ((ScroungerEntity)this.mob).isTame();
            }
        });
        this.goalSelector.addGoal(3,new ScroungerWanderGoal(this,1.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2,new ScroungerFollowOwnerGoal(this,3.5d,15.0F,3.0F,true));
        this.targetSelector.addGoal(2, new SearchOwnerGoal(this));
        this.targetSelector.addGoal(2, new HelpOwnerGoal(this));
        this.targetSelector.addGoal(2,new ScroungerRangedAttack(this,1.5d,20,20,15.0f));
        super.registerGoals();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isAttacking()){
            this.nextAttack--;
        }
        if(this.nextAttack==10){
            this.shoot(this.getTarget());
        }
        if(this.nextAttack<0){
            this.setIsAttacking(false);
        }
        if(this.helpOwnerTimer>0){
            this.helpOwnerTimer--;
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
            } else if(itemstack.is(Items.WATER_BUCKET) && this.isPainted()){
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
            if(itemstack.is(ModItems.SCROUNGER_POUCH.get())){
                if(!this.level.isClientSide){
                    this.setHasChest(true);
                    this.playSound(SoundEvents.MULE_CHEST);
                }
                if(!player.getAbilities().instabuild){
                    itemstack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
            if(itemstack.is(Items.FERMENTED_SPIDER_EYE)){
                if(this.canTame()){
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
                        playSound(SoundEvents.PARROT_EAT, 1.0F, 1.5F);
                        return InteractionResult.SUCCESS;
                    }
                }

            }
            if(itemstack.is(ModItems.BEAST_STAFF.get())){
                this.openInventory(player);
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
                return InteractionResult.SUCCESS;
            }
            if(player.isSecondaryUseActive() && this.isOwnedBy(player) && this.isTame()){
                this.setSitting(!this.isSitting());
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, pHand);
        }else {
            if(player.isSecondaryUseActive() && this.isOwnedBy(player) && this.hasChest()){
                this.setHasChest(false);
                this.dropPotions();
                if(!player.getAbilities().instabuild){
                    player.setItemInHand(pHand,new ItemStack(ModItems.SCROUNGER_POUCH.get()));
                }
                return InteractionResult.CONSUME;
            }
            if(!player.isSecondaryUseActive() && this.isOwnedBy(player) && this.isTame()){
                this.setSitting(!this.isSitting());
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, pHand);
    }

    public boolean canTame(){
        List<IllagerBeastTamerEntity> list = this.level.getEntitiesOfClass(IllagerBeastTamerEntity.class,this.getBoundingBox().inflate(40.0D));
        return list.isEmpty();
    }
    public void containerChanged(Container pInvBasic) {
        ItemStack potionH=this.inventory.getItem(0);
        ItemStack potionD=this.inventory.getItem(1);
        this.updateContainerEquipment();
        ItemStack potionH1=this.inventory.getItem(0);
        ItemStack potionD1=this.inventory.getItem(1);
        if(this.tickCount>20 && (potionD1!=potionD || potionH1!=potionH)){
            this.playSound(SoundEvents.MULE_CHEST);
        }
    }

    public void dropPotions(){
        for(int i=0;i<this.getInventorySize();i++){
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()){
                this.spawnAtLocation(stack);
                this.setItemSlot(i==0 ? EquipmentSlot.FEET : EquipmentSlot.LEGS,ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void dropEquipment() {
        if(this.hasChest()){
            this.dropPotions();
            this.spawnAtLocation(new ItemStack(ModItems.SCROUNGER_POUCH.get()));
        }
        super.dropEquipment();
    }

    @Override
    protected int getInventorySize() {
        return 2;
    }

    public LivingEntity getOwnerIllager() {
        return this.ownerIllager;
    }

    public void setOwnerIllager(LivingEntity ownerIllager) {
        this.ownerIllager = ownerIllager;
    }

    public void setHasChest(boolean pBoolean){
        this.entityData.set(DATA_ID_CHEST,pBoolean);
    }

    public boolean hasChest(){
        return this.entityData.get(DATA_ID_CHEST);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isAttacking",this.isAttacking());
        compound.putBoolean("isHasChest",this.hasChest());
        if(this.hasChest()){
            ListTag tags = new ListTag();
            for(int i=0;i<this.getInventorySize();i++){
                ItemStack stack=this.getItemBySlot(i==0 ? EquipmentSlot.FEET : EquipmentSlot.LEGS);
                if(!stack.isEmpty()){
                    CompoundTag nbt=new CompoundTag();
                    nbt.putByte("Slot",(byte)i);
                    stack.save(nbt);
                    tags.add(nbt);
                }
            }
            compound.put("Potions",tags);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIsAttacking(compound.getBoolean("isAttacking"));
        this.setHasChest(compound.getBoolean("isHasChest"));
        if(this.hasChest()){
            ListTag tags = compound.getList("Potions",10);
            for (int i = 0 ; i < tags.size() ; i++){
                CompoundTag tag=tags.getCompound(i);
                int j=tag.getByte("Slot") & 255;
                if(j<this.getInventorySize()){
                    if(j==0){
                        this.setItemSlot(EquipmentSlot.FEET,ItemStack.of(tag));
                    }else {
                        this.setItemSlot(EquipmentSlot.LEGS,ItemStack.of(tag));
                    }
                }
            }
        }

        this.updateContainerEquipment();
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
    public void setItemSlot(EquipmentSlot slotIn, ItemStack itemStack) {
        switch (slotIn.getType()){
            case ARMOR :
                this.inventory.setItem(slotIn.getIndex(),itemStack);
            default:
                super.setItemSlot(slotIn,itemStack);
        }
    }

    public int getRowInventory(int slot){
        return 18;
    }
    public int getColumnInventory(int slot){
        return 80+18*slot;
    }


    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACKING,false);
        this.entityData.define(ID_POTION_INTENT,0);
        this.entityData.define(DATA_ID_CHEST,false);
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
    public Beast getTypeBeast() {
        return Beast.SCROUNGER;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        ItemStack itemStack=new ItemStack(ModItems.SCROUNGER_FEATHER.get());
        itemStack.setCount(this.level.getRandom().nextInt(1,4));
        this.spawnAtLocation(itemStack);
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<ScroungerEntity>(this, "controller_body",
                0, this::predicateMaster));
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

    public int getPotionIntent(){
        return this.entityData.get(ID_POTION_INTENT);
    }


    public List<MobEffectInstance> intentAttack(PotionIntent intent, LivingEntity pTarget){
        List<MobEffectInstance> effects = new ArrayList<>();
        if(pTarget!=null && intent!=null){
            int i = intent==PotionIntent.HEAL_OWNER ? 0 : 1;
            ItemStack stack=this.inventory.getItem(i);
            if(!stack.isEmpty()){
                for(MobEffectInstance effect1 : PotionUtils.getPotion(stack).getEffects()){
                    MobEffect effect2 =effect1.getEffect();
                    if(effect2.getCategory() == MobEffectCategory.BENEFICIAL){
                        if(!pTarget.hasEffect(effect2)){
                            effects.add(effect1);
                        }
                    }else if(i==1){
                        if (effect2.getCategory() == MobEffectCategory.HARMFUL){
                            if(!pTarget.hasEffect(effect2)){
                                effects.add(effect1);
                            }
                        }
                    }
                }
                return effects;
            }
        }
        return null;
    }
    private void setIdPotionIntent(int i) {
        this.entityData.set(ID_POTION_INTENT,i);
    }

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float pDistanceFactor) {
        this.setIsAttacking(true);
        this.setTarget(target);
    }

    public void shoot(LivingEntity target){
        Level levelAccessor = this.level;
        PotionIntent intent = this.getIdPotionIntent();
        LivingEntity owner = this.isTame() ? this.getOwner() : this.getOwnerIllager();
        boolean flag = intent == PotionIntent.HEAL_OWNER && owner!=null;
        target=flag  ? owner : target;
        if(target!=null){
            FeatherProjectile entityarrow = new FeatherProjectile(levelAccessor, this);
            double i = flag ? 1.0d : 5.0D;
            if(this.hasChest()){
                if(!this.level.isClientSide){
                    if(target instanceof Zombie){
                        intent = PotionIntent.HEAL_OWNER;
                    }
                    if(this.intentAttack(intent,target)!=null){
                        for(MobEffectInstance effect:this.intentAttack(intent,target)){
                            if(effect!=null){
                                entityarrow.addEffect(new MobEffectInstance(effect.getEffect(),effect.getDuration(),effect.getAmplifier(),effect.isAmbient(),effect.isVisible()));
                            }
                        }
                    }
                }
            }
            entityarrow.setBaseDamage(i);
            double d0 = target.getY() + target.getEyeHeight() - 1.1;
            double d1 = target.getX() - this.getX();
            double d3 = target.getZ() - this.getZ();
            entityarrow.shoot(d1, d0 - entityarrow.getY() + Math.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, 1.6F, 0.1F);
            this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            levelAccessor.addFreshEntity(entityarrow);
        }
        this.setTarget((LivingEntity)null);
    }
    public void nextAction(LivingEntity target){
        LivingEntity owner = this.isTame() ? this.getOwner() : this.getOwnerIllager();
        Predicate<MobEffectInstance> predicate=e-> e.getEffect()!=MobEffects.HEAL && e.getEffect()!=MobEffects.REGENERATION && e.getEffect()!=MobEffects.ABSORPTION;
        if(owner!=null && target!=null){
            float h = owner.getHealth();
            List<MobEffectInstance> listEffects=this.intentAttack(PotionIntent.HEAL_OWNER,owner);
            if(listEffects!=null && !listEffects.isEmpty()){
                boolean hasHeal = listEffects.stream().anyMatch(e->e.getEffect()==MobEffects.HEAL || e.getEffect()==MobEffects.REGENERATION || e.getEffect()==MobEffects.ABSORPTION );
                boolean flag = owner.getActiveEffects().stream().filter(predicate)==listEffects.stream().filter(predicate);
                if(owner.getMaxHealth()*0.5f>h || (!hasHeal && !flag)){
                    this.setIdPotionIntent(0);
                }else {
                        this.setIdPotionIntent(1);
                }
            }else {
                this.setIdPotionIntent(1);
            }

        }
    }

    protected SoundEvent getAmbientSound() {
        return this.level.random.nextFloat() > 0.5f ? ModSounds.SCROUNGER_AMBIENT1.get() : ModSounds.SCROUNGER_AMBIENT2.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.SCROUNGER_HURT.get();
    }


    public void ordenAttack(LivingEntity pTarget){
        this.nextAction(pTarget);
        this.setTarget(pTarget);
        this.setOnCombat(true);
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
                  return !this.scrounger.isSitting() && this.scrounger.helpOwnerTimer<=0 && owner.getMaxHealth()!=owner.getHealth();
            }
            return false;
        }

        @Override
        public void start() {
            boolean flag;
            LivingEntity owner = this.scrounger.isTame() ? this.scrounger.getOwner() : this.scrounger.getOwnerIllager();
            if(owner!=null){
                if(owner.getHealth()<owner.getMaxHealth()*0.3D){
                    flag=true;
                }else {
                    flag=this.scrounger.level.random.nextFloat()>0.30D;
                }
                if(flag){
                    this.scrounger.setIdPotionIntent(0);
                    this.scrounger.helpOwnerTimer=1200;
                    this.scrounger.performRangedAttack(owner,9.0f);
                }
            }
        }
    }

    @Override
    public boolean canEquipOnFeet(ItemStack p_39690_) {
        return this.canUsedPotion(0,p_39690_);
    }

    @Override
    public boolean canViewInventory() {
        return super.canViewInventory() && this.hasChest();
    }

    @Override
    public boolean canEquipOnLegs(ItemStack p_39690_) {
        return this.canUsedPotion(1,p_39690_);
    }

    public boolean canUsedPotion(int pSlot, ItemStack pStack){
        return (pStack.is(Items.POTION)) && this.isBeneficalPotion(pSlot,pStack);
    }

    public boolean isBeneficalPotion(int pSlot,ItemStack pStack){
        if(pSlot==0){
            return PotionUtils.getPotion(pStack).getEffects().stream().anyMatch(e-> e.getEffect().isBeneficial());
        }else {
            return PotionUtils.getPotion(pStack).getEffects().stream().anyMatch(e-> !e.getEffect().isBeneficial());
        }
    }

    static class ScroungerRangedAttack extends Goal{
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public ScroungerRangedAttack(RangedAttackMob pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
            this(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackInterval, pAttackRadius);
        }

        public ScroungerRangedAttack(RangedAttackMob pRangedAttackMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax, float pAttackRadius) {
            if (!(pRangedAttackMob instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.rangedAttackMob = pRangedAttackMob;
                this.mob = (Mob)pRangedAttackMob;
                this.speedModifier = pSpeedModifier;
                this.attackIntervalMin = pAttackIntervalMin;
                this.attackIntervalMax = pAttackIntervalMax;
                this.attackRadius = pAttackRadius;
                this.attackRadiusSqr = pAttackRadius * pAttackRadius;
                this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            }
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !((ScroungerEntity)this.mob).isSitting();
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            ScroungerEntity scrounger=((ScroungerEntity)this.mob);
            if(!scrounger.isAttacking()){
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);

                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 5) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target.getX(),this.target.getY(0.5D),this.target.getZ(),this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (--this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }
                    float f = (float)Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.rangedAttackMob.performRangedAttack(this.target, f1);
                    this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
                } else if (this.attackTime < 0) {
                    this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
                }

            }else {
                this.mob.getNavigation().stop();
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

    static class ScroungerFlyingMoveControl extends MoveControl{
        private final int maxTurn;
        private final boolean hoversInPlace;

        public ScroungerFlyingMoveControl(Mob pMob, int pMaxTurn, boolean pHoversInPlace) {
            super(pMob);
            this.maxTurn = pMaxTurn;
            this.hoversInPlace = pHoversInPlace;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                this.mob.setNoGravity(true);
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }

                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
                float f1;
                if (this.mob.isOnGround()) {
                    f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                } else {
                    f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                }

                this.mob.setSpeed(f1);
                double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                if (Math.abs(d1) > (double)1.0E-5F || Math.abs(d4) > (double)1.0E-5F) {
                    float f2 = (float)(-(Mth.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float)this.maxTurn));
                    this.mob.setYya(d1 > 0.0D ? f1 : -f1);
                }
            } else {
                if (!this.hoversInPlace && !((ScroungerEntity)this.mob).onCombat()) {
                    this.mob.setNoGravity(false);
                }

                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }

        }
    }

    static class ScroungerFollowOwnerGoal extends Goal{
        private final TamableAnimal tamable;
        private LivingEntity owner;
        private final LevelReader level;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;
        private float oldWaterCost;
        private final boolean canFly;

        public ScroungerFollowOwnerGoal(TamableAnimal pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
            this.tamable = pTamable;
            this.level = pTamable.level;
            this.speedModifier = pSpeedModifier;
            this.navigation = pTamable.getNavigation();
            this.startDistance = pStartDistance;
            this.stopDistance = pStopDistance;
            this.canFly = pCanFly;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            if (!(pTamable.getNavigation() instanceof GroundPathNavigation) && !(pTamable.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.tamable.isTame() ? this.tamable.getOwner() : ((ScroungerEntity)this.tamable).getOwnerIllager();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.tamable.isOrderedToSit()) {
                return false;
            } else if (this.tamable.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
                return false;
            }else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.tamable.isOrderedToSit()) {
                return false;
            } else {
                return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                    double dx=this.tamable.isTame() ? 900.0D : 144.0D;
                    if (this.tamable.distanceToSqr(this.owner) >= dx) {
                        this.teleportToOwner();
                    } else {
                        if(!((ScroungerEntity)this.tamable).onCombat()){
                            this.navigation.moveTo(this.owner, this.speedModifier);
                        }else {
                            this.navigation.moveTo(this.owner.getX(),this.owner.getY()+5.0d,this.owner.getZ(),this.speedModifier);
                        }
                    }

                }
            }
        }

        private void teleportToOwner() {
            BlockPos blockpos = this.owner.blockPosition();
            for(int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                int b = ((ScroungerEntity)this.tamable).onCombat() ? 5 : 0;
                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k + b, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean maybeTeleportTo(int pX, int pY, int pZ) {
            if (Math.abs((double)pX - this.owner.getX()) < 2.0D && Math.abs((double)pZ - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
                return false;
            } else {
                this.tamable.moveTo((double)pX + 0.5D, (double)pY, (double)pZ + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean canTeleportTo(BlockPos pPos) {
            BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pPos.mutable());
            boolean flag = ((ScroungerEntity)this.tamable).onCombat();
            if(flag && this.level.getBlockState(pPos).isAir()){
                return this.level.noCollision(this.tamable,this.tamable.getBoundingBox().move(pPos));
            }
            if (blockpathtypes != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pPos.below());
                if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pPos.subtract(this.tamable.blockPosition());
                    return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockpos));
                }
            }
        }

        private int randomIntInclusive(int pMin, int pMax) {
            return this.tamable.getRandom().nextInt(pMax - pMin + 1) + pMin;
        }
    }

    public enum PotionIntent{
        HEAL_OWNER(0),
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
