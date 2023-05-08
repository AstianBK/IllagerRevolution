package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IItemCapability;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.entity.goals.HurtByTargetGoalIllager;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
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

import java.util.*;


public class IllagerScavengerEntity extends AbstractIllager implements IAnimatable, InventoryCarrier {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(6);
    private static final UUID SCAVENGER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private boolean useArena;

    private int robTimer;

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
        this.robTimer=0;
        this.attackTimer=0;
        this.useArena =false;
    }
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.JUNK_AXE.get()));
        this.setIdVariant(this.level.random.nextInt(0,6));
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
        if(this.level.getRandom().nextFloat() < 0.2){
            ItemStack item =new ItemStack(ModItems.GOGGLES_MINER.get());
            item.setDamageValue(item.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(item.getMaxDamage() - 3, 1))));
            this.spawnAtLocation(item);
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR,5.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 0.31f).build();

    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isAggressive() && !this.isAttacking() && !this.isAttackLantern()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.walk"+(this.isUpgrading() ? "3" : ""), ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isAttacking() && this.isAttackLantern()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.attack2", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        else if (this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        else if (this.isAggressive() && event.isMoving() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        } else if (this.isSprinting() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.walk3", ILoopType.EDefaultLoopTypes.LOOP));

        } else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;

    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if(!super.doHurtTarget(pEntity)){
            return false;
        }else {
                int cc=this.robTimer;
                if(pEntity instanceof ServerPlayer && cc==0){
                    if(this.getRandom().nextInt(2)==1){
                        if(this.getArmorTierValue()<3){
                            this.setArmorTier(this.getArmorTierValue()+1);
                            this.setUpgrading(true);
                        }
                        this.setAttackArena(true);
                        this.robTimer=200;
                        for(ItemStack stack : pEntity.getArmorSlots()){
                            if(stack.getItem() instanceof ArmorItem ){
                                if(!stack.isEmpty()){
                                    stack.hurtAndBreak(40,(LivingEntity)pEntity,e-> e.broadcastBreakEvent(stack.getEquipmentSlot()));
                                }
                            }
                        }
                    }
                }
                return super.doHurtTarget(pEntity);
        }
    }

    public boolean isAttackLantern(){
        return this.entityData.get(ATTACKLANTERN);
    }

    public void setAttackArena(boolean pboolean){
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
        this.attackTimer = isAttacking() ? 10 : 0;
    }
    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource pDamageSource) {
        super.dropAllDeathLoot(pDamageSource);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
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
            if (!this.level.isClientSide) {
                this.getAttribute(Attributes.ARMOR).removeModifier(SCAVENGER_ARMOR_UUID);
                int i = (pTier*5)+this.getArmorValue();
                if (i != 0) {
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(SCAVENGER_ARMOR_UUID, "Raker armor bonus", i, AttributeModifier.Operation.ADDITION));
                }
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(2+(pTier*2));
                this.upgradeWeapon(pTier);
            }
        }
    }

    public void upgradeWeapon(int pTier){
        if(!this.level.isClientSide){
            IItemCapability capability= CapabilityHandler.getItemCapability(this.getMainHandItem(),CapabilityHandler.SWORD_CAPABILITY);
            if(capability!=null){
                capability.setTier(pTier);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.getArmorTier()==ArmorTier.HEAVY_ARMOR){
            if (pSource.getEntity() instanceof  LivingEntity living){
                living.hurt(DamageSource.MAGIC,3f);
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
                if(this.attackTimer==7 && this.getTarget()!=null){
                    if(!this.isAttackLantern()){
                        this.doHurtTarget(this.getTarget());
                    }else{
                        this.useArena =true;
                        if(!this.level.isClientSide){
                            PacketHandler.sendToPlayer(new PacketSand(this.getTarget()), (ServerPlayer) this.getTarget());
                        }
                        this.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,50,1));
                        this.getTarget().addEffect(new MobEffectInstance(MobEffects.CONFUSION,100,1));
                    }
                }
            }else{
                this.setAttacking(false);
                if(this.useArena){
                    this.setAttackArena(false);
                    this.useArena=false;
                }
            }
        }

        if(this.isUpgrading()){
            this.robTimer--;
        }

        if(this.robTimer==0 && this.isUpgrading()){
            this.setUpgrading(false);
        }

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
        this.setAttackArena(pCompound.getBoolean("attackLantern"));
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
    public AnimationFactory getFactory() {
        return this.factory;
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

        private static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(IllagerScavengerEntity.Variant::getId)).toArray(Variant[]::new);
        private final int id;

        Variant(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static IllagerScavengerEntity.Variant byId(int p_30987_) {
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

        public static IllagerScavengerEntity.ArmorTier byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }

    }
}
