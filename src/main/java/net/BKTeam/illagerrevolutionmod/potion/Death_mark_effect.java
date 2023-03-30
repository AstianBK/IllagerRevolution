package net.BKTeam.illagerrevolutionmod.potion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Death_mark_effect extends MobEffect {

    public Death_mark_effect() {
            super(MobEffectCategory.HARMFUL, 0);}

    @Override
    public String getDescriptionId() {
            return "effect.illagerrevolutionmod.death_mark";}

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
    }


    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        float f = pLivingEntity.yBodyRot * ((float) Math.PI / 180F) + Mth.cos((float) pLivingEntity.tickCount * 0.6662F) * 0.25F;
        float f1 = Mth.cos(f);
        float f2 = Mth.sin(f);
        pLivingEntity.level.addParticle(ParticleTypes.SMOKE,pLivingEntity.getX()+f1*0.2d,pLivingEntity.getY(),pLivingEntity.getZ()+f2*0.2d,0.0F,0.0F,0.0F);
        pLivingEntity.level.addParticle(ParticleTypes.SMOKE,pLivingEntity.getX()-f1*0.2d,pLivingEntity.getY(),pLivingEntity.getZ()-f2*0.2d,0.0F,0.0F,0.0F);
    }


}
