package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketStopSound;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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


public class SoulSageEntity extends SpellcasterKnight implements IAnimatable, InventoryCarrier{
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private final SimpleContainer inventory = new SimpleContainer(1);

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET_0 =
            SynchedEntityData.defineId(SoulSageEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET_1 =
            SynchedEntityData.defineId(SoulSageEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET_2 =
            SynchedEntityData.defineId(SoulSageEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DRAIN_SOUL =
            SynchedEntityData.defineId(SoulSageEntity.class, EntityDataSerializers.BOOLEAN);

    @javax.annotation.Nullable
    private LivingEntity clientSideDrainTarget0;

    @javax.annotation.Nullable
    private LivingEntity clientSideDrainTarget1;

    @javax.annotation.Nullable
    private LivingEntity clientSideDrainTarget2;

    private int drainDuration;

    public int absorbedSouls;
    private int soundDrianTick;

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 60.D)
                .add(Attributes.MOVEMENT_SPEED, 0.30f).build();
    }

    public SoulSageEntity(EntityType<? extends SpellcasterKnight> entityType, Level level) {
        super(entityType, level);
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,99999999,0,false,false));
        this.absorbedSouls=0;
        this.drainDuration=0;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect() != InitEffect.DEATH_MARK.get()
                && super.canBeAffected(pPotioneffect);
    }

    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isSprinting() && !this.isCastingSpell()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.soulsage.walk1", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (event.isMoving() && this.isSprinting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.soulsage.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        } else if (this.isDrainSoul()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.soulsage.channel", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (this.isCastingSpell() && this.getCurrentSpell() == IllagerSpell.SUMMON_VEX){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.soulsage.spellshield", ILoopType.EDefaultLoopTypes.LOOP));
        }else if (this.isCastingSpell() && this.getCurrentSpell() == IllagerSpell.BLINDNESS){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.soulsage.spellbomb", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.soulsage.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3,new DrainSpellGoal());
        this.goalSelector.addGoal(1,new ShieldSpellGoal());
        this.goalSelector.addGoal(2,new ShootSoulSpellGoal());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(3,new AvoidEntityGoal<LivingEntity>(this,LivingEntity.class,5.0F,1.0D,1.5D){
            @Override
            public boolean canUse() {
                return super.canUse() && this.toAvoid==this.mob.getTarget();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new BreakDoorGoal(this, e -> true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.tickCount%300==0){
            this.spawSoulBomb(1);
        }
        if(this.isDrainSoul()){
            this.getNavigation().stop();
            List<LivingEntity> list = this.getDrainEntities();
            if(this.drainDuration > 0 ) {
                this.drainDuration--;
                if(this.tickCount%20==0){
                    if(!list.isEmpty()){
                        int i = 0;
                        for(LivingEntity target : list){
                            if (this.checkIsAlive(target,i)){
                                float f=Mth.clamp(1.0F+0.25F*this.absorbedSouls,1.0F,2.0f);
                                float f1=1.0F;
                                if(list.size()>1){
                                    f1=0.5F*list.size()-1;
                                }
                                if(target instanceof Player pPlayer){
                                    int j = (int) pPlayer.getAttribute(SoulTick.SOUL).getValue();
                                    if(j>0 && this.random.nextFloat()<0.2F){
                                        pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(j-1);
                                        this.spawSoulBomb(1);
                                    }
                                    //sonido cuando roba alma.
                                    pPlayer.playSound(SoundEvents.VILLAGER_NO,1.0F,1.0F);
                                }
                                if(target.hurt(DamageSource.mobAttack(this).setMagic(),f)){
                                    //sonido cuando chupa vida.
                                    target.playSound(SoundEvents.VILLAGER_NO,1.0F,1.0F);
                                    this.heal(Mth.clamp(f*f1,1.0F,f));
                                    this.absorbedSouls++;
                                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,1));
                                    this.addEffect(new MobEffectInstance(MobEffects.CONFUSION,100,0));
                                }

                            }
                            i++;
                        }
                    }
                }else {
                    this.refreshTargetsDrain(list);
                }
                if(this.drainDuration==0){
                    this.setActiveAttackTarget(0,3);
                    this.setDrainSoul(false);
                }
            }
        }
    }

    public void spawSoulBomb(int cantSpaw){
        List<SoulBomb> soulBombs = this.level.getEntitiesOfClass(SoulBomb.class,this.getBoundingBox().inflate(3.0D),e->e.getOwnerID()==this.getId() && !e.isDefender());
        int i = soulBombs.size();
        cantSpaw = Mth.clamp(cantSpaw,0,6-i);
        if(i<6){
            for (int k = 0; k<cantSpaw ; k++){
                SoulBomb soul = new SoulBomb(this,this.level,i+1);
                soul.setPosition(this);
                soul.setOwner(this);
                soul.setPowerLevel(3);
                this.level.addFreshEntity(soul);
                this.rePositionSoulBomb();
                i++;
            }
        }
    }

    public void rePositionSoulBomb(){
        List<SoulBomb> soulBombs = this.level.getEntitiesOfClass(SoulBomb.class,this.getBoundingBox().inflate(3.0D),e->e.getOwnerID()==this.getId() && !e.isDefender());
        int i = 0;
        for(SoulBomb bombs : soulBombs){
            bombs.setPositionSummon(i+1);
            i++;
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.random.nextFloat()<0.10){
            ItemStack drop = new ItemStack(ModItems.OMINOUS_GRIMOIRE.get());
            this.spawnAtLocation(drop);
        }
    }

    public void refreshTargetsDrain(List<LivingEntity> targets){
        int i = 0;
        for(LivingEntity target : targets){
            this.checkIsAlive(target,i);
            i++;
        }
    }

    public boolean checkIsAlive(LivingEntity living,int index){
        if (living!=null){
            if(living.isAlive()){
                double dist = this.distanceTo(living);
                if(dist<60){
                    return true;
                }else {
                    this.setActiveAttackTarget(0,index);
                    return false;
                }
            }else if(living.getLastHurtByMob()==this){
                this.spawSoulBomb(1);
            }
        }
        this.setActiveAttackTarget(0,index);
        return false;
    }

    public List<LivingEntity> getDrainEntities(){
        List<LivingEntity> list = new ArrayList<>();
        LivingEntity target0 = this.getActiveAttackTarget0();
        LivingEntity target1 = this.getActiveAttackTarget1();
        LivingEntity target2 = this.getActiveAttackTarget2();
        list.add(0,target0);
        list.add(1,target1);
        list.add(2,target2);
        return list;
    }

    void setActiveAttackTarget(int pEntityId,int pIndex) {
        switch (pIndex){
            case 0-> this.entityData.set(DATA_ID_ATTACK_TARGET_0, pEntityId);
            case 1-> this.entityData.set(DATA_ID_ATTACK_TARGET_1, pEntityId);
            case 2-> this.entityData.set(DATA_ID_ATTACK_TARGET_2, pEntityId);
            case 3-> {
                this.entityData.set(DATA_ID_ATTACK_TARGET_0, 0);
                this.entityData.set(DATA_ID_ATTACK_TARGET_1, 0);
                this.entityData.set(DATA_ID_ATTACK_TARGET_2, 0);
            }
        }
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET_0) != 0 ||
                this.entityData.get(DATA_ID_ATTACK_TARGET_1)!= 0 ||
                this.entityData.get(DATA_ID_ATTACK_TARGET_2)!= 0;
    }

    public LivingEntity getActiveAttackTarget0() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideDrainTarget0 != null) {
                return this.clientSideDrainTarget0;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_0));

                if (entity instanceof LivingEntity) {
                    this.clientSideDrainTarget0 = (LivingEntity)entity;
                    return this.clientSideDrainTarget0;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public LivingEntity getActiveAttackTarget1() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideDrainTarget1 != null) {
                return this.clientSideDrainTarget1;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_1));

                if (entity instanceof LivingEntity) {
                    this.clientSideDrainTarget1 = (LivingEntity)entity;
                    return this.clientSideDrainTarget1;
                } else {
                    return null;
                }
            }
        } else {
            return this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_1)) instanceof LivingEntity ?
                    (LivingEntity) this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_1)) :
                    null;
        }
    }

    public LivingEntity getActiveAttackTarget2() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideDrainTarget2 != null) {
                return this.clientSideDrainTarget2;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_2));

                if (entity instanceof LivingEntity) {
                    this.clientSideDrainTarget2 = (LivingEntity)entity;
                    return this.clientSideDrainTarget2;
                } else {
                    return null;
                }
            }
        } else {
            Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET_2));
            return entity instanceof LivingEntity ? (LivingEntity) entity : null;
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (DATA_ID_ATTACK_TARGET_0.equals(pKey)) {
            this.clientSideDrainTarget0 = null;
        }
        if (DATA_ID_ATTACK_TARGET_1.equals(pKey)) {
            this.clientSideDrainTarget1 = null;
        }
        if (DATA_ID_ATTACK_TARGET_2.equals(pKey)) {
            this.clientSideDrainTarget2 = null;
        }

    }

    public boolean isProtection(){
        List<SoulBomb> souls = SoulSageEntity.this.level.getEntitiesOfClass(SoulBomb.class,SoulSageEntity.this.getBoundingBox().inflate(3.0D),
                e->e.getOwnerID()==SoulSageEntity.this.getId() &&
                        e.isDefender());
        return !souls.isEmpty();
    }
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        int i = this.level.random.nextInt(1,5);
        this.spawSoulBomb(i);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!pSource.isProjectile() && this.isDrainSoul()){
            this.setDrainSoul(false);
            this.setActiveAttackTarget(0,3);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_ATTACK_TARGET_0, 0);
        this.entityData.define(DATA_ID_ATTACK_TARGET_1, 0);
        this.entityData.define(DATA_ID_ATTACK_TARGET_2, 0);
        this.entityData.define(DRAIN_SOUL,false);

    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setDrainSoul(compound.getBoolean("isDrainSoul"));
        this.absorbedSouls=compound.getInt("absorbedSouls");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isDrainSoul",this.isDrainSoul());
        compound.putInt("absorbedSouls",this.absorbedSouls);
    }

    public void setDrainSoul(boolean pBoolean){
        this.entityData.set(DRAIN_SOUL,pBoolean);
        this.drainDuration = pBoolean ? 100 : 0;
        if(pBoolean){
            // Inicia a drenar vida
            this.level.playSound(null,this, SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE,1.0F,1.0F);
        }else {
            this.stopDrainSound();
        }
    }
    protected void stopDrainSound(){
        if(!this.level.isClientSide){
            // Para el sonido del drenar vida
            PacketHandler.sendToAllTracking(new PacketStopSound(ModSounds.DRUM_SOUND.getId(),SoundSource.HOSTILE),this);
        }
    }

    public boolean isDrainSoul(){
        return this.entityData.get(DRAIN_SOUL);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==60){
            this.setDrainSoul(true);
        }else {
            super.handleEntityEvent(pId);
        }
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.ILLUSIONER_HURT;
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
    public void applyRaidBuffs(int pWave, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
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

    class DrainSpellGoal extends SpellcasterUseSpellGoal {

        DrainSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }


        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 250;
        }

        @Override
        protected int getCastWarmupTime() {
            return 20;
        }

        public void stop() {
            super.stop();
            SoulSageEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public boolean canUse() {
            return super.canUse() &&
                    !SoulSageEntity.this.isDrainSoul();
        }

        @Override
        public void tick() {
            super.tick();
            SoulSageEntity.this.getNavigation().stop();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_CAST_SPELL;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }

        protected void performSpellCasting() {
            SoulSageEntity owner = SoulSageEntity.this;
            List<SoulBomb> souls = owner.level.getEntitiesOfClass(SoulBomb.class,
                    owner.getBoundingBox().inflate(3.0D),
                    e->e.getOwnerID()==owner.getId() && !e.isDefender());
            boolean flag = false;
            int i = 0;
            if(!owner.isProtection()){
                for(SoulBomb soulBomb : souls){
                    if (!flag){
                        soulBomb.setInOrbit(false);
                        soulBomb.setDefender(true);
                        soulBomb.setOwner(owner);
                        flag=true;
                    }else {
                        soulBomb.setPositionSummon(i+1);
                        i++;
                    }
                }
                // sonido del escudo
                if(flag){
                    owner.level.playLocalSound(owner.getX(),owner.getY(),owner.getZ(),SoundEvents.AMBIENT_NETHER_WASTES_MOOD,SoundSource.HOSTILE,5.0f,-5.0f,false);
                }
            }
            owner.level.playLocalSound(owner.getX(),owner.getY(),owner.getZ(),SoundEvents.AMBIENT_NETHER_WASTES_MOOD,SoundSource.HOSTILE,5.0f,-5.0f,false);
            owner.setDrainSoul(true);
            owner.level.broadcastEntityEvent(owner,(byte) 60);
            owner.getNavigation().stop();
            if(owner.getTarget()!=null){

                owner.setActiveAttackTarget(owner.getTarget().getId(),0);

                List<LivingEntity> targets = owner.level.getEntitiesOfClass(LivingEntity.class,
                        owner.getBoundingBox().inflate(30.0D),
                        e->e.getMobType()!=MobType.ILLAGER && !(e instanceof Player player && player.isCreative()));

                int k = 1;

                for (LivingEntity living: targets){
                    owner.setActiveAttackTarget(living.getId(), k);
                    k++;
                    if(k == 3){
                        break;
                    }
                }
            }
        }
    }


    class ShieldSpellGoal extends SpellcasterUseSpellGoal {

        ShieldSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 200;
        }

        @Override
        protected int getCastWarmupTime() {
            return 20;
        }

        public void stop() {
            super.stop();
            SoulSageEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public void start() {
            super.start();
            SoulSageEntity.this.getNavigation().stop();
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()){
                return false;
            }
            return !SoulSageEntity.this.isDrainSoul();
        }

        @Override
        public void tick() {
            super.tick();
            SoulSageEntity.this.getNavigation().stop();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_CAST_SPELL;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }

        protected void performSpellCasting() {
            SoulSageEntity mage = SoulSageEntity.this;

            List<LivingEntity> targets = SoulSageEntity.this.level.getEntitiesOfClass(LivingEntity.class,
                    SoulSageEntity.this.getBoundingBox().inflate(30.0D),
                    e->e.getMobType() == MobType.ILLAGER);

            List<SoulBomb> souls = mage.level.getEntitiesOfClass(SoulBomb.class,
                    mage.getBoundingBox().inflate(3.0D),
                    e->e.getOwnerID() == mage.getId() && !e.isDefender());

            int i = 0;

            if(!souls.isEmpty()){
                for (LivingEntity target : targets){
                    List<SoulBomb> souls1 = target.level.getEntitiesOfClass(SoulBomb.class,
                            target.getBoundingBox().inflate(3.0D),
                            e->e.getOwnerID()==target.getId() && e.isDefender());
                    boolean flag = !souls1.isEmpty();
                    int k = 0;
                    for(SoulBomb soulBomb : souls){
                        if (!flag){
                            soulBomb.setInOrbit(false);
                            soulBomb.setDefender(true);
                            soulBomb.setOwner(target);
                            soulBomb.setPosition(target);
                            flag=true;
                        }else {
                            soulBomb.setPositionSummon(k+1);
                            k++;
                        }
                    }

                    target.level.playLocalSound(target.getX(),target.getY(),target.getZ(),
                            SoundEvents.AMBIENT_NETHER_WASTES_MOOD,
                            SoundSource.HOSTILE,5.0f,-5.0f,false);
                    i++;
                    if(i>2){
                        break;
                    }
                }
            }

        }
    }

    class ShootSoulSpellGoal extends SpellcasterUseSpellGoal {

        ShootSoulSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }


        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected int getCastWarmupTime() {
            return 20;
        }

        @Override
        public void start() {
            super.start();
            Entity target = SoulSageEntity.this.getTarget();
            SoulSageEntity.this.getNavigation().stop();
            if(target!=null){
                SoulSageEntity.this.getLookControl().setLookAt(target);
                SoulSageEntity.this.yBodyRot=SoulSageEntity.this.getYHeadRot();
                SoulSageEntity.this.setYBodyRot(SoulSageEntity.this.yBodyRot);
            }
        }

        public void stop() {
            super.stop();
            SoulSageEntity.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public boolean canUse() {
            if(SoulSageEntity.this.getTarget() !=null){
                LivingEntity entity = SoulSageEntity.this.getTarget();
                List<SoulBomb> souls = SoulSageEntity.this.level.getEntitiesOfClass(SoulBomb.class
                        ,SoulSageEntity.this.getBoundingBox().inflate(3.0D),
                        e->e.getOwnerID()==SoulSageEntity.this.getId() && !e.isDefender());
                float Dx = distanceTo(entity) ;
                    if (!super.canUse()){
                        return false;
                    }
                    return !souls.isEmpty() ;
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
            LivingEntity target = SoulSageEntity.this.getTarget();
            SoulSageEntity.this.getNavigation().stop();
            if(target!=null){
                SoulSageEntity.this.getLookControl().setLookAt(target);
                SoulSageEntity.this.yBodyRot=SoulSageEntity.this.getYHeadRot();
                SoulSageEntity.this.setYBodyRot(SoulSageEntity.this.yBodyRot);
            }
        }

        protected void performSpellCasting() {
            LivingEntity owner = SoulSageEntity.this;
            Entity target = SoulSageEntity.this.getTarget();
            List<SoulBomb> souls = owner.level.getEntitiesOfClass(SoulBomb.class,owner.getBoundingBox().inflate(3.0D),e->e.getOwnerID()==owner.getId() && !e.isDefender());
            int i = 0;
            for(SoulBomb soulBomb : souls){
                if(target!=null){
                    BlockPos posTarget1 = target.getOnPos();
                    BlockPos posOwner = owner.getOnPos();
                    soulBomb.setInOrbit(false);
                    soulBomb.setYRot(owner.getYRot());
                    soulBomb.shoot(posTarget1.getX()- posOwner.getX(), posTarget1.getY() -2F - posOwner.getY(), posTarget1.getZ()- posOwner.getZ(),2.0F,0.0F);
                    //sonido del sould bomb al ser lanzado
                    soulBomb.level.playSound(null,soulBomb,SoundEvents.CHICKEN_DEATH,SoundSource.HOSTILE,1.0F,1.0F);

                }
                break;
            }
            owner.level.playLocalSound(owner.getX(),owner.getY(),owner.getZ(),SoundEvents.AMBIENT_NETHER_WASTES_MOOD,SoundSource.HOSTILE,5.0f,-5.0f,false);
        }
    }
}
