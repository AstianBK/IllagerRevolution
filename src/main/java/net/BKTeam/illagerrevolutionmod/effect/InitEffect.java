package net.BKTeam.illagerrevolutionmod.effect;

import net.BKTeam.illagerrevolutionmod.potion.MauledEffect;
import net.BKTeam.illagerrevolutionmod.potion.DeathMarkEffect;
import net.BKTeam.illagerrevolutionmod.potion.EffectBleeding;
import net.BKTeam.illagerrevolutionmod.potion.SoulBurnEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;

public class InitEffect {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IllagerRevolutionMod.MOD_ID);
    public static final RegistryObject<MobEffect> DEEP_WOUND = REGISTRY.register("deep_wound", EffectBleeding::new);
    public static final RegistryObject<MobEffect> MAULED = REGISTRY.register("mauled", MauledEffect::new);
    public static final RegistryObject<MobEffect> DEATH_MARK = REGISTRY.register("death_mark", DeathMarkEffect::new);

    public static final RegistryObject<MobEffect> SOUL_BURN = REGISTRY.register("soul_burn", SoulBurnEffect::new);

}


