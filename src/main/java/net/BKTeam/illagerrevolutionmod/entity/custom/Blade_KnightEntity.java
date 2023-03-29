package net.BKTeam.illagerrevolutionmod.entity.custom;

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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.entity.goals.GoalLowhealth;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Entity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Hunter;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Projectile;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.SwordRuneBladeItem;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.procedures.Events;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
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

import java.util.EnumSet;
import java.util.List;


public class Blade_KnightEntity extends SpellcasterKnight implements IAnimatable, InventoryCarrier{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(1);
    private int attackTimer;
    private int attackShield;
    public int lowHealtTimer;
    public float count_expansion;

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        if(pPotioneffect.getEffect()==init_effect.BLEEDING.get()){
            return this.isLowLife();
        }
        return pPotioneffect.getEffect() != init_effect.DEATH_MARK.get() && super.canBeAffected(pPotioneffect);
    }
    private static final EntityDataAccessor<Boolean> STARTANIMATIONLOWHEALTH =
            SynchedEntityData.defineId(Blade_KnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(Blade_KnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKINGSHIELD =
            SynchedEntityData.defineId(Blade_KnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> LOW_LIFE =
            SynchedEntityData.defineId(Blade_KnightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> FASE2 =
            SynchedEntityData.defineId(Blade_KnightEntity.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 35.D)
                .add(Attributes.MOVEMENT_SPEED, 0.31f).build();
    }

    public Blade_KnightEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);
        this.populateDefaultEquipmentSlots(this.level.getCurrentDifficultyAt(this.blockPosition()));
        this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,99999,0));
        this.attackTimer=0;
        this.lowHealtTimer=40;
        this.attackShield=0;
        this.count_expansion=2.0f;
    }
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return super.doHurtTarget(pEntity);
    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        String s1="1";
        String s2="1";
        if(this.isAttackingShield()){
            s1="2";
        }
        if(this.isLowLife() && this.isFase2()){
            s2="2";
        }
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
        this.goalSelector.addGoal(0,new GoalLowhealth(this,60));
        this.goalSelector.addGoal(1,new BKSummonUpUndeadSpellGoal());
        this.goalSelector.addGoal(1,new BKSummonHunterSpellGoal());
        this.goalSelector.addGoal(2, new BkAttackGoal(this,1.0,false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
        this.goalSelector.addGoal(7, new BreakDoorGoal(this, e -> true));

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
                this.setFase2(true);
            }
            if(this.lowHealtTimer<=39 && this.lowHealtTimer>=20){
                if(this.lowHealtTimer==39){
                    for (int i=0;i<6;i++){
                        Vec3 pos=new Vec3(this.getX()+this.level.getRandom().nextDouble(-3.0d,3.0d),this.getY()+3.0D,this.getZ()+this.level.getRandom().nextDouble(-3.0d,3.0d));
                        Player player=this.level.getNearestPlayer(this,40.0D);
                        if(player!=null){
                            Soul_Projectile soul_projectile=new Soul_Projectile(player,player.level,this);
                            soul_projectile.moveTo(pos);
                            this.level.addFreshEntity(soul_projectile);
                        }
                    }
                }
                ParticleOptions particle=ModParticles.BKSOULS_PARTICLES.get();
                level.playLocalSound(this.getX(),this.getY(),this.getZ(),SoundEvents.SOUL_ESCAPE,SoundSource.HOSTILE,5.0f,-5.0f,false);
                for(int i=0;i<5;i++){
                    this.level.addParticle(particle, this.getX(), this.getY() , this.getZ(),  this.level.getRandom().nextFloat(-0.1f,0.1f), this.level.getRandom().nextFloat(0.1f,0.15f),this.level.getRandom().nextFloat(-0.1f,0.1f));
                }
            }
            if(this.lowHealtTimer==0){
                this.setStartAnimationLowHealth(false);
            }
        }
        if(this.isPassenger()){
            this.dismountTo(this.getX(),this.getY(),this.getZ());
        }
    }
    public boolean isAttackingShield(){
        return this.entityData.get(ATTACKINGSHIELD);
    }

    public void setAttackingshield(boolean pboolean){
        this.entityData.set(ATTACKINGSHIELD,pboolean);
        this.attackShield=pboolean ? 600 : this.attackShield;
    }

    public boolean isStartAnimationLowHealth() {
        return this.entityData.get(STARTANIMATIONLOWHEALTH);
    }

    public void setStartAnimationLowHealth(boolean startAnimationLowHealth) {
        this.entityData.set(STARTANIMATIONLOWHEALTH,startAnimationLowHealth);
    }
    public boolean isFase2() {
        return this.entityData.get(FASE2);
    }

    public void setFase2(boolean fase2) {
        this.entityData.set(FASE2 ,fase2);
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
            Blade_KnightEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public void start() {
            super.start();
            Blade_KnightEntity.this.getNavigation().stop();
        }

        @Override
        public boolean canUse() {
            List<Soul_Entity> listentity=Blade_KnightEntity.this.level.getEntitiesOfClass(Soul_Entity.class,Blade_KnightEntity.this.getBoundingBox().inflate(50.0d));
            if(Events.checkOwnerSoul(listentity,Blade_KnightEntity.this)){
                return super.canUse();
            }
            return false;
        }

        @Override
        public void tick() {
            super.tick();
            Blade_KnightEntity.this.getNavigation().stop();
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
            List<Soul_Entity> listentity=Blade_KnightEntity.this.level.getEntitiesOfClass(Soul_Entity.class,Blade_KnightEntity.this.getBoundingBox().inflate(50.0d));
            Entity entity;
            while (i<listentity.size() && i<=5){
                entity=listentity.get(i);
                if(entity instanceof Soul_Entity entity1 && entity1.getOwner()==Blade_KnightEntity.this) {
                    entity1.spawUndead((ServerLevel) Blade_KnightEntity.this.level,Blade_KnightEntity.this,entity);
                }
                i++;
            }
        }
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ILLAGIUM_RUNED_BLADE.get()));
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
            Blade_KnightEntity.this.getNavigation().stop();
        }
        public void stop() {
            super.stop();
            Blade_KnightEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public boolean canUse() {
            if(Blade_KnightEntity.this.getTarget() !=null){
            LivingEntity entity = Blade_KnightEntity.this.getTarget();
            float Dx = distanceTo(entity) ;
            if (!super.canUse()){
                return false;
            }return entity instanceof Player && (entity.getY() > Blade_KnightEntity.this.getY()+3 || Dx > 4.0f);
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
            Blade_KnightEntity.this.getNavigation().stop();
        }

        protected void performSpellCasting() {
            if(Blade_KnightEntity.this.getTarget() instanceof Player)

                {
                    Entity target = Blade_KnightEntity.this.getTarget();
                    Level souce = Blade_KnightEntity.this.getLevel();
                    Entity owner=Blade_KnightEntity.this;

                    level.playLocalSound(owner.getX(),owner.getY(),owner.getZ(),SoundEvents.AMBIENT_NETHER_WASTES_MOOD,SoundSource.HOSTILE,5.0f,-5.0f,false);
                    Soul_Hunter soul_hunter = new Soul_Hunter(Blade_KnightEntity.this,souce);
                    Vec3 pos = Blade_KnightEntity.this.position();
                    Vec3 targetPos = target.position();
                    soul_hunter.setPos(soul_hunter.getX(), soul_hunter.getY() - 0.5, soul_hunter.getZ());
                    soul_hunter.setDeltaMovement(new Vec3(targetPos.x - pos.x, targetPos.y - pos.y, targetPos.z - pos.z).normalize().scale(0.75));
                    souce.addFreshEntity(soul_hunter);
                }

        }
    }
    static class BkAttackGoal extends MeleeAttackGoal {
        private final Blade_KnightEntity goalOwner;

        public BkAttackGoal(Blade_KnightEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
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
        if(!this.hasItemInSlot(EquipmentSlot.MAINHAND)){
            this.populateDefaultEquipmentSlots(this.level.getCurrentDifficultyAt(this.blockPosition()));
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
        this.entityData.define(FASE2,false);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAttacking(compound.getBoolean("isAttacking"));
        this.setAttackingshield(compound.getBoolean("isAttackingShield"));
        this.setLowLife(compound.getBoolean("isLowlife"));
        this.setStartAnimationLowHealth(compound.getBoolean("isLowHealth"));
        this.setFase2(compound.getBoolean("isFase2"));

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isAttacking",this.isAttacking());
        compound.putBoolean("isAttackingShield",this.isAttackingShield());
        compound.putBoolean("isLowlife",this.isLowLife());
        compound.putBoolean("isLowHealth",this.isStartAnimationLowHealth());
        compound.putBoolean("isFase2",this.isFase2());
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
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12.0D);
            this.removeEffect(MobEffects.FIRE_RESISTANCE);
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
        return null;
    }

    @Override
    public Container getInventory() {
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
