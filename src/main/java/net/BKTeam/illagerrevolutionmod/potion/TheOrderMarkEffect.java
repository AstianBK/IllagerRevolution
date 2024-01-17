package net.BKTeam.illagerrevolutionmod.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TheOrderMarkEffect extends MobEffect {

    public TheOrderMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public String getDescriptionId() {
        return "effect.illagerrevolutionmod.death_mark";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {

    }
}
