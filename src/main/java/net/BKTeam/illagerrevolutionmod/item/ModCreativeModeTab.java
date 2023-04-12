package net.BKTeam.illagerrevolutionmod.item;

import com.google.common.collect.Ordering;
import net.BKTeam.illagerrevolutionmod.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;

import java.util.*;

public class ModCreativeModeTab {

    static Comparator<ItemStack> stackComparator;
    public static final CreativeModeTab ILLAGERREVOLUTION_TAB = new CreativeModeTab("illagerrevolutiontab") {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ILLAGIUM.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> pItems) {
            super.fillItemList(pItems);
            PreOrdenInit();
            pItems.sort(stackComparator);
        }
    };

    public static void PreOrdenInit(){
        List<Item> itemList= Arrays.asList(ModItems.BLADE_KNIGHT_SPAWN_EGG.get(),ModItems.ILLAGERBEASTTAMER_SPAWN_EGG.get(),ModItems.ILLAGERMINERBADLANDS_SPAWN_EGG.get(),ModItems.ILLAGERMINER_SPAWN_EGG.get(),
                ModItems.SCRAPPER_SPAWN_EGG.get(),ModItems.ILLAGIUM.get(),ModItems.ENCRUSTED_LAPIS.get(),ModItems.RUSTIC_CHISEL.get(),ModItems.SCRAPER_CLAW.get()
                ,ModItems.ILLAGIUM_CLEAVER.get(),ModItems.ILLAGIUM_HAMMER.get(),ModItems.ILLAGIUM_HELBERD.get(),ModItems.ILLAGIUM_MACE.get(),ModItems.ILLAGIUM_SCYTHE.get(),ModItems.ILLAGIUM_RUNED_BLADE.get(),
                ModItems.ILLAGIUM_ALT_RUNED_BLADE.get(),ModItems.SCRAPPER_ARMOR_IRON.get(),ModItems.SCRAPPER_ARMOR_GOLD.get(),ModItems.SCRAPPER_ARMOR_DIAMOND.get(),ModItems.SCRAPPER_ARMOR_NETHERITE.get(),ModItems.SCRAPPER_ARMOR_ILLAGIUM.get(),
                ModItems.HELMET_MINER.get(),ModItems.HELMET_MINER_REINFORCED.get()
                ,ModItems.GOGGLES_MINER.get(),ModItems.GOGGLES_MINER_REINFORCED.get(),ModItems.EVOKER_ROBE_ARMOR.get(),ModItems.ILLUSIONER_ROBE_ARMOR.get(),ModItems.VINDICATOR_JACKET_ARMOR.get(),ModItems.VINDICATOR_LEGGINS_ARMOR.get(),ModItems.PILLAGER_VEST_ARMOR.get(),ModItems.PILLAGER_LEGGINS_ARMOR.get(),ModItems.PILLAGER_BOOTS_ARMOR.get(),ModItems.CLAWS_ARMOR_IRON.get(),ModItems.CLAWS_ARMOR_GOLD.get(),ModItems.CLAWS_ARMOR_DIAMOND.get()
                ,ModItems.CLAWS_ARMOR_NETHERITE.get(),ModItems.CLAWS_ARMOR_ILLAGIUM.get(),ModItems.ARROW_BEAST.get()
                ,ModItems.RUNE_FRAGMENT_BONE.get(),ModItems.RUNE_FRAGMENT_FLESH.get(),ModItems.RUNE_FRAGMENT_UNDYING.get(),ModItems.RUNE_TABLET_UNDYING_BONE.get(),ModItems.RUNE_TABLET_UNDYING_FLESH.get()
                , ModBlocks.RUNE_TABLE_BLOCK.get().asItem());

        stackComparator= Ordering.explicit(itemList).onResultOf(ItemStack::getItem);
    }
}
