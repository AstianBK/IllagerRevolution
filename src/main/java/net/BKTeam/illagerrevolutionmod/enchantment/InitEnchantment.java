package net.BKTeam.illagerrevolutionmod.enchantment;

import net.BKTeam.illagerrevolutionmod.enchantment.custom.InsightEnchantment;
import net.BKTeam.illagerrevolutionmod.enchantment.custom.SerratedEdgeEnchantment;
import net.BKTeam.illagerrevolutionmod.enchantment.custom.SoulSlashEnchantment;
import net.BKTeam.illagerrevolutionmod.enchantment.custom.WaryLensesEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;

public class InitEnchantment {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, IllagerRevolutionMod.MOD_ID);
    public static final RegistryObject<Enchantment>  SERRATED_EDGE= REGISTRY.register("serrated_edge", SerratedEdgeEnchantment::new);

    //public static final RegistryObject<Enchantment>  BEAST_SLAYER= REGISTRY.register("beast_slayer", BeastSlayerEnchantment::new);

    public static final RegistryObject<Enchantment> WARY_LENSES = REGISTRY.register("wary_lenses", WaryLensesEnchantment::new);

    public static final RegistryObject<Enchantment> SOUL_SLASH = REGISTRY.register("soul_slash", SoulSlashEnchantment::new);

    public static final RegistryObject<Enchantment> INSIGHT = REGISTRY.register("insight", InsightEnchantment::new);
}
