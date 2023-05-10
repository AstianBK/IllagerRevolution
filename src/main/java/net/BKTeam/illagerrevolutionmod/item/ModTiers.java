package net.BKTeam.illagerrevolutionmod.item;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;


public class ModTiers {
    public static final ForgeTier ILLAGIUM = new ForgeTier(5, 5000, 7.5f,
            1f, 15, BlockTags.NEEDS_IRON_TOOL,
            () -> Ingredient.of(ModItems.ILLAGIUM.get()));
    
        public static final ForgeTier JUNK = new ForgeTier(0, 350, 1.0f,
            0f, 0, BlockTags.NEEDS_IRON_TOOL,
            () -> Ingredient.of(ModItems.JUNK_AXE.get()));




}
