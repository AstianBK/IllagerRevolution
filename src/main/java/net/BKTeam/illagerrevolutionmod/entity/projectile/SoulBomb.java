package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.AreaFireColumnEntity;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SoulBomb extends ThrowableProjectile {

    public static final EntityDataAccessor<Integer> OWNER_UUID = SynchedEntityData.defineId(SoulBomb.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IN_ORBIT = SynchedEntityData.defineId(SoulBomb.class, EntityDataSerializers.BOOLEAN);
    protected int life;
    private static final EntityDataAccessor<Integer> POSITION_SUMMON = SynchedEntityData.defineId(SoulBomb.class, EntityDataSerializers.INT);

    public SoulBomb(EntityType<? extends ThrowableProjectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
        this.setInOrbit(true);
        this.life = 200;
    }

    public SoulBomb(LivingEntity thrower, Level level,int summonPosition) {
        super(ModEntityTypes.SOUL_BOMB.get(), thrower, level);
        this.setNoGravity(true);
        this.setInOrbit(true);
        this.life = 200;
        this.setPositionSummon(summonPosition);
    }

    @Override
    public void tick() {
        Entity owner = level.getEntity(this.getOwnerID());
        if (!this.level.isClientSide && owner == null ) {
            this.remove(RemovalReason.DISCARDED);
            return;
        } else if (owner == null) {
            return;
        }
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();

        if (this.inOrbit() && owner instanceof LivingEntity){
            this.setPosition((LivingEntity) owner);
        }
        if(this.life>0){
            this.life--;
        }else {
            this.discard();
        }
        if(this.level.isClientSide){
            this.playParticles();
        }
        super.tick();
    }

    public void playParticles() {
        for (int i = 0; i < 1; i++) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
            for (double j = 0; j < dist; j++) {
                double coeff = j / dist;
                this.level.addParticle(ModParticles.SOUL_FLAME.get(),
                        (float) (this.xo + deltaX * coeff),
                        (float) (this.yo + deltaY * coeff) + 0.1, (float)
                                (this.zo + deltaZ * coeff),
                        0.0125f * (this.random.nextFloat() - 0.5f),
                        0.0125f * (this.random.nextFloat() - 0.5f),
                        0.0125f * (this.random.nextFloat() - 0.5f));
            }
        }
        this.level.addParticle(ModParticles.SOUL_FLAME.get(),this.getX(),this.getY(),this.getZ()
                ,0.0F,0.0F,0.0F);
    }

    private int getOwnerID() {
        return this.entityData.get(OWNER_UUID);
    }

    private void setOwnerId(int pId){
        this.entityData.set(OWNER_UUID,pId);
    }

    public boolean inOrbit() {
        return this.entityData.get(IN_ORBIT);
    }

    public void setInOrbit(boolean pBoolean){
        this.entityData.set(IN_ORBIT,pBoolean);
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

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.inOrbit()) {
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
    }

    @Override
    public void shootFromRotation(Entity pProjectile, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        this.setInOrbit(false);
        this.life=50;
        super.shootFromRotation(pProjectile, pX, pY, pZ, pVelocity, pInaccuracy);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (pResult.getEntity() instanceof LivingEntity living) {
            AreaFireColumnEntity areaFireColumn = new AreaFireColumnEntity(ModEntityTypes.AREA_FIRE_COLUMN.get(), this.level);
            areaFireColumn.setPos(living.getOnPos().getX(), living.getOnPos().getY() + 1, living.getOnPos().getZ());
            areaFireColumn.setOwner((LivingEntity) this.getOwner());
            areaFireColumn.setDuration(200,50);
            this.level.addFreshEntity(areaFireColumn);
            if (this.getOwner() instanceof LivingEntity) {
                living.hurt(DamageSource.mobAttack((LivingEntity) this.getOwner()).setMagic(), 3);
            } else {
                living.hurt(DamageSource.MAGIC, 3);
            }

            if(!this.level.isClientSide){
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,2));
            }
        }
    }

    @Override
    public void setOwner(@Nullable Entity pEntity) {
        super.setOwner(pEntity);
        this.setOwnerId(pEntity != null ? pEntity.getId() : 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        BlockPos blockPos = p_37258_.getBlockPos();
        AreaFireColumnEntity areaFireColumn = new AreaFireColumnEntity(ModEntityTypes.AREA_FIRE_COLUMN.get(), this.level);
        areaFireColumn.setPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
        areaFireColumn.setOwner((LivingEntity) this.getOwner());
        areaFireColumn.setDuration(200,50);
        this.level.addFreshEntity(areaFireColumn);
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
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setPositionSummon(pCompound.getInt("Summon"));
        this.setOwnerId(pCompound.getInt("ownerId"));
        this.setInOrbit(pCompound.getBoolean("inOrbit"));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(POSITION_SUMMON,1);
        this.entityData.define(OWNER_UUID,0);
        this.entityData.define(IN_ORBIT,false);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}