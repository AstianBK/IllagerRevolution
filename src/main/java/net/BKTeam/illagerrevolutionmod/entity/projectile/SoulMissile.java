package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.particle.custom.BloodBKParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SoulMissile extends ProjectileMagic{
    public SoulMissile(EntityType<? extends ThrowableProjectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
        this.setInOrbit(true);
    }

    public SoulMissile(LivingEntity thrower, Level level) {
        super(ModEntityTypes.SOUL_MISSILE.get() , thrower , level , 0);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(pResult.getEntity() instanceof LivingEntity target){
            Entity entity = this.level.getEntity(this.getOwnerID());
            if(entity instanceof LivingEntity){
                target.hurt(DamageSource.mobAttack((LivingEntity) entity),2.0F+1.0F*this.getPowerLevel());
            }else {
                target.hurt(DamageSource.MAGIC,2.0F+1.0F*this.getPowerLevel());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    protected SimpleParticleType getParticle() {
        return ModParticles.RUNE_CURSED_PARTICLES.get();
    }
}
