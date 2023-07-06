package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.Events;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.goals.GoalLowhealth;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulHunter;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulProjectile;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.SwordRuneBladeItem;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;


public class BladeKnightEntity extends SpellcasterKnight implements IAnimatable, InventoryCarrier{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(1);
    private int attackTimer;
    private int attackShield;
    public int lowHealtTimer;
    public float count_expansion;
    private final List<FallenKnightEntity> knights=new ArrayList<>();

    private static final EntityDataAccessor<Boolean> STARTANIMATIONLOWHEALTH =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKINGSHIELD =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> LOW_LIFE =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> PHASE2 =
            SynchedEntityData.defineId(BladeKnightEntity.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.85D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 36.D)
                .add(Attributes.MOVEMENT_SPEED, 0.31f).build();
    }

    public BladeKnightEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);
        this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,99999999,0,false,false));
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,99999999,0,false,false));
        this.attackTimer=0;
        this.lowHealtTimer=40;
        this.attackShield=0;
        this.count_expansion=2.0f;
    }
    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        if(pPotioneffect.getEffect()== InitEffect.DEEP_WOUND.get()){
            return this.isLowLife();
        }
        return pPotioneffect.getEffect() != InitEffect.DEATH_MARK.get() && super.canBeAffected(pPotioneffect);
    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        String s1=this.isAttackingShield() ? "2"  : "1";
        String s2=this.isLowLife() && this.isPhase2()? "2" : "1";
        if (event.isMoving() && !this.isAggressive() && !this.isAttacking() && !this.isCastingSpell() && !this.isStartAnimationLowHealth()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.walk" + s2, ILoopType.EDefaultLoopTypes.LOOP));

        }else if (this.isStartAnimationLowHealth()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.lowhealth", ILoopType.EDefaultLoopTypes.PLAY_ONCE));

        }else if (event.isMoving() && this.isAggressive() && !this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.walkhostile"+s2, ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.attack"+s1, ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isCastingSpell()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.summon1", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.IllagerBladeKnight.idle"+s2, ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1,new GoalLowhealth(this,60));
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

    @Override
    public void tick() {
        super.tick();
        Random random1 = new Random();
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
        if(this.isStartAnimationLowHealth()){
            --this.lowHealtTimer;
            if(this.lowHealtTimer==50 || this.lowHealtTimer==45 || this.lowHealtTimer==40){
                this.playSound(SoundEvents.FIRECHARGE_USE,3.0f,0.0f);
                for(int i=0;i<300;i+=10){
                    float f4 = Mth.cos(i)*(1.0f+this.count_expansion);
                    float f5 = Mth.sin(i)*(1.0f+this.count_expansion);
                    this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + (double) f4, this.getY()+0.2d , this.getZ() + (double) f5,  0.0f, 0.0f,0.0f);
                }
                this.count_expansion--;
            }
            if(this.lowHealtTimer==3){
                this.setPhase2(true);
            }
            if(this.lowHealtTimer<=39 && this.lowHealtTimer>=20){
                if(this.lowHealtTimer==39){
                    if(this.getMainHandItem().is(ModItems.ILLAGIUM_RUNED_BLADE.get())){
                        Util.spawZombifiedBack(this.level,this,4);
                    }else {
                        Util.spawFallenKnightBack(this.level,this,2);
                    }
                    for (int i=0;i<6;i++){
                        Vec3 pos=new Vec3(this.getX()+random1.nextDouble(-3.0d,3.0d),this.getY()+3.0D,this.getZ()+random1.nextDouble(-3.0d,3.0d));
                        Player player=this.level.getNearestPlayer(this,40.0D);
                        if(player!=null){
                            SoulProjectile soul_projectile=new SoulProjectile(player,player.level,this);
                            soul_projectile.moveTo(pos);
                            this.level.addFreshEntity(soul_projectile);
                        }
                    }
                }
                ParticleOptions particle=ModParticles.BKSOULS_PARTICLES.get();
                level.playLocalSound(this.getX(),this.getY(),this.getZ(),SoundEvents.SOUL_ESCAPE,SoundSource.HOSTILE,5.0f,-5.0f,false);
                for(int i=0;i<3;i++){
                    float f = this.yBodyRot * ((float) Math.PI / 180F);
                    float f1 = Mth.cos(f);
                    float f2 = Mth.sin(f);
                    float f3 = 0.3f;
                    this.level.addParticle(particle, this.getX()-(f2*f3), this.getY() , this.getZ()+(f1*f3),  random1.nextFloat(-0.1f,0.1f), random1.nextFloat(0.1f,0.15f),random1.nextFloat(-0.1f,0.1f));
                }
            }
            if(this.lowHealtTimer==0){
                this.setStartAnimationLowHealth(false);
            }
        }
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

    public boolean isAttackingShield(){
        return this.entityData.get(ATTACKINGSHIELD);
    }

    public void setAttackingshield(boolean pboolean){
        this.entityData.set(ATTACKINGSHIELD,pboolean);
        this.attackShield=pboolean ? 600 : this.attackShield;
    }

    @Override
    public void die(DamageSource pCause) {
        this.knights.forEach(knight->{
            knight.setIdNecromancer(null);
            knight.hurt(DamageSource.MAGIC.bypassMagic().bypassArmor(),knight.getMaxHealth());
        });
        super.die(pCause);
    }

    public boolean isStartAnimationLowHealth() {
        return this.entityData.get(STARTANIMATIONLOWHEALTH);
    }

    public void setStartAnimationLowHealth(boolean startAnimationLowHealth) {
        this.entityData.set(STARTANIMATIONLOWHEALTH,startAnimationLowHealth);
    }
    public boolean isPhase2() {
        return this.entityData.get(PHASE2);
    }

    public void setPhase2(boolean phase2) {
        this.entityData.set(PHASE2 ,phase2);
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!this.isLowLife()){
            if(pSource.getEntity() instanceof Player player){
                if(player.getMainHandItem().getItem() instanceof TieredItem Item && Util.detectorTier(Item,3)){
                    player.level.playSound(player,player.blockPosition(),SoundEvents.ANVIL_PLACE, SoundSource.HOSTILE,0.2f,-1.5f);
                    playSound(ModSounds.BLADE_KNIGHT_LAUGH.get() ,5.0f,1.0f);
                    return false;
                }
                if (player.getMainHandItem().getItem() instanceof SwordRuneBladeItem){
                    pAmount=80;
                }
            }
            if(pSource.getEntity() instanceof AbstractArrow abstractArrow){
                pAmount=abstractArrow.isCritArrow() ? 3.0f : 2.0f;
            }
            if (pAmount>=4.0f){
                pAmount=4.0f;
            }

        }
        return super.hurt(pSource, pAmount);
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
                return super.canUse();
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
            List<SoulEntity> listentity= BladeKnightEntity.this.level.getEntitiesOfClass(SoulEntity.class, BladeKnightEntity.this.getBoundingBox().inflate(50.0d));
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

    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.level.random.nextFloat() < 0.5f ? ModItems.ILLAGIUM_RUNED_BLADE.get() :ModItems.ILLAGIUM_ALT_RUNED_BLADE.get()));
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
            }return entity instanceof Player && (entity.getY() > BladeKnightEntity.this.getY()+3 || Dx > 4.0f);
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
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity) + 10.0D;
            if (distance <= d0 && this.getTicksUntilNextAttack() <= 0 && this.goalOwner.attackTimer<=0 && !this.goalOwner.isAttacking()) {
                if(entity instanceof Player player && player.isBlocking() && this.goalOwner.attackShield==0 && !this.goalOwner.isLowLife()){
                    this.goalOwner.setAttackingshield(true);
                }
                this.resetAttackCooldown();
                this.goalOwner.getNavigation().stop();
                this.goalOwner.getLookControl().setLookAt(entity,180,180);
                this.goalOwner.setYBodyRot(this.goalOwner.getYHeadRot());
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);;
        }
    }

    public void aiStep() {
        super.aiStep();
        if(this.getHealth() < this.getMaxHealth()*50/100 && !this.isLowLife()){
            this.setLowLife(true);
        }
        if(this.attackShield>0){
            this.attackShield--;
        }
        if(this.isAttacking()){
            --this.attackTimer;
            if(this.attackTimer!=0){
                if(this.attackTimer==4 && this.getTarget()!=null){
                    if(this.isAttackingShield()){
                        ((Player)this.getTarget()).disableShield(true);
                        this.playSound(SoundEvents.ANVIL_BREAK, 4.0F, -1.0F);
                        this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                    }else{
                        this.doHurtTarget(this.getTarget());
                        SoundEvent Sound =this.level.getRandom().nextInt(0,2)==1 ? ModSounds.BLADE_KNIGHT_SWORDHIT1.get():ModSounds.BLADE_KNIGHT_SWORDHIT2.get();
                        this.playSound(Sound, 1.2F, 1.0F);
                    }
                }
            }else{
                this.setAttacking(false);
                this.setAttackingshield(false);
            }
        }
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING,false);
        this.entityData.define(ATTACKINGSHIELD,false);
        this.entityData.define(LOW_LIFE,false);
        this.entityData.define(STARTANIMATIONLOWHEALTH,false);
        this.entityData.define(PHASE2,false);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAttacking(compound.getBoolean("isAttacking"));
        this.setAttackingshield(compound.getBoolean("isAttackingShield"));
        this.setLowLife(compound.getBoolean("isLowlife"));
        this.setStartAnimationLowHealth(compound.getBoolean("isLowHealth"));
        this.setPhase2(compound.getBoolean("isphase2"));

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isAttacking",this.isAttacking());
        compound.putBoolean("isAttackingShield",this.isAttackingShield());
        compound.putBoolean("isLowlife",this.isLowLife());
        compound.putBoolean("isLowHealth",this.isStartAnimationLowHealth());
        compound.putBoolean("isphase2",this.isPhase2());
        }

    public void setAttacking(boolean attacking){
        this.entityData.set(ATTACKING,attacking);
        this.attackTimer = isAttacking() ? 10 : 0;
    }
    public void setLowLife(boolean lowLife){
        this.entityData.set(LOW_LIFE,lowLife);
        this.lowHealtTimer=lowLife ? 60 : 0;
        if(lowLife){
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.36D);
            this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(1.5D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(15.0D);
            this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(3.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(10.0D);
            this.removeEffect(MobEffects.FIRE_RESISTANCE);
            this.removeEffect(MobEffects.WATER_BREATHING);
        }
    }
    public boolean  isLowLife(){
        return this.entityData.get(LOW_LIFE);
    }
    public boolean isAttacking(){
        return this.entityData.get(ATTACKING);
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
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
