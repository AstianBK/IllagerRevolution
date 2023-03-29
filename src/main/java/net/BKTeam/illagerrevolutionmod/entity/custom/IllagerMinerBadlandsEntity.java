package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.entity.goals.EscapeMinerGoal;
import net.BKTeam.illagerrevolutionmod.entity.goals.HurtByTargetGoalIllager;
import net.BKTeam.illagerrevolutionmod.entity.goals.NearestAttackableTargetGoalIllager;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
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

import java.util.EnumSet;


public class IllagerMinerBadlandsEntity extends AbstractIllager implements IAnimatable, InventoryCarrier {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(6);

    private final int[] listRob =new int[5];

    private boolean useLantern;

    private int robTimer;

    private int attackTimer;

    private boolean isEscape;

    private static final EntityDataAccessor<Boolean> HAS_ITEM =
            SynchedEntityData.defineId(IllagerMinerBadlandsEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(IllagerMinerBadlandsEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKLANTERN =
            SynchedEntityData.defineId(IllagerMinerBadlandsEntity.class, EntityDataSerializers.BOOLEAN);
    public IllagerMinerBadlandsEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
        this.populateDefaultEquipmentSlots(this.level.getCurrentDifficultyAt(this.blockPosition()));
        this.robTimer=0;
        this.attackTimer=0;
        this.useLantern=false;
        this.isEscape=false;
        for (int i=0;i<5;i++) {
            this.listRob[i]=0;
        }
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
        if(this.level.getRandom().nextInt(0,5)==0 && !(this instanceof IllagerMinerEntity)){
            this.spawnAtLocation(ModItems.GOGGLES_MINER.get());
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.30f).build();

    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        String s1="";
        if(this.isEscape){
            s1="3";
        }

        if (event.isMoving() && !this.isAggressive() && !this.isAttacking() && !this.isAttackLantern()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.walk"+s1, ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isAttacking() && this.isAttackLantern()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.attack2", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        else if (this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }
        else if (this.isAggressive() && event.isMoving() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminerbadlands.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;

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
                            this.setAttacklantern(true);
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
    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
    }

    public boolean isAttackLantern(){
        return this.entityData.get(ATTACKLANTERN);
    }
    public void setAttacklantern(boolean pboolean){
        this.entityData.set(ATTACKLANTERN,pboolean);
    }

    public void setEscape(boolean escape) {
        this.isEscape = escape;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1,new MinerAttackGoal(this,1.1d,false));
        this.goalSelector.addGoal(0,new EscapeMinerGoal<>(this, Player.class,8.0f,1.1D,1.5D));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7f));
        this.targetSelector.addGoal(4, new HurtByTargetGoalIllager(this));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoalIllager<>(this, Player.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }
    static class MinerAttackGoal extends MeleeAttackGoal {
        private final IllagerMinerBadlandsEntity goalOwner;

        public MinerAttackGoal(IllagerMinerBadlandsEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
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
    public SoundEvent getCelebrateSound() {
        return null;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }
    public boolean isHasItems(){
        return this.entityData.get(HAS_ITEM);
    }
    public void setHasItem(boolean pBoolean){
        this.entityData.set(HAS_ITEM,pBoolean);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.isAttacking()){
            --this.attackTimer;
            if(this.attackTimer!=0){
                if(this instanceof IllagerMinerEntity){
                    if(this.attackTimer==4 && this.getTarget()!=null){
                        this.doHurtTarget(this.getTarget());
                        this.setAttacklantern(false);
                    }
                }else if(this.attackTimer==4 && this.getTarget()!=null){
                    if(!this.isHasItems() && !this.isAttackLantern()){
                        this.doHurtTarget(this.getTarget());
                    }else{
                        this.useLantern=true;
                        this.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS,50,1));
                        this.getTarget().setSecondsOnFire(5);
                    }
                }
            }else{
                this.setAttacking(false);
                if(this.useLantern){
                    this.setAttacklantern(false);
                    this.useLantern=false;
                }
            }
        }
        int cc = this.robTimer;
        if(this.isHasItems()){
            if(cc==0){
                this.setHasItem(false);
            }else{
                --cc;
                this.robTimer=cc;
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasItem",this.isHasItems());
        pCompound.putBoolean("attackLantern",this.isAttackLantern());
        pCompound.putBoolean("attacking",this.isAttacking());
        for (int i=0;i<5;i++){
            String s1=Integer.toString(i);
            pCompound.putInt("count"+s1,this.listRob[i]);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setHasItem(pCompound.getBoolean("hasItem"));
        this.setAttacklantern(pCompound.getBoolean("attackLantern"));
        this.setAttacking(pCompound.getBoolean("attacking"));
        for (int i=0;i<5;i++){
            String s1=Integer.toString(i);
            this.listRob[i]=pCompound.getInt("count"+s1);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HAS_ITEM,false);
        this.entityData.define(ATTACKLANTERN,false);
        this.entityData.define(ATTACKING,false);
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
    public @NotNull Container getInventory() {
        return this.inventory;
    }
}
