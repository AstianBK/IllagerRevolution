package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.Events;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulSlash;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulHunter;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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


public class BladeKnightEntity extends SpellcasterKnight implements IAnimatable, InventoryCarrier{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(1);
    private static final EntityDataAccessor<Integer> ID_COMBO_STATE =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ID_COMBO =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.INT);

    private static final UUID BLADE_KNIGHT_ATTACK_DAMAGE_UUID= UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");

    @javax.annotation.Nullable
    private LivingEntity clientSideCachedAttackTarget;

    private int animationTimer;

    public int absorbedSouls;

    public boolean continueAnim;
    private Combo oldCombo;

    public int[] timers = new int[]{
            9,
            18,
            10
    };

    private final List<FallenKnightEntity> knights=new ArrayList<>();



    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.85D)
                .add(Attributes.ARMOR, 15.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 40.D)
                .add(Attributes.MOVEMENT_SPEED, 0.31f).build();
    }

    public BladeKnightEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,99999999,0,false,false));
        this.animationTimer=0;
        this.continueAnim=false;
        this.absorbedSouls=0;
        this.oldCombo = Combo.NO_COMBO;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect() != InitEffect.DEATH_MARK.get() && pPotioneffect.getEffect() != InitEffect.DEEP_WOUND.get() && super.canBeAffected(pPotioneffect);
    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isAggressive() && !this.isCastingSpell() && (!this.hasCombo() || !this.continueAnim)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.walk1", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (event.isMoving() && this.isAggressive() && (!this.hasCombo() || !this.continueAnim)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.walkhostile1", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (this.hasCombo() && this.continueAnim) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(this.getCombo().location+(this.getCombo()==Combo.COMBO_SPIN ?this.getComboState().nameAttack1 : this.getComboState().nameAttack2), ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        } else if (this.isCastingSpell()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.summon1", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.idle1", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1,new BKSummonUpUndeadSpellGoal());
        this.goalSelector.addGoal(1,new BKSummonHunterSpellGoal());
        this.goalSelector.addGoal(2, new BkAttackGoal(this,1.0,false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new BreakDoorGoal(this, e -> true));
    }

    public boolean hasCombo(){
        return this.getCombo()!=Combo.NO_COMBO;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.hasCombo()){
            if(this.getCombo()==Combo.COMBO_PERFORATE){
                if(this.getTarget()!=null && !this.continueAnim){
                    LivingEntity target = this.getTarget();
                    double dist = this.distanceToSqr(target.getX(), target.getY(), target.getZ());
                    if(dist<this.getAttackReachSqr(target)){
                        this.setContinueAnim(true);
                        this.level.broadcastEntityEvent(this,(byte) 62);
                    }
                }
            }

            if(this.continueAnim){
                this.animationTimer--;
            }
        }

        int i = this.getIdComboState();
        if (this.animationTimer<0 && this.continueAnim){
            boolean flag = this.hasCombo() && i>0;
            if(flag && this.getCombo()==Combo.COMBO_SPIN ){
                if (i+1<4){
                    this.setIdComboState(i+1);
                }else {
                    this.setIdComboState(0);
                    this.setIdCombo(0);
                }
            }
            if(flag && this.getCombo()==Combo.COMBO_PERFORATE){
                if (i+1<4){
                    this.setIdComboState(i+1);
                    this.setContinueAnim(false);
                }else {
                    this.setIdComboState(0);
                    this.setIdCombo(0);
                }
            }
        }
    }

    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
    }

    protected float getTotalDamage(){
        if(this.getMainHandItem().getItem() instanceof SwordItem sword){
            return sword.getDamage() + (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        return 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (level.random.nextInt(14) == 0 && this.isCastingSpell()) {
            if(this.isCastingSpell()){
                for(int i=0;i<301;i+=30){
                    float f1 = Mth.cos(i);
                    float f2 = Mth.sin(i);
                    this.level.addParticle(ModParticles.RUNE_SOUL_PARTICLES.get(), this.getX() + (double) f1* (0.30f) , this.getY() + 2.9D, this.getZ() + (double) f2 * (0.30f),0.0f,0.0f, 0.0f);
                    this.level.addParticle(ModParticles.RUNE_CURSED_PARTICLES.get(), this.getX() + (double) f1* (0.50f) , this.getY() + 3.4D, this.getZ() + (double) f2 * (0.50f), 0.0f, 0.0f, 0.0f);
                    this.level.addParticle(ModParticles.SOUL_PROJECTILE_PARTICLES.get(), this.getX() + (double) f1* (0.80f) , this.getY() + 3.9D, this.getZ() + (double) f2 * (0.80f), 0.0f, 0.0f, 0.0f);
                }
            }
        }
        if(this.level.isClientSide){
            if (this.hasActiveAttackTarget()) {
                LivingEntity livingentity = this.getActiveAttackTarget();
                if (livingentity != null) {
                    this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
                    this.getLookControl().tick();
                    double d5 = 1.0D;
                    double d0 = livingentity.getX() - this.getX();
                    double d1 = livingentity.getY(0.5D) - this.getEyeY();
                    double d2 = livingentity.getZ() - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 /= d3;
                    d1 /= d3;
                    d2 /= d3;
                    double d4 = this.random.nextDouble();

                    while(d4 < d3) {
                        d4 += 1.8D - d5 + this.random.nextDouble() * (1.7D - d5);
                        //this.level.addParticle(ModParticles.BKSOULS_PARTICLES.get(), this.getX() + d0 * d4, this.getEyeY() + d1 * d4, this.getZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }
    void setActiveAttackTarget(int pEntityId) {
        this.entityData.set(DATA_ID_ATTACK_TARGET, pEntityId);
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
    }

    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientSideCachedAttackTarget = (LivingEntity)entity;
                    return this.clientSideCachedAttackTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (DATA_ID_ATTACK_TARGET.equals(pKey)) {
            this.clientSideCachedAttackTarget = null;
        }

    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.level.random.nextFloat() < 0.5f ? ModItems.ILLAGIUM_RUNED_BLADE.get() : ModItems.ILLAGIUM_ALT_RUNED_BLADE.get()));
        this.setDropChance(EquipmentSlot.MAINHAND,0.10F);
    }

    public List<FallenKnightEntity> getKnights() {
        return this.knights;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pDifficulty);
        if(this.getMainHandItem().is(ModItems.ILLAGIUM_RUNED_BLADE.get())){
            Util.spawZombifiedBack(this.level,this,4);
        }else {
            Util.spawFallenKnightBack(this.level,this,2);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void die(DamageSource pCause) {
        this.knights.forEach(knight->{
            knight.setIdNecromancer(null);
            knight.hurt(DamageSource.MAGIC.bypassMagic().bypassArmor(),knight.getMaxHealth());
        });
        super.die(pCause);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.hasCombo() && this.continueAnim){
            pAmount=pAmount-(pAmount*0.20F);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_COMBO_STATE,0);
        this.entityData.define(ID_COMBO,0);
        this.entityData.define(DATA_ID_ATTACK_TARGET, 0);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIdComboState(compound.getInt("ComboState"));
        this.setIdCombo(compound.getInt("Combo"));
        this.absorbedSouls=compound.getInt("absorbedSouls");
        this.updateDamage();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ComboState",this.getIdComboState());
        compound.putInt("Combo",this.getIdCombo());
        compound.putInt("absorbedSouls",this.absorbedSouls);
    }
    public void setIdComboState(int pId){
        this.entityData.set(ID_COMBO_STATE,pId);
        if(pId>0){
            this.animationTimer = this.getCombo()==Combo.COMBO_PERFORATE ? timers[this.getIdComboState()-1] : 12;
        }else {
            this.animationTimer=0;
        }
    }

    public void updateDamage(){
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.absorbedSouls*0.25F);
    }

    public int getIdComboState() {
        return this.entityData.get(ID_COMBO_STATE);
    }

    public ComboState getComboState() {
        return ComboState.byId(this.getIdComboState() & 255);
    }

    public void setIdCombo(int pId){
        this.entityData.set(ID_COMBO,pId);
        this.setContinueAnim(pId>0);
        this.oldCombo=Combo.byId(pId & 255);
    }



    public int getIdCombo() {
        return this.entityData.get(ID_COMBO);
    }

    public Combo getCombo() {
        return Combo.byId(this.getIdCombo() & 255);
    }

    public void setContinueAnim(boolean continueAnim) {
        this.continueAnim = continueAnim;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==60){
            this.setIdComboState(1);
            this.setIdCombo(1);
        }else if (pId==61){
            this.setIdComboState(1);
            this.setIdCombo(2);
        }else if (pId==62){
            this.setContinueAnim(true);
        }else {
            super.handleEntityEvent(pId);

        }
    }


    public void damageZone(){
        for (int i=0;i<8;i++){
            SoulSlash court = new SoulSlash(this,this.level);
            court.shootFromRotation(this,this.getXRot(),this.getYRot()+(45F*i),0.0F,0.5F,0.1F);
            this.level.addFreshEntity(court);
        }
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.BLADE_KNIGHT_HURT.get();
    }
    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_37845_) {}

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    class BKSummonUpUndeadSpellGoal extends SpellcasterUseSpellGoal {

        BKSummonUpUndeadSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }


        protected int getCastingTime() {
            return 70;
        }

        protected int getCastingInterval() {
            return 250;
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        public void stop() {
            super.stop();
            BladeKnightEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public void start() {
            super.start();
            BladeKnightEntity.this.getNavigation().stop();
        }

        @Override
        public boolean canUse() {
            List<SoulEntity> listentity= BladeKnightEntity.this.level.getEntitiesOfClass(SoulEntity.class, BladeKnightEntity.this.getBoundingBox().inflate(50.0d));
            if(Events.checkOwnerSoul(listentity, BladeKnightEntity.this)){
                return super.canUse() && !BladeKnightEntity.this.hasCombo();
            }
            return false;
        }

        @Override
        public void tick() {
            super.tick();
            BladeKnightEntity.this.getNavigation().stop();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }

        protected void performSpellCasting()
        {
            int i=0;
            List<SoulEntity> listentity= BladeKnightEntity.this.level.getEntitiesOfClass(SoulEntity.class, BladeKnightEntity.this.getBoundingBox().inflate(60.0d));
            Entity entity;
            while (i<listentity.size() && i<=5){
                entity=listentity.get(i);
                if(entity instanceof SoulEntity entity1 && entity1.getOwner()== BladeKnightEntity.this) {
                    entity1.spawUndead((ServerLevel) BladeKnightEntity.this.level, BladeKnightEntity.this,entity,false);
                }
                i++;
            }
        }
    }

    class BKSummonHunterSpellGoal extends SpellcasterUseSpellGoal {

        BKSummonHunterSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }


        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 200;
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        public void start() {
            super.start();
            BladeKnightEntity.this.getNavigation().stop();
        }
        public void stop() {
            super.stop();
            BladeKnightEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public boolean canUse() {
            if(BladeKnightEntity.this.getTarget() !=null){
            LivingEntity entity = BladeKnightEntity.this.getTarget();
            float Dx = distanceTo(entity) ;
                if (!super.canUse()){
                    return false;
                }return entity instanceof Player && (entity.getY() > BladeKnightEntity.this.getY()+3 || Dx > 4.0f) && !BladeKnightEntity.this.hasCombo();
            }
            return false;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.BLINDNESS;
        }

        @Override
        public void tick() {
            super.tick();
            BladeKnightEntity.this.getNavigation().stop();
        }

        protected void performSpellCasting() {
            if(BladeKnightEntity.this.getTarget() instanceof Player) {
                    Entity target = BladeKnightEntity.this.getTarget();
                    Level souce = BladeKnightEntity.this.getLevel();
                    Entity owner= BladeKnightEntity.this;

                    level.playLocalSound(owner.getX(),owner.getY(),owner.getZ(),SoundEvents.AMBIENT_NETHER_WASTES_MOOD,SoundSource.HOSTILE,5.0f,-5.0f,false);
                    SoulHunter soul_hunter = new SoulHunter(BladeKnightEntity.this,souce);
                    Vec3 pos = BladeKnightEntity.this.position();
                    Vec3 targetPos = target.position();
                    soul_hunter.setPos(soul_hunter.getX(), soul_hunter.getY() - 0.5, soul_hunter.getZ());
                    soul_hunter.setDeltaMovement(new Vec3(targetPos.x - pos.x, targetPos.y - pos.y, targetPos.z - pos.z).normalize().scale(0.75));
                    souce.addFreshEntity(soul_hunter);
            }
        }
    }

    static class BkAttackGoal extends MeleeAttackGoal {
        private final BladeKnightEntity goalOwner;

        public BkAttackGoal(BladeKnightEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return super.canUse() || this.goalOwner.hasCombo();
        }

        @Override
        public void stop() {
            super.stop();
            this.goalOwner.setActiveAttackTarget(0);
            this.goalOwner.setContinueAnim(false);
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void tick(){
            if(!this.goalOwner.hasCombo() || !this.goalOwner.continueAnim){
                super.tick();
            }
            if(this.goalOwner.getCombo()!=Combo.NO_COMBO){
                LivingEntity target = this.goalOwner.getTarget();
                boolean flag = this.goalOwner.getCombo()==Combo.COMBO_SPIN;
                if(flag){
                    if(this.goalOwner.animationTimer>5 && target!=null){
                        this.goalOwner.lookAt(target, 30F, 30F);
                    }else {
                        this.goalOwner.setYRot(this.goalOwner.yRotO);
                    }
                    this.goalOwner.navigation.stop();
                    if(this.goalOwner.getComboState()==ComboState.THIRD_HIT){
                        if(this.goalOwner.animationTimer==8){
                            this.goalOwner.damageZone();
                            BlockPos pos = new BlockPos(this.goalOwner.getX(),this.goalOwner.getY()+1.5d,this.goalOwner.getZ());
                            List<Entity> targets = this.goalOwner.level.getEntitiesOfClass(Entity.class,new AABB(pos).inflate(7,7,7), e -> e != this.goalOwner && this.goalOwner.distanceTo(e) <= 3 + e.getBbWidth() / 2f && e.getY() <= this.goalOwner.getY() + 3);
                            for(Entity living : targets){
                                float entityHitDistance = (float) Math.sqrt((living.getZ() - this.goalOwner.getZ()) * (living.getZ() - this.goalOwner.getZ()) + (living.getX() - this.goalOwner.getX()) * (living.getX() - this.goalOwner.getX())) - living.getBbWidth() / 2f;
                                if(living instanceof LivingEntity){
                                    if (entityHitDistance <= 7 - 0.3 ) {
                                        living.hurt(DamageSource.mobAttack(this.goalOwner), 1.0F+this.goalOwner.getTotalDamage()*0.4F);
                                    }
                                }else if(living instanceof Projectile projectile && projectile.getOwner()!=this.goalOwner) {
                                    projectile.shoot(projectile.getX()-this.goalOwner.getX(),projectile.getY()-this.goalOwner.getY(),projectile.getZ()-this.goalOwner.getZ(),1f,0.1f);
                                }

                            }
                        }
                    }else {
                        if(this.goalOwner.animationTimer==5){
                            SoulSlash court = new SoulSlash(this.goalOwner,this.goalOwner.level);
                            court.setPos(new Vec3(court.getX(),court.getY(),court.getZ()));
                            court.shootFromRotation(this.goalOwner,this.goalOwner.getXRot(),this.goalOwner.getYRot(),0.0F,0.5F,0.1F);
                            this.goalOwner.level.addFreshEntity(court);
                            BlockPos pos = new BlockPos(this.goalOwner.getX(),this.goalOwner.getY()+1.5d,this.goalOwner.getZ());
                            List<Entity> targets = this.goalOwner.level.getEntitiesOfClass(Entity.class,new AABB(pos).inflate(7,7,7), e -> e != this.goalOwner && this.goalOwner.distanceTo(e) <= 3 + e.getBbWidth() / 2f && e.getY() <= this.goalOwner.getY() + 3);
                            for(Entity living : targets){
                                float entityHitAngle = (float) ((Math.atan2(living.getZ() - this.goalOwner.getZ(), living.getX() - this.goalOwner.getX()) * (180 / Math.PI) - 90) % 360);
                                float entityAttackingAngle = this.goalOwner.yBodyRot % 360;
                                float arc = 180.0F;
                                if (entityHitAngle < 0) {
                                    entityHitAngle += 360;
                                }
                                if (entityAttackingAngle < 0) {
                                    entityAttackingAngle += 360;
                                }
                                float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                                float entityHitDistance = (float) Math.sqrt((living.getZ() - this.goalOwner.getZ()) * (living.getZ() - this.goalOwner.getZ()) + (living.getX() - this.goalOwner.getX()) * (living.getX() - this.goalOwner.getX())) - living.getBbWidth() / 2f;
                                if(living instanceof  LivingEntity){
                                    if (entityHitDistance <= 7 - 0.3 && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2) ) {
                                        living.hurt(DamageSource.mobAttack(this.goalOwner), 1.0F+this.goalOwner.getTotalDamage()*0.25F);
                                    }
                                }else if(living instanceof Projectile projectile && projectile.getOwner()!=this.goalOwner){
                                    projectile.shoot(projectile.getX()-this.goalOwner.getX(),projectile.getY()-this.goalOwner.getY(),projectile.getZ()-this.goalOwner.getZ(),1f,0.1f);
                                }

                            }
                        }
                    }
                }else {
                    if (this.goalOwner.continueAnim){
                        if(target!=null){
                            if(this.goalOwner.animationTimer>5){
                                this.goalOwner.lookAt(target, 30F, 30F);
                            }else {
                                this.goalOwner.setYRot(this.goalOwner.yRotO);
                            }
                            if (this.goalOwner.getComboState()==ComboState.FIRST_HIT){
                                if(this.goalOwner.animationTimer==5){
                                    this.goalOwner.doHurtTarget(target);
                                }
                            }else if(this.goalOwner.getComboState()==ComboState.SECOND_HIT){
                                if(this.goalOwner.animationTimer==10){
                                    this.goalOwner.doHurtTarget(target);
                                    if(target.isBlocking() && target instanceof Player pTarget){
                                        pTarget.disableShield(true);
                                    }
                                }
                            }else if(this.goalOwner.getComboState()==ComboState.THIRD_HIT){
                                if(this.goalOwner.animationTimer==5){
                                    BlockPos pos = new BlockPos(this.goalOwner.getX(),this.goalOwner.getY()+1.5d,this.goalOwner.getZ());
                                    List<LivingEntity> targets = this.goalOwner.level.getEntitiesOfClass(LivingEntity.class,new AABB(pos).inflate(10,10,10), e -> e != this.goalOwner && this.goalOwner.distanceTo(e) <= 4 + e.getBbWidth() / 2f && e.getY() <= this.goalOwner.getY() + 3);
                                    for(LivingEntity living : targets){
                                        float entityHitAngle = (float) ((Math.atan2(living.getZ() - this.goalOwner.getZ(), living.getX() - this.goalOwner.getX()) * (180 / Math.PI) - 90) % 360);
                                        float entityAttackingAngle = this.goalOwner.yBodyRot % 360;
                                        float arc = 60.0F;
                                        if (entityHitAngle < 0) {
                                            entityHitAngle += 360;
                                        }
                                        if (entityAttackingAngle < 0) {
                                            entityAttackingAngle += 360;
                                        }
                                        float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                                        float entityHitDistance = (float) Math.sqrt((living.getZ() - this.goalOwner.getZ()) * (living.getZ() - this.goalOwner.getZ()) + (living.getX() - this.goalOwner.getX()) * (living.getX() - this.goalOwner.getX())) - living.getBbWidth() / 2f;
                                        if (entityHitDistance <= 10 - 0.3 && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2) ) {
                                            if(living.isBlocking() && living instanceof Player player){
                                                player.disableShield(true);
                                            }
                                            living.hurt(DamageSource.mobAttack(this.goalOwner).bypassArmor(), 1.0F+this.goalOwner.getTotalDamage()*0.5F);
                                            living.addEffect(new MobEffectInstance(InitEffect.DEEP_WOUND.get(),100,1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity) + 10.0D;
            if (distance <= d0 && this.goalOwner.animationTimer <= 0 && !this.goalOwner.hasCombo()) {
                this.resetAttackCooldown();
                this.goalOwner.getLookControl().setLookAt(entity,30,30);
                this.goalOwner.setYBodyRot(this.goalOwner.getYHeadRot());
            }
        }

        @Override
        protected void resetAttackCooldown() {
            this.goalOwner.setIdComboState(1);
            int i;
            if(this.goalOwner.oldCombo!=Combo.NO_COMBO){
                i = this.goalOwner.oldCombo==Combo.COMBO_PERFORATE ? 1 : 2;
            }else {
                i = this.goalOwner.level.random.nextBoolean() ? 1 : 2;
            }
            this.goalOwner.setIdCombo(i);
            if(i==1){
                this.goalOwner.level.broadcastEntityEvent(this.goalOwner,(byte) 60);
            }else {
                this.goalOwner.level.broadcastEntityEvent(this.goalOwner,(byte) 61);
            }
        }
    }

    public enum ComboState{
        FIRST_HIT(1,"halfspin1","push"),
        SECOND_HIT(2,"halfspin2","shieldbreak"),
        THIRD_HIT(3,"fullspin","stab"),
        CANCEL_COMBO(0,null,null);

        private static final BladeKnightEntity.ComboState[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(BladeKnightEntity.ComboState::getId)).toArray(BladeKnightEntity.ComboState[]::new);
        private final int  id;

        private final String nameAttack1;

        private final String nameAttack2;



        ComboState(int pId,String nameAttack1,String nameAttack2){
            this.id = pId;
            this.nameAttack1 = nameAttack1;
            this.nameAttack2 = nameAttack2;
        }

        public int getId(){
            return this.id;
        }

        public String getNameAttack1() {
            return this.nameAttack1;
        }

        public String getNameAttack2() {
            return this.nameAttack2;
        }

        public static ComboState byId(int pId){
            return BY_ID[pId % BY_ID.length];
        }
    }

    public enum Combo{
        COMBO_SPIN(1,"animation.IllagerBladeKnight.combo2"),
        COMBO_PERFORATE(2,"animation.IllagerBladeKnight.combo1"),
        NO_COMBO(0,null);

        private static final BladeKnightEntity.Combo[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(BladeKnightEntity.Combo::getId)).toArray(BladeKnightEntity.Combo[]::new);

        private final int id;

        private final String location;

        Combo(int id, String location){
            this.id=id;
            this.location=location;
        }

        public int getId() {
            return this.id;
        }

        public static Combo byId(int pId){
            return BY_ID[pId % BY_ID.length];
        }
    }
}
