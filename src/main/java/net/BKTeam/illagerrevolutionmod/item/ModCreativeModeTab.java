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
        List<Item> itemList= Arrays.asList(ModItems.BLADE_KNIGHT_SPAWN_EGG.get(),ModItems.SOUL_SAGE_SPAWN_EGG.get(),ModItems.ILLAGERBEASTTAMER_SPAWN_EGG.get(),
                ModItems.ILLAGERMINERBADLANDS_SPAWN_EGG.get(),ModItems.ILLAGERMINER_SPAWN_EGG.get(),
                ModItems.WILD_RAVAGER_SPAWN_EGG.get(),ModItems.MAULER_SPAWN_EGG.get(),ModItems.SCROUNGER_SPAWN_EGG.get(),
                ModItems.RAKER_SPAWN_EGG.get(),ModItems.ILLAGIUM.get(),ModItems.ILLAGIUM_APPLE.get(),ModItems.RAKER_CLAW.get(),ModItems.BEAST_STAFF.get(),
                ModItems.MAULER_PELT.get(), ModItems.SCROUNGER_FEATHER.get(),ModItems.ILLAGIUM_CROSSBOW.get(),
                ModItems.ILLAGIUM_SWORD.get(),ModItems.ILLAGIUM_PICKAXE.get(),ModItems.ILLAGIUM_AXE.get(),ModItems.ILLAGIUM_SHOVEL.get(),ModItems.ILLAGIUM_HOE.get(),ModItems.ILLAGIUM_RUNED_BLADE.get(),
                ModItems.ILLAGIUM_ALT_RUNED_BLADE.get(),ModItems.OMINOUS_GRIMOIRE.get(),ModItems.SCROUNGER_POUCH.get(),ModItems.RAKER_ARMOR_IRON.get(),ModItems.RAKER_ARMOR_GOLD.get(),
                ModItems.RAKER_ARMOR_DIAMOND.get(),ModItems.RAKER_ARMOR_NETHERITE.get(),ModItems.RAKER_ARMOR_ILLAGIUM.get(),
                ModItems.CLAWS_ARMOR_IRON.get(),ModItems.CLAWS_ARMOR_GOLD.get(),ModItems.CLAWS_ARMOR_DIAMOND.get(),ModItems.CLAWS_ARMOR_NETHERITE.get(),ModItems.CLAWS_ARMOR_ILLAGIUM.get(),
                ModItems.MAULER_ARMOR_DIAMOND.get(),ModItems.MAULER_ARMOR_NETHERITE.get(),ModItems.MAULER_ARMOR_ILLAGIUM.get(),ModItems.MAULER_ARMOR_IRON.get(),ModItems.MAULER_ARMOR_GOLD.get(),ModItems.WILD_RAVAGER_ARMOR_DIAMOND.get(),
                ModItems.WILD_RAVAGER_ARMOR_NETHERITE.get(),ModItems.WILD_RAVAGER_ARMOR_ILLAGIUM.get(),ModItems.WILD_RAVAGER_ARMOR_GOLD.get(),ModItems.WILD_RAVAGER_ARMOR_IRON.get(),
                ModItems.HELMET_MINER.get(),ModItems.HELMET_MINER_REINFORCED.get(),ModItems.JUNK_AXE.get(),
                ModItems.GOGGLES_MINER.get(),ModItems.GOGGLES_MINER_REINFORCED.get(),ModItems.EVOKER_ROBE_ARMOR.get(),ModItems.ILLUSIONER_ROBE_ARMOR.get(),ModItems.VINDICATOR_JACKET_ARMOR.get(),ModItems.VINDICATOR_LEGGINS_ARMOR.get(),
                ModItems.PILLAGER_VEST_ARMOR.get(),ModItems.PILLAGER_LEGGINS_ARMOR.get(),ModItems.PILLAGER_BOOTS_ARMOR.get(),ModItems.ARROW_BEAST.get(),
                ModItems.RUNE_FRAGMENT_BONE.get(),ModItems.RUNE_FRAGMENT_FLESH.get(),ModItems.RUNE_FRAGMENT_UNDYING.get(),ModItems.RUNE_TABLET_UNDYING_BONE.get(),ModItems.RUNE_TABLET_UNDYING_FLESH.get(),
                ModBlocks.RUNE_TABLE_BLOCK.get().asItem(),ModBlocks.DRUM_SPEED.get().asItem(),ModBlocks.DRUM_HEAL.get().asItem(),ModBlocks.DRUM_DAMAGE.get().asItem());

        stackComparator= Ordering.explicit(itemList).onResultOf(ItemStack::getItem);
    }
}
