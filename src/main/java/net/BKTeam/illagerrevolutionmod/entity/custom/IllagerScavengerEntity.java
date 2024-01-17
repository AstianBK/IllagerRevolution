package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSand;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.UUID;


public class IllagerScavengerEntity extends AbstractIllager implements GeoEntity, InventoryCarrier {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final SimpleContainer inventory = new SimpleContainer(6);
    private static final UUID SCAVENGER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final UUID SCAVANGER_ATTACK_DAMAGE_UUID= UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");

    private boolean useSand;

    private int scrapTimer;

    private int attackTimer;


    private static final EntityDataAccessor<Boolean> HAS_ITEM =
            SynchedEntityData.defineId(IllagerScavengerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(IllagerScavengerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKLANTERN =
            SynchedEntityData.defineId(IllagerScavengerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ID_VARIANT =
            SynchedEntityData.defineId(IllagerScavengerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ARMOR_TIER =
            SynchedEntityData.defineId(IllagerScavengerEntity.class, EntityDataSerializers.INT);

    public IllagerScavengerEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
        this.scrapTimer =0;
        this.attackTimer=0;
        this.useSand =false;
    }
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FAKE_JUNK_AXE.get()));
        this.setIdVariant(this.level().random.nextInt(0,6));
        this.setArmorTier(this.level().random.nextInt(0,2));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean isAlliedTo(@NotNull Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof LivingEntity && ((LivingEntity) pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {

    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR,5.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 0.31f).build();

    }

    private   <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        if (event.isMoving() && !this.isAggressive() && !this.isAttacking() && !this.isAttackLantern()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.illagerminerbadlands.walk", Animation.LoopType.LOOP));
        }
        else if (this.isAttacking() && this.isAttackLantern()){
            event.getController().setAnimation(RawAnimation.begin().then("animation.illagerminerbadlands.attack2",Animation.LoopType.PLAY_ONCE));
        }
        else if (this.isAttacking()){
            event.getController().setAnimation(RawAnimation.begin().then("animation.illagerminerbadlands.attack",Animation.LoopType.PLAY_ONCE));
        }
        else if (this.isAggressive() && event.isMoving() && !this.isAttacking()){
            event.getController().setAnimation(RawAnimation.begin().then("animation.illagerminerbadlands.walk2",Animation.LoopType.LOOP));

        } else event.getController().setAnimation(RawAnimation.begin().then("animation.illagerminerbadlands.idle",Animation.LoopType.LOOP));
        return PlayState.CONTINUE;

    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        int cc=this.scrapTimer;
        if(pEntity instanceof ServerPlayer && cc==0){
            if(this.getRandom().nextInt(2)==1){
                boolean flag = false;
                for(ItemStack stack : pEntity.getArmorSlots()){
                    if(stack.getItem() instanceof ArmorItem armorItem ){
                        switch (armorItem.getEquipmentSlot()){
                            case HEAD -> {
                                flag=true;
                                stack.hurtAndBreak(40,(LivingEntity)pEntity,e-> e.broadcastBreakEvent(EquipmentSlot.HEAD));
                            }
                            case CHEST -> {
                                flag=true;
                                stack.hurtAndBreak(40,(LivingEntity)pEntity,e-> e.broadcastBreakEvent(EquipmentSlot.CHEST));
                            }
                            case LEGS -> {
                                flag=true;
                                stack.hurtAndBreak(40,(LivingEntity)pEntity,e-> e.broadcastBreakEvent(EquipmentSlot.LEGS));
                            }
                            case FEET -> {
                                flag=true;
                                stack.hurtAndBreak(40,(LivingEntity)pEntity,e-> e.broadcastBreakEvent(EquipmentSlot.FEET));
                            }
                        }
                    }
                }
                if(((Player)pEntity).isBlocking()){
                    flag=true;
                    for (InteractionHand hand : InteractionHand.values()){
                        ItemStack itemStack = ((ServerPlayer) pEntity).getItemInHand(hand);
                        if (hand == InteractionHand.MAIN_HAND) {
                            if (itemStack.getItem() instanceof ShieldItem) {
                                itemStack.hurtAndBreak(40, (LivingEntity) pEntity, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            }
                        } else {
                            itemStack.hurtAndBreak(40, (LivingEntity) pEntity, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                        }
                    }
                }
                if(this.getArmorTierValue()<3 && flag){
                    this.setArmorTier(this.getArmorTierValue()+1);
                }
                this.setUpgrading(true);
                this.setAttackSand(true);
                this.scrapTimer =200;
            }
        }
        return super.doHurtTarget(pEntity);
    }

    public boolean isAttackLantern(){
        return this.entityData.get(ATTACKLANTERN);
    }

    public void setAttackSand(boolean pboolean){
        this.entityData.set(ATTACKLANTERN,pboolean);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1,new MinerAttackGoal(this,1.1d,false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7f));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this, Player.class,true,true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }
    static class MinerAttackGoal extends MeleeAttackGoal {
        private final IllagerScavengerEntity goalOwner;

        public MinerAttackGoal(IllagerScavengerEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && this.getTicksUntilNextAttack() <= 0 && this.goalOwner.attackTimer<=0 && !this.goalOwner.isAttacking()) {
                this.resetAttackCooldown();
                this.goalOwner.getNavigation().stop();
                this.goalOwner.getLookControl().setLookAt(entity,180,180);
                this.goalOwner.setYBodyRot(this.goalOwner.getYHeadRot());
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);
        }
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_37845_) {

    }
    public void setAttacking(boolean attacking){
        this.entityData.set(ATTACKING,attacking);
        this.attackTimer = isAttacking() ? 20 : 0;
    }
    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource pDamageSource) {
        if(this.level().getRandom().nextFloat() < 0.2F){
            ItemStack item =new ItemStack(ModItems.GOGGLES_MINER.get());
            item.setDamageValue(item.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(item.getMaxDamage() - 3, 1))));
            this.spawnAtLocation(item);
        }
        if(this.level().getRandom().nextFloat() < 0.3F){
            ItemStack item =new ItemStack(ModItems.JUNK_AXE.get());
            item.setDamageValue(item.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(item.getMaxDamage() - 3, 1))));
            this.spawnAtLocation(item);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller",
                0, this::predicate));
    }

    public boolean isUpgrading(){
        return this.entityData.get(HAS_ITEM);
    }

    public void setUpgrading(boolean pBoolean){
        this.entityData.set(HAS_ITEM,pBoolean);
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

    public ArmorTier getArmorTier(){
        return ArmorTier.byId(this.getArmorTierValue() & 255);
    }

    public int getArmorTierValue(){
        return this.entityData.get(ARMOR_TIER);
    }

    public void setArmorTier(int pTier){
        this.entityData.set(ARMOR_TIER,pTier);
        if(this.getArmorTier()!=ArmorTier.NONE){
            if (!this.level().isClientSide) {
                this.getAttribute(Attributes.ARMOR).removeModifier(SCAVENGER_ARMOR_UUID);
                int i = (pTier*5)+this.getArmorValue();
                if (i != 0) {
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(SCAVENGER_ARMOR_UUID, "scavenger armor bonus", i, AttributeModifier.Operation.ADDITION));
                }
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(2+(pTier*2));
                if(pTier==3){
                    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.3d);
                }
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1+(pTier*7));
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.getArmorTier()==ArmorTier.HEAVY_ARMOR){
            if (pSource.getEntity() instanceof  LivingEntity living){
                living.hurt(this.damageSources().magic(),1f);
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isAttacking()){
            --this.attackTimer;
            if(this.attackTimer!=0){
                if(this.attackTimer==10 && this.getTarget()!=null){
                    if(!this.isAttackLantern()){
                        this.doHurtTarget(this.getTarget());
                    }else{
                        this.useSand =true;
                        if(!this.level().isClientSide){
                            this.sendPacketSand(this,this.getTarget());
                        }
                        this.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,30,1));
                        this.getTarget().addEffect(new MobEffectInstance(MobEffects.CONFUSION,100,1));
                    }
                }
            }else{
                this.setAttacking(false);
                if(this.useSand){
                    this.setAttackSand(false);
                    this.useSand =false;
                }
            }
        }

        if(this.isUpgrading()){
            this.scrapTimer--;
        }

        if(this.scrapTimer ==0 && this.isUpgrading()){
            this.setUpgrading(false);
        }

    }

    public void sendPacketSand(LivingEntity livingEntity,LivingEntity target) {
        if (target instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketSand(livingEntity,target), player);
        }
        PacketHandler.sendToAllTracking(new PacketSand(livingEntity,target),livingEntity);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("upgrade",this.isUpgrading());
        pCompound.putBoolean("attackLantern",this.isAttackLantern());
        pCompound.putBoolean("attacking",this.isAttacking());
        pCompound.putInt("idVariant",this.getTypeIdVariant());
        pCompound.putInt("armorTier",this.getArmorTierValue());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setUpgrading(pCompound.getBoolean("hasItem"));
        this.setAttackSand(pCompound.getBoolean("attackLantern"));
        this.setAttacking(pCompound.getBoolean("attacking"));
        this.setIdVariant(pCompound.getInt("idVariant"));
        this.setArmorTier(pCompound.getInt("armorTier"));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HAS_ITEM,false);
        this.entityData.define(ATTACKLANTERN,false);
        this.entityData.define(ATTACKING,false);
        this.entityData.define(ID_VARIANT,0);
        this.entityData.define(ARMOR_TIER,0);
        super.defineSynchedData();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
        this.playSound(SoundEvents.STONE_STEP, 0.15F, 1.5F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.PILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public enum Variant {
        BROWN(0),
        DARKPURPLE(1),
        DARKGREEN(2),
        DARKBLUE(3),
        DARKGRAY(4),
        GRAY(5);

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

    public enum ArmorTier {
        NONE(0),
        LOW_ARMOR(1),
        MEDIUM_ARMOR(2),
        HEAVY_ARMOR(3);

        private static final ArmorTier[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ArmorTier::getId)).toArray(ArmorTier[]::new);
        private final int id;

        ArmorTier(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static ArmorTier byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }

    }
}
