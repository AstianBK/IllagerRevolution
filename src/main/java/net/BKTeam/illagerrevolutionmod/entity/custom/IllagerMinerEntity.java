package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.entity.goals.EscapeMinerGoal;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSmoke;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
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

import java.util.EnumSet;


public class IllagerMinerEntity extends AbstractIllager implements IAnimatable, InventoryCarrier {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(5);
    public boolean fistUseInvi;
    public boolean animIdle2;
    private int attackTimer;
    public int robTimer;
    private final int[] listRob =new int[5];

    private static final EntityDataAccessor<Boolean> HAS_ITEM =
            SynchedEntityData.defineId(IllagerMinerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(IllagerMinerEntity.class, EntityDataSerializers.BOOLEAN);

    public IllagerMinerEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
        this.fistUseInvi = false;
        this.animIdle2 = false;
        this.attackTimer = 0;
        this.robTimer = 0;
        for (int i=0;i<5;i++) {
            this.listRob[i]=0;
        }
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_37845_) {

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
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


    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 27.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.30f).build();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        if(this.level.getRandom().nextInt(0,5)==0 ){
            this.spawnAtLocation(ModItems.HELMET_MINER.get());
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (event.isMoving() && !this.isAggressive() && !this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.walk"+(this.isHasItems() ? "3" : ""), ILoopType.EDefaultLoopTypes.LOOP));

        }else if (this.isAggressive() && event.isMoving() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.walk2", ILoopType.EDefaultLoopTypes.LOOP));

        }else if (this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));

        }else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.idle1", ILoopType.EDefaultLoopTypes.LOOP));

        return PlayState.CONTINUE;

    }

    public void setAttacking(boolean attacking){
        this.entityData.set(ATTACKING,attacking);
        this.attackTimer = isAttacking() ? 10 : 0;
    }

    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
    }

    public boolean isHasItems(){
        return this.entityData.get(HAS_ITEM);
    }


    @Override
    protected void dropAllDeathLoot(DamageSource pDamageSource) {
        for(int i=0;i<5;i++){
            if(this.listRob[i]!=0){
                ItemStack stack=new ItemStack(Util.selectItem(i));
                stack.setCount(this.listRob[i]);
                this.spawnAtLocation(stack);
            }
        }
        super.dropAllDeathLoot(pDamageSource);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if(!super.doHurtTarget(pEntity)){
            return false;
        }else {
            int cc=this.robTimer;
            if(pEntity instanceof ServerPlayer player && cc==0){
                int i=0;
                if(this.getRandom().nextInt(2)==1){
                    while(i<player.getInventory().getContainerSize() && !this.isHasItems()){
                        ItemStack itemstack=player.getInventory().getItem(i);
                        if(Util.isItemRob(itemstack.getItem())){
                            int rCount=this.getRandom().nextInt(1,5);
                            if(rCount>itemstack.getCount()){
                                rCount=itemstack.getCount();
                            }
                            this.setHasItem(true);
                            this.listRob[Util.mineralId(itemstack.getItem())]+=rCount;
                            itemstack.shrink(rCount);
                            this.robTimer=500;
                        }
                        i++;
                    }
                }
            }
            return super.doHurtTarget(pEntity);
        }
    }
    public void setHasItem(boolean pBoolean) {
        this.entityData.set(HAS_ITEM,pBoolean);
        if(pBoolean){
            if(!this.level.isClientSide){
                PacketHandler.sendToAllTracking(new PacketSmoke(this),this);
            }
            this.level.playSound(null,this,SoundEvents.FIRE_EXTINGUISH, SoundSource.AMBIENT,5.0f,-1.0f/(random.nextFloat() * 0.4F + 0.8F));
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,150));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isAttacking()){
            this.attackTimer--;
        }
        if(this.attackTimer<0 && this.isAttacking()){
            this.setAttacking(false);
        }
        if(this.isHasItems()){
            this.robTimer--;
        }

        if(this.robTimer==0 && this.isHasItems()){
            this.setHasItem(false);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                10, this::predicate));
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

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MinerAttackGoal(this, 1.1d, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7f));
        this.goalSelector.addGoal(0, new EscapeMinerGoal<>(this, Player.class, 10.0f, 1.0d, 1.5d));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this){
            @Override
            public boolean canUse() {
                return super.canUse() && (this.mob instanceof  IllagerMinerEntity miner && !miner.isHasItems());
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true){
            @Override
            public boolean canUse() {
                return super.canUse() && (this.mob instanceof IllagerMinerEntity miner && !miner.isHasItems());
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING,false);
        this.entityData.define(HAS_ITEM,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasItem",this.isHasItems());
        pCompound.putBoolean("isAttacking",this.isAttacking());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAttacking(pCompound.getBoolean("isAttacking"));
        this.setHasItem(pCompound.getBoolean("hasItem"));
    }

    static class MinerAttackGoal extends MeleeAttackGoal {
        private final IllagerMinerEntity goalOwner;

        public MinerAttackGoal(IllagerMinerEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
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
                this.goalOwner.doHurtTarget(entity);
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
}
