package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.AreaFireColumnEntity;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProjectileMagic extends ThrowableProjectile {

    public static final EntityDataAccessor<Integer> OWNER_UUID =
            SynchedEntityData.defineId(ProjectileMagic.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IN_ORBIT =
            SynchedEntityData.defineId(ProjectileMagic.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DEFENDER =
            SynchedEntityData.defineId(ProjectileMagic.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> POSITION_SUMMON =
            SynchedEntityData.defineId(ProjectileMagic.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> POWER_LEVEL =
            SynchedEntityData.defineId(ProjectileMagic.class, EntityDataSerializers.INT);

    public int discardTimer;

    public boolean discardMoment;

    public ProjectileMagic(EntityType<? extends ThrowableProjectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
        this.setInOrbit(true);
    }

    public ProjectileMagic(EntityType<? extends ProjectileMagic> entityType,LivingEntity thrower, Level level, int summonPosition) {
        super(entityType, thrower , level);
        this.setNoGravity(true);
        this.setInOrbit(true);
        this.setOwner(thrower);
        this.setPositionSummon(summonPosition);
        this.discardMoment=false;
    }

    @Override
    public void tick() {
        super.tick();

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();

        if(this.level.isClientSide && !this.isDefender()){
            this.playParticles();
        }
    }

    public void playParticles() {
        for (int i = 0; i < 1; i++) {
            double deltaX = this.getX() - this.xOld;
            double deltaY = this.getY() - this.yOld;
            double deltaZ = this.getZ() - this.zOld;
            double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
            for (double j = 0; j < dist; j++) {
                double coeff = j / dist;
                this.level.addParticle(getParticle(),
                        (float) (this.xo + deltaX * coeff),
                        (float) (this.yo + deltaY * coeff) + 0.1, (float)
                                (this.zo + deltaZ * coeff),
                        0.0125f * (this.random.nextFloat() - 0.5f),
                        0.0125f * (this.random.nextFloat() - 0.5f),
                        0.0125f * (this.random.nextFloat() - 0.5f));
            }
        }
        this.level.addParticle(this.getParticle(),this.getX(),this.getY(),this.getZ()
                ,0.0F,0.0F,0.0F);
    }

    protected SimpleParticleType getParticle(){
        return ModParticles.SOUL_FLAME.get();
    }

    public int getOwnerID() {
        return this.entityData.get(OWNER_UUID);
    }

    public void setOwnerId(int pId){
        this.entityData.set(OWNER_UUID,pId);
    }

    public boolean inOrbit() {
        return this.entityData.get(IN_ORBIT);
    }

    public void setInOrbit(boolean pBoolean){
        this.entityData.set(IN_ORBIT,pBoolean);
    }

    public boolean isDefender() {
        return this.entityData.get(DEFENDER);
    }

    public void setDefender(boolean pBoolean){
        this.entityData.set(DEFENDER,pBoolean);
    }

    public int getPowerLevel() {
        return this.entityData.get(POWER_LEVEL);
    }

    public void setPowerLevel(int pPower){
        this.entityData.set(POWER_LEVEL,pPower);
    }

    public void setPosition(LivingEntity owner) {
        float f = owner.getYRot() * (float) Math.PI/180F;
        float f1 = Mth.cos(f);
        float f2 = Mth.sin(f);
        double targetX;
        double targetY;
        double targetZ;
        int i1=Mth.ceil(this.getPositionSummon()/2.0F);
        if(this.getPositionSummon()%2==0){
            targetX = owner.getX() + f1 * (0.4D*i1);
            targetY = owner.getY() + owner.getBbHeight() + 0.3D+0.5D*i1;
            targetZ = owner.getZ() + f2 * (0.4D*i1);
        }else {
            targetX = owner.getX() - f1 * (0.4D*i1);
            targetY = owner.getY() + owner.getBbHeight() + 0.3D+0.5D*i1;
            targetZ = owner.getZ() - f2 * (0.4D*i1);
        }
        this.setPos(targetX, targetY, targetZ);
    }

    public void setDefenderPosition(LivingEntity owner){
        float f1 = (float) Math.cos(this.tickCount/10.0F) * (1.5F);
        float f2 = (float) Math.sin(this.tickCount/10.0F) * (1.5F);
        double targetX = owner.getX() - f2;
        double targetY = owner.getY() + owner.getBbHeight()/2;
        double targetZ = owner.getZ() - f1;
        this.setPos(targetX, targetY, targetZ);
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.inOrbit()) {
            this.discard();
        }
    }

    @Override
    public void shootFromRotation(Entity pProjectile, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        super.shootFromRotation(pProjectile, pX, pY, pZ, pVelocity, pInaccuracy);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
    }

    @Override
    public void setOwner(@Nullable Entity pEntity) {
        super.setOwner(pEntity);
        this.setOwnerId(pEntity != null ? pEntity.getId() : 0);
    }


    public void setPositionSummon(int pPositionSummon) {
        this.entityData.set(POSITION_SUMMON, pPositionSummon);
    }

    public int getPositionSummon(){
        return this.entityData.get(POSITION_SUMMON);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Summon",this.getPositionSummon());
        pCompound.putInt("ownerId",this.getOwnerID());
        pCompound.putBoolean("inOrbit",this.inOrbit());
        pCompound.putInt("powerLevel",this.getPowerLevel());
        pCompound.putBoolean("defender",this.isDefender());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setPositionSummon(pCompound.getInt("Summon"));
        this.setOwnerId(pCompound.getInt("ownerId"));
        this.setInOrbit(pCompound.getBoolean("inOrbit"));
        this.setPowerLevel(pCompound.getInt("powerLevel"));
        this.setDefender(pCompound.getBoolean("defender"));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(POSITION_SUMMON,1);
        this.entityData.define(OWNER_UUID,0);
        this.entityData.define(IN_ORBIT,false);
        this.entityData.define(POWER_LEVEL,0);
        this.entityData.define(DEFENDER,false);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}