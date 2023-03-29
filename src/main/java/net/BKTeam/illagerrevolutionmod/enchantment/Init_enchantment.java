package net.BKTeam.illagerrevolutionmod.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;

public class Init_enchantment {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, IllagerRevolutionMod.MOD_ID);
    public static final RegistryObject<Enchantment>  SERRATED_EDGE= REGISTRY.register("serrated_edge", SerratedEdgeEnchantment::new);
    public static final RegistryObject<Enchantment>  WARYLENSES= REGISTRY.register("wary_lenses", WaryLensesEnchantment::new);

}
