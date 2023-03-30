package net.BKTeam.illagerrevolutionmod.item;

import com.google.common.collect.Ordering;
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
        List<Item> itemList= Arrays.asList(ModItems.BLADE_KNIGHT_SPAWN_EGG.get(),ModItems.ILLAGERBEASTTAMER_SPAWN_EGG.get(),ModItems.ILLAGERMINERBADLANDS_SPAWN_EGG.get(),ModItems.ILLAGERMINER_SPAWN_EGG.get(),ModItems.SCRAPPER_SPAWN_EGG.get(),ModItems.FALLEN_KNIGHT_SPAWN_EGG.get(),ModItems.ILLAGIUM.get(),ModItems.ENCRUSTED_LAPIS.get(),ModItems.SCRAPER_CLAW.get()
                ,ModItems.ILLAGIUM_CLEAVER.get(),ModItems.ILLAGIUM_HAMMER.get(),ModItems.ILLAGIUM_HELBERD.get(),ModItems.ILLAGIUM_MACE.get(),ModItems.ILLAGIUM_SCYTHE.get(),ModItems.ILLAGIUM_RUNED_BLADE.get()
                ,ModItems.SCRAPPER_ARMOR_IRON.get(),ModItems.SCRAPPER_ARMOR_GOLD.get(),ModItems.SCRAPPER_ARMOR_DIAMOND.get(),ModItems.SCRAPPER_ARMOR_NETHERITE.get(),ModItems.SCRAPPER_ARMOR_ILLAGIUM.get(),ModItems.HELMET_MINER.get(),ModItems.HELMET_MINER_REINFORCED.get()
                ,ModItems.GOGGLES_MINER.get(),ModItems.GOGGLES_MINER_REINFORCED.get(),ModItems.CLAWS_ARMOR_IRON.get(),ModItems.CLAWS_ARMOR_GOLD.get(),ModItems.CLAWS_ARMOR_DIAMOND.get(),ModItems.CLAWS_ARMOR_NETHERITE.get(),ModItems.CLAWS_ARMOR_ILLAGIUM.get(),ModItems.ARROW_BEAST.get());

        stackComparator= Ordering.explicit(itemList).onResultOf(ItemStack::getItem);
    }
}