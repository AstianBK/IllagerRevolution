package net.BKTeam.illagerrevolutionmod.block;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.block.custom.RuneTableBlock;
import net.BKTeam.illagerrevolutionmod.item.ModCreativeModeTab;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, IllagerRevolutionMod.MOD_ID);

    //public static final RegistryObject<Block> ILLAGIUM_BLOCK = registerBlock("illagium_block",
            //() -> new Block(BlockBehaviour.Properties.of(Material.HEAVY_METAL)
                    //.strength(9f).requiresCorrectToolForDrops().explosionResistance(5.0F)), ModCreativeModeTab.ILLAGERREVOLUTION_TAB);

    public static final RegistryObject<Block> RUNE_TABLE_BLOCK = registerBlock("rune_table_block",
            () -> new RuneTableBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS).noOcclusion()),
            ModCreativeModeTab.ILLAGERREVOLUTION_TAB);



    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends  Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
    return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
            new Item.Properties().tab(tab)));
}

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
