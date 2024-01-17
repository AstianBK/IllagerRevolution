package net.BKTeam.illagerrevolutionmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties ILLAGIUM_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.1F).effect(new MobEffectInstance(MobEffects.CONFUSION, 250, 2), 1.0F).effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 1), 1.0F).effect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 2), 1.0F).effect(new MobEffectInstance(MobEffects.REGENERATION, 250, 2), 1.0F).alwaysEat().build();
}
