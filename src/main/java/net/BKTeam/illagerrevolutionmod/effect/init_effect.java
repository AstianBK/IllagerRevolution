package net.BKTeam.illagerrevolutionmod.effect;

import net.BKTeam.illagerrevolutionmod.potion.Death_mark_effect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.potion.Effect_bleeding;

public class init_effect {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IllagerRevolutionMod.MOD_ID);
    public static final RegistryObject<MobEffect> DEEP_WOUND = REGISTRY.register("deep_wound", Effect_bleeding::new);
    public static final RegistryObject<MobEffect> DEATH_MARK = REGISTRY.register("death_mark", Death_mark_effect::new);

}


