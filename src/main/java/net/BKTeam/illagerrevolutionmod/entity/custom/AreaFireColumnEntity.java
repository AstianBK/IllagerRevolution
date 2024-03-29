package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AreaFireColumnEntity extends Entity {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(
            AreaFireColumnEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> IS_BURN = SynchedEntityData.defineId(
            AreaFireColumnEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(
            AreaFireColumnEntity.class,EntityDataSerializers.INT);
    private int duration;

    private int maxDuration;
    private int prepareDuration;
    private int prepareTimer;
    private LivingEntity owner;
    private UUID idOwner;
    private boolean applySlowness;
    public AreaFireColumnEntity(EntityType<?> pEntityType, Level pLevel,boolean pIsBurn) {
        super(pEntityType, pLevel);
        this.setIsBurn(pIsBurn);
        this.noPhysics = true;
        this.setRadius(5.0F);
        this.setDuration(100,25);
        this.applySlowness=false;
    }

    public AreaFireColumnEntity(EntityType<AreaFireColumnEntity> areaFireColumnEntityEntityType, Level level) {
        super(areaFireColumnEntityEntityType,level);
        this.noPhysics = true;
        this.setRadius(5.0F);
        this.setDuration(100,25);
        this.applySlowness=false;
    }

    public void setDuration(int duration,int prepareDuration) {
        int i = Mth.clamp(duration,100,1000);
        int j = Mth.clamp(prepareDuration,0,100);
        this.duration = i;
        this.maxDuration = i;
        this.prepareDuration = j;
        this.prepareTimer = j;
    }

    public void setApplySlowness(boolean applySlowness){
        this.applySlowness=applySlowness;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS,0.0F);
        this.entityData.define(IS_BURN,false);
        this.entityData.define(LEVEL,0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setRadius(pCompound.getFloat("radius"));
        this.setIsBurn(pCompound.getBoolean("isBurn"));
        this.setPowerLevel(pCompound.getInt("powerLevel"));
        this.setApplySlowness(pCompound.getBoolean("applySlowness"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("radius",this.getRadius());
        pCompound.putBoolean("isBurn",this.isBurn());
        pCompound.putInt("powerLevel",this.getPowerLevel());
        pCompound.putBoolean("applySlowness",this.applySlowness);
    }

    protected void setIsBurn(boolean isBurn){
        this.entityData.set(IS_BURN,isBurn);
        // sonido del inicio del burn
        if(isBurn){
            this.level.playSound(null,this, ModSounds.SOUL_SAGE_FIRE.get(),
                    SoundSource.HOSTILE,10.0F,1.0F);
        }
    }

    protected boolean isBurn(){
        return this.entityData.get(IS_BURN);
    }

    public void setPowerLevel(int pLevel){
        this.entityData.set(LEVEL,pLevel);
    }

    public int getPowerLevel(){
        return this.entityData.get(LEVEL);
    }

    public void setOwner(LivingEntity owner){
        this.owner=owner;
        this.idOwner=owner!=null ? owner.getUUID() : null;
    }

    public LivingEntity getOwner(){
        if (this.owner == null && this.idOwner != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.idOwner);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }
        return this.owner;
    }

    protected void setRadius(float radius){
        this.entityData.set(RADIUS,radius);
    }

    protected float getRadius(){
        return this.entityData.get(RADIUS);
    }

    @Override
    public void tick() {
        super.tick();
        int i = this.getPowerLevel();
        if(this.prepareTimer==this.prepareDuration || this.prepareTimer==Mth.ceil((float)this.prepareDuration- (float) this.prepareDuration /4.0F) ||
                this.prepareTimer==Mth.ceil((float)this.prepareDuration - (float) this.prepareDuration /2.0F) ||
                this.prepareTimer==Mth.ceil((float)this.prepareDuration- (float)this.prepareDuration/1.25F)){
            this.level.playSound(null,this, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE,5.0F,1.0F);
        }
        if(this.isBurn()){
            for(int j = 0;j<i+1;j++){
                this.applyRadius(this.getRadiusForLevel(j),0.5f);
            }
        }else {
            if(this.prepareTimer<=Mth.ceil((float)this.prepareDuration- (float) this.prepareDuration /4.0F)){
                this.applyRadius(this.getRadius()/1.25F,0.05f);
                if(this.prepareTimer<=Mth.ceil((float)this.prepareDuration - (float) this.prepareDuration /2.0F)){
                    this.applyRadius(this.getRadius()/2.0F,0.05f);
                    if(this.prepareTimer<=Mth.ceil((float)this.prepareDuration-(float) this.prepareDuration/1.25F)){
                        this.applyRadius(this.getRadius()/4F,0.05f);
                    }
                }
            }
            this.applyRadius(this.getRadius(),0.01F);
        }
        if(this.prepareTimer==0){
            this.setIsBurn(true);
            this.prepareTimer--;
        }else {
            this.prepareTimer--;
        }
        if(this.isBurn()){
            if(this.tickCount%10==0){
                List<LivingEntity> targets;
                if(this.getOwner()!=null){
                    targets = this.level.getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(this.getRadiusForLevel(i),3.0d,this.getRadiusForLevel(i)),e->!this.getOwner().isAlliedTo(e) && this.getOwner()!=e);
                    for (LivingEntity living : targets){
                        living.hurt(DamageSource.indirectMagic(this,this.getOwner()),2.0F+1.0F*this.getPowerLevel());
                        living.setSecondsOnFire(3);
                        if(this.applySlowness){
                            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,2));
                        }
                    }
                }
            }
            if(this.duration<0){
                this.discard();
            }else {
                this.duration--;
            }
        }else {
            if(this.tickCount%5==0){
                if(this.getOwner()!=null){
                    List<LivingEntity> target = this.level.getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(5.0D),e->!this.getOwner().isAlliedTo(e) && e!=this.getOwner());
                    for(LivingEntity collateral : target){
                        int level = 0;
                        if(collateral.hasEffect(InitEffect.SOUL_BURN.get())){
                            int levelEffect=Mth.clamp(collateral.getEffect(InitEffect.SOUL_BURN.get()).getAmplifier(),0,this.getPowerLevel()-1);
                            level=levelEffect+1;
                        }
                        collateral.addEffect(new MobEffectInstance(InitEffect.SOUL_BURN.get(),200,level));
                    }
                }
            }
        }
    }

    public float getRadiusForLevel(int level){
        if(level == 1){
            return this.getRadius()/2F;
        }else if (level == 2) {
            return this.getRadius()/1.25F;
        }else if (level >= 3) {
            return this.getRadius();
        }
        return this.getRadius()/4F;
    }

    public void applyRadius(float radius,float speedY){
        int i;
        i = Mth.ceil((float)Math.PI * radius * radius);
        float radius1 = (float)Math.PI /(float) i;
        Random random1 = new Random();
        for(int j=0;j<=i*2;j++){
            float f1 = Mth.sin(j * radius1)*radius;
            float f2 = Mth.cos(j * radius1)*radius;
            this.level.addParticle(ModParticles.SOUL_FLAME.get(),this.getX()+f2 ,this.getY(),this.getZ()+f1,
                    0.01F,
                    random1.nextFloat(0.0F,speedY),
                    0.01F);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}