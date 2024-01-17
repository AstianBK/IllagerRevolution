package net.BKTeam.illagerrevolutionmod.entity.custom;

import com.google.common.collect.ImmutableList;
import net.BKTeam.illagerrevolutionmod.entity.goals.ChargedGoal;
import net.BKTeam.illagerrevolutionmod.entity.goals.KnightEntity;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class BulkwarkEntity extends KnightEntity implements GeoEntity {

    private static final EntityDataAccessor<Byte> ID_MODE_STATUS =
            SynchedEntityData.defineId(BulkwarkEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> SHIELD_HEALTH =
            SynchedEntityData.defineId(BulkwarkEntity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public boolean slamMoment;
    public int slamTimer;
    public float damageAbsorb;
    public int chargedCooldown;
    public int chargedTimer;
    public int guardianTimer;
    public int prepareTimer;
    public int stunnedTimer;
    public int cooldownGuardian;
    public Vec3 vec3Charged;
    public BulkwarkEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_) {
        super(p_32105_, p_32106_);
        this.chargedCooldown=0;
        this.chargedTimer=0;
        this.prepareTimer=0;
        this.stunnedTimer=0;
        this.slamTimer=0;
        this.damageAbsorb=0;
        this.vec3Charged = Vec3.ZERO;
        this.setShieldHealth(100.0F);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.80D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 40.D)
                .add(Attributes.MOVEMENT_SPEED, 0.31f).build();
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2,new PushAttack(this,1.4D,true));
        this.targetSelector.addGoal(1,new ChargedGoal(this,1.5D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1){
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof BulkwarkEntity bulkwark && !bulkwark.isCharged() && !bulkwark.isAbsorbMode();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this){
            @Override
            public boolean canUse() {
                return super.canUse() && !BulkwarkEntity.this.isAbsorbMode() && !BulkwarkEntity.this.isCharged();
            }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.goalSelector.addGoal(7, new BreakDoorGoal(this, e -> true));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setShieldHealth(100.0F);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private   <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        String s1 = !this.isGuarding() ? "down" : "";
        if (event.isMoving() && !this.isCharged() && !this.isStunned()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.bulkwark.walk"+s1, Animation.LoopType.LOOP));
        }else if(event.isMoving() && this.isCharged() && !this.isStunned()){
            event.getController().setAnimation(RawAnimation.begin().then("animation.bulkwark.charge",Animation.LoopType.LOOP));
        }else if(this.isAbsorbMode()){
            event.getController().setAnimation(RawAnimation.begin().then("animation.bulkwark.guard",Animation.LoopType.LOOP));
        }else if(this.slamMoment){
            event.getController().setAnimation(RawAnimation.begin().then("animation.bulkwark.slam",Animation.LoopType.PLAY_ONCE));
        }else if(this.isStunned()){
            event.getController().setAnimation(RawAnimation.begin().then("animation.bulkwark.stun",Animation.LoopType.LOOP));
        }
        else event.getController().setAnimation(RawAnimation.begin().then("animation.bulkwark.idle"+s1,Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private boolean isStunned(){
        return this.stunnedTimer>0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isStunned() || this.isAbsorbMode() || this.slamMoment;
    }

    public ShieldHealth getShieldHealthStat(){
        System.out.print(this.getShieldHealth()/this.getMaxShieldHealth());
        return ShieldHealth.byFraction(this.getShieldHealth()/this.getMaxShieldHealth());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.getShieldHealth()/this.getMaxShieldHealth()<1.0F){
            if(this.tickCount%200==0){
                float f = 10.0F;
                float f1 =this.getMaxShieldHealth()-this.getShieldHealth();
                this.setShieldHealth(this.getShieldHealth()+Math.min(f, f1));
            }
        }
        if(this.isStunned()){
            this.getLookControl().setLookAt(this.getViewVector(1.0F));
            this.setYBodyRot(this.getYHeadRot());
            this.yBodyRot=this.getYHeadRot();
            this.stunnedTimer--;
            this.stunEffect();
            this.getNavigation().stop();
        }
        if(this.chargedCooldown>0){
            this.chargedCooldown--;
        }

        if(this.cooldownGuardian>0){
            this.cooldownGuardian--;
        }

        if(this.isCharged()){
            if (this.isAlive() && !this.level().isClientSide) {
                for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                    if (!this.isAlliedTo(entity) && entity != this) {
                        entity.push(0.0D, 1.0D, 0.0D);
                    }
                }
            }
            if(this.prepareTimer>0){
                this.getNavigation().stop();
                if(this.getTarget()!=null){
                    this.lookAt(this.getTarget(),30.0F,30.0F);
                    this.setYBodyRot(this.getYHeadRot());
                    this.yBodyRot=this.getYHeadRot();
                }
                this.prepareTimer--;
                if(this.prepareTimer==0){
                    this.chargedTimer=30;
                }
            }else if(this.chargedTimer>0){
                this.chargedTimer--;
                this.getLookControl().setLookAt(this.getViewVector(1.0F));
                this.setYBodyRot(this.getYHeadRot());
                this.yBodyRot=this.getYHeadRot();
                Random random1 = new Random();
                float f = this.yBodyRot * ((float) Math.PI / 180F);
                float f1 = Mth.cos(f);
                float f2 = Mth.sin(f);
                double dx0 = this.getX() - (this.getX() - f2 + f1 * 0.4d);
                double dz0 = this.getZ() - (this.getZ() + f1 + f2 * 0.4d);
                double dx1 = this.getX() - (this.getX() - f2  - f1 * 0.4d);
                double dz1 = this.getZ() - (this.getZ() + f1  - f2 * 0.4d);
                double d0 = Math.max(dx0*dx0 + dz0*dz0,0.001D);
                double d1 = Math.max(dx1*dx1 + dz1*dz1,0.001D);
                for(int i = 0; i<10;i++){
                    double r = random1.nextFloat(0.01F,0.2F);
                    this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - f2 + f1 * 0.4d, this.getY(), this.getZ() + f1 + f2 * 0.4d, (dx0/d0)*r, random1.nextFloat(0.05F,0.1F),(dz0/d0)*r);
                    this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - f2  - f1 * 0.4d, this.getY(), this.getZ() + f1  - f2 * 0.4d, (dx1/d1)*r   , random1.nextFloat(0.05F,0.1F), (dz1/d1)*r);
                }
                if(this.chargedTimer<=0){
                    this.setChargedMode(false,false);
                }
            }
        }
        if(this.isGuarding()){
            this.guardianTimer--;
            if(this.guardianTimer<=0){
                this.setGuardingMode(false);
                if(this.isAbsorbMode()){
                    this.setAbsorbMode(false);
                    this.slamMoment=true;
                    this.slamTimer=20;
                    this.level().broadcastEntityEvent(this,(byte) 60);
                }
            }
            if(this.isAbsorbMode()){
                this.applyRadius(7.0F,0.01f);
                this.getNavigation().stop();
                this.getLookControl().setLookAt(this.getViewVector(1.0F));
                this.yBodyRot=this.getYHeadRot();
                this.setYBodyRot(this.getYHeadRot());
                BlockPos pos = new BlockPos((int) this.getX(), (int) (this.getY()+1.5d), (int) this.getZ());
                List<Entity> targets = this.level().getEntitiesOfClass(Entity.class,new AABB(pos).inflate(10,10,10), e -> e != this && this.distanceTo(e) <= 7 + e.getBbWidth() / 2f && e.getY() <= this.getY() + 7);
                for(Entity living : targets){
                    float entityHitAngle = (float) ((Math.atan2(living.getZ() - this.getZ(), living.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
                    float entityAttackingAngle = this.yBodyRot % 360;
                    float arc = 120.0F;
                    if (entityHitAngle < 0) {
                        entityHitAngle += 360;
                    }
                    if (entityAttackingAngle < 0) {
                        entityAttackingAngle += 360;
                    }
                    float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                    float entityHitDistance = (float) Math.sqrt((living.getZ() - this.getZ()) * (living.getZ() - this.getZ()) + (living.getX() - this.getX()) * (living.getX() - this.getX())) - living.getBbWidth() / 2f;
                    if(living instanceof  LivingEntity){
                        if (entityHitDistance <= 7 - 0.3 && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2) ) {
                            if (!this.isAlliedTo(living)) {
                                BlockPos posTarget = living.getOnPos();
                                BlockPos posOwner = this.getOnPos();
                                Vec3 vec3 = living.getDeltaMovement().add(posOwner.getX()-posTarget.getX(),posOwner.getY()-posTarget.getY(),posOwner.getZ()-posTarget.getZ()).normalize().scale(0.05D);
                                living.setDeltaMovement(vec3);
                                if(living instanceof Player){
                                    ((Player) living).travel(vec3);
                                }
                            }

                        }
                    }else if(living instanceof Projectile projectile && projectile.getOwner()!=this){
                        boolean isAllied = projectile.getOwner() != null && this.isAlliedTo(projectile.getOwner());
                        if(!isAllied){
                            this.level().playSound(null,projectile, ModSounds.SOUL_ABSORB.get(),SoundSource.HOSTILE,2.0F,-3.0F);
                            projectile.discard();
                        }
                    }
                }
            }
        }
        if(this.slamMoment){
            this.slamTimer--;
            if(this.slamTimer==10){
                double power = (this.damageAbsorb)*0.5F;
                for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D))) {
                    if (!this.isAlliedTo(entity) && entity != this) {
                        entity.push(0.0D, 0.5D+power, 0.0D);
                    }
                }
                this.damageAbsorb=0.0F;
            }
            if(this.slamTimer==0){
                this.slamMoment=false;
                this.level().broadcastEntityEvent(this,(byte) 61);
            }

        }
    }

    public void applyRadius(float radius, float speedY){
        int i;
        i = Mth.ceil(((float)Math.PI/2.0F) * radius * radius);
        float radius1 = ((float)Math.PI/2.0F)/(float) i;
        Random random1 = new Random();
        for(int j=0;j<=i;j++){
            float f = this.yBodyRot * ((float) Math.PI / 180F);
            float f1 = Mth.sin(((float)Math.PI/4.0F)+f + j * radius1)*radius;
            float f2 = Mth.cos(((float)Math.PI/4.0F)+f + j * radius1)*radius;
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK,this.getBlockStateOn()),this.getX()+f2 ,this.getY(),this.getZ()+f1,
                    0.01F,
                    random1.nextFloat(0.0F,speedY),
                    0.01F);
            if(j==0 || j==i){
                double deltaY = this.getY() - this.yOld;
                double dist = Math.ceil(Math.sqrt(f2 * f2 + deltaY * deltaY + f1 * f1) * 6);
                for (double k = 0; k < dist; k++) {
                    double coeff = k / dist;
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK,this.getBlockStateOn()),
                            (float) (this.xo + f2 * coeff),
                            (float) (this.yo + deltaY * coeff) + 0.1, (float)
                                    (this.zo + f1 * coeff),
                            0.0125f * (this.random.nextFloat() - 0.5f),
                            0.0125f * (this.random.nextFloat() - 0.5f),
                            0.0125f * (this.random.nextFloat() - 0.5f));
                }
            }
        }
    }

    public void setShieldHealth(float pHealth){
        this.entityData.set(SHIELD_HEALTH,pHealth);
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==39){
            this.stunnedTimer=60;
        }else if(pId==60){
            this.slamMoment = true;
            this.slamTimer=20;
        }else if(pId==61){
            this.slamMoment = false;
            this.slamTimer=0;
        }else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.getMaxHealth()*0.30<=this.getHealth() && !this.isGuarding() && this.guardianTimer<=0){
            this.setGuardingMode(true);
        }
        if(this.isAbsorbMode()){
            float sH = this.getShieldHealth();
            if(pAmount<=sH){
                this.setShieldHealth(sH-pAmount);
                return false;
            }else {
                pAmount=pAmount-this.getShieldHealth();
                this.setShieldHealth(0.0F);
                this.setAbsorbMode(false);
                this.setGuardingMode(false);
            }
            this.damageAbsorb+=pAmount;
            this.level().playSound(null,this,SoundEvents.ANVIL_HIT, SoundSource.HOSTILE,2F,1.0F);
        }
        return super.hurt(pSource, pAmount);
    }

    public void setGuardingMode(boolean pBoolean){
        byte b0 = this.entityData.get(ID_MODE_STATUS);
        this.entityData.set(ID_MODE_STATUS,pBoolean ? (byte)(b0 | 1) : (byte)(b0 & -2));
        this.guardianTimer = pBoolean ? 100 : 0;
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        if(this.isCharged()){
            if(this.horizontalCollision){
                this.getNavigation().stop();
                this.setChargedMode(false,true);
                this.setAbsorbMode(false);
                this.setGuardingMode(false);
                this.stunnedTimer=60;
                this.level().broadcastEntityEvent(this,(byte) 39);
            }
        }
    }

    public boolean isGuarding(){
        return (this.entityData.get(ID_MODE_STATUS) & 1)!=0;
    }

    public void setChargedMode(boolean pBoolean,boolean pStun){
        byte b0 = this.entityData.get(ID_MODE_STATUS);
        this.entityData.set(ID_MODE_STATUS,pBoolean ? (byte)(b0 | 2) : (byte)(b0 & -3));
        this.prepareTimer=pBoolean ? 20 : 0;
        this.chargedCooldown = pBoolean ? 0 : 600;
        if(pStun){
            this.level().playSound(null,this,SoundEvents.ANVIL_FALL, SoundSource.HOSTILE,2F,1.0F);
        }
    }

    public boolean isCharged(){
        return (this.entityData.get(ID_MODE_STATUS) & 2)!=0;
    }

    public void setAbsorbMode(boolean pBoolean){
        byte b0 = this.entityData.get(ID_MODE_STATUS);
        this.entityData.set(ID_MODE_STATUS,pBoolean ? (byte)(b0 | 4) : (byte)(b0 & -5));
    }

    public boolean isAbsorbMode(){
        return (this.entityData.get(ID_MODE_STATUS) & 4)!=0;
    }

    public float getShieldHealth(){
        return this.entityData.get(SHIELD_HEALTH);
    }
    public float getMaxShieldHealth(){
        return 100.0F;
    }

    private void strongKnockbackCharged(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        double d3 = 2.5D;
        p_33340_.push(d0 / d2 * d3, 0.2D, d1 / d2 * d3);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("isAbsorb",this.isAbsorbMode());
        pCompound.putBoolean("isGuarding",this.isGuarding());
        pCompound.putFloat("ShieldHealth",this.getShieldHealth());
        if(this.vec3Charged != Vec3.ZERO){
            pCompound.putDouble("posChargedX",this.vec3Charged.x);
            pCompound.putDouble("posChargedY",this.vec3Charged.y);
            pCompound.putDouble("posChargedZ",this.vec3Charged.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAbsorbMode(pCompound.getBoolean("isAbsorb"));
        this.setGuardingMode(pCompound.getBoolean("isGuarding"));
        this.setShieldHealth(pCompound.getFloat("ShieldHealth"));
        if(pCompound.contains("posChargedX")){
            this.vec3Charged =new Vec3(pCompound.getDouble("posChargedX")
                    ,pCompound.getDouble("posChargedY")
                    ,pCompound.getDouble("posChargedZ"));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_MODE_STATUS,(byte)0);
        this.entityData.define(SHIELD_HEALTH,100.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this,"predicate",
                0,this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class PushAttack extends MeleeAttackGoal {
        private final BulkwarkEntity goalOwner;

        public PushAttack(BulkwarkEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.goalOwner.isCharged() && !this.goalOwner.isAbsorbMode();
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && !this.goalOwner.isAbsorbMode()) {
                this.goalOwner.playSound(ModSounds.SOUL_ABSORB.get(), 1.0F, 1.0F);
                this.goalOwner.getNavigation().stop();
                this.goalOwner.setAbsorbMode(true);
            }
        }
    }

    public static enum ShieldHealth {
        SOUL_6(0.9F),
        SOUL_5(0.75F),
        SOUL_4(0.6F),
        SOUL_3(0.45F),
        SOUL_2(0.20F),
        SOUL_1(0.0F),
        NONE(0.0F);

        private static final List<ShieldHealth> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble((p_28904_) -> {
            return (double)p_28904_.fraction;
        })).collect(ImmutableList.toImmutableList());
        private final float fraction;

        ShieldHealth(float p_28900_) {
            this.fraction = p_28900_;
        }

        public static ShieldHealth byFraction(float p_28902_) {
            ShieldHealth shieldHealth=NONE;
            for(ShieldHealth irongolem$crackiness : BY_DAMAGE) {
                if (p_28902_ > irongolem$crackiness.fraction && NONE!=irongolem$crackiness) {
                    shieldHealth=irongolem$crackiness;
                }
            }

            return shieldHealth;
        }
    }
}
