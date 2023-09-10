package net.BKTeam.illagerrevolutionmod.potion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SoulBurnEffect extends MobEffect {

    public SoulBurnEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public String getDescriptionId() {
        return "effect.illagerrevolutionmod.soul_burn";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {

    }
}
