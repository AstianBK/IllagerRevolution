package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.AreaFireColumnEntity;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class SoulBomb extends ProjectileMagic {

    public SoulBomb(EntityType<? extends ThrowableProjectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
        this.setInOrbit(true);
    }

    public SoulBomb(LivingEntity thrower, Level level,int summonPosition) {
        super(ModEntityTypes.SOUL_BOMB.get(),thrower, level,summonPosition);

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

        if(owner instanceof LivingEntity){
            if (this.inOrbit() && !this.isDefender()){
                this.setPosition((LivingEntity) owner);
            }else if(this.isDefender()){
                this.setDefenderPosition((LivingEntity) owner);
            }
        }

        if(this.discardMoment){
            this.discardTimer--;
            if(this.discardTimer>8){
                List<LivingEntity> livings = owner.level.getEntitiesOfClass(LivingEntity.class,owner.getBoundingBox().inflate(2.0D+(0.5D*this.getPowerLevel())),e->e!=owner && e!=owner.getVehicle());
                for(LivingEntity living : livings){
                    double d0 = living.getX() - owner.getX();
                    double d1 = living.getZ() - owner.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                    double d3 = 1.5D + (0.5D * this.getPowerLevel());
                    living.push(d0 / d2 * d3, 0.2D, d1 / d2 * d3);
                    if(owner instanceof LivingEntity){
                        living.hurt(DamageSource.mobAttack((LivingEntity) owner),5.0F+1.0F*this.getPowerLevel());
                    }else {
                        living.hurt(DamageSource.GENERIC,5.0F+1.0F*this.getPowerLevel());
                    }
                    if(!this.level.isClientSide){
                        living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,100,0));
                    }
                }
                if(owner instanceof LivingEntity living){
                    //sonido de desactivar escudo
                    living.level.playSound(null,living,ModSounds.SOUL_SAGE_SHIELD.get(), SoundSource.NEUTRAL,3.0F,1.0F);
                    if(living.level.isClientSide){
                        Vec3 vec3 = living.getBoundingBox().getCenter();
                        for(int i = 0; i < 40; ++i) {
                            double d0x = living.level.random.nextGaussian() * 0.2D;
                            double d1y = living.level.random.nextGaussian() * 0.2D;
                            double d2z = living.level.random.nextGaussian() * 0.2D;
                            living.level.addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0x, d1y, d2z);
                        }
                    }
                }

            }
            if(this.discardTimer<0){
                this.discard();
            }
        }
        if(!owner.isAlive()){
            this.discard();
        }

        super.tick();
    }

    public void expander(){
        this.discardTimer=10;
        this.discardMoment=true;
    }


    @Override
    public void shootFromRotation(Entity pProjectile, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        this.setInOrbit(false);
        super.shootFromRotation(pProjectile, pX, pY, pZ, pVelocity, pInaccuracy);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (pResult.getEntity() instanceof LivingEntity initialTarget) {
            AreaFireColumnEntity areaFireColumn = new AreaFireColumnEntity(ModEntityTypes.AREA_FIRE_COLUMN.get(), this.level);
            areaFireColumn.setPos(initialTarget.getOnPos().getX(), initialTarget.getOnPos().getY() + 1, initialTarget.getOnPos().getZ());
            areaFireColumn.setOwner((LivingEntity) this.getOwner());
            areaFireColumn.setPowerLevel(this.getPowerLevel());
            areaFireColumn.setDuration(100,25);
            this.level.addFreshEntity(areaFireColumn);
            if (this.getOwner() instanceof LivingEntity) {
                initialTarget.hurt(DamageSource.mobAttack((LivingEntity) this.getOwner()).setMagic(), 3);
            } else {
                initialTarget.hurt(DamageSource.MAGIC, 3);
            }

            if(!this.level.isClientSide){
                initialTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,2));
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
        areaFireColumn.setPowerLevel(this.getPowerLevel());
        areaFireColumn.setDuration(100,25);
        this.level.addFreshEntity(areaFireColumn);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}