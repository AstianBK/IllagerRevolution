package net.BKTeam.illagerrevolutionmod.item;

import com.google.common.collect.Ordering;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IllagerRevolutionMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ILLAGERREVOLUTION_TAB = TABS.register("bk_items",()-> CreativeModeTab.builder()
            .icon(()->new ItemStack(ModItems.ILLAGIUM.get()))
            .title(Component.translatable("itemGroup.illagerrevolutiontab"))
            .displayItems((s,a)-> {
                a.accept(ModItems.BLADE_KNIGHT_SPAWN_EGG.get());
                a.accept(ModItems.SOUL_SAGE_SPAWN_EGG.get());
                a.accept(ModItems.ILLAGERBEASTTAMER_SPAWN_EGG.get());
                a.accept(ModItems.ILLAGERMINERBADLANDS_SPAWN_EGG.get());
                a.accept(ModItems.ILLAGERMINER_SPAWN_EGG.get());
                a.accept(ModItems.WILD_RAVAGER_SPAWN_EGG.get());
                a.accept(ModItems.MAULER_SPAWN_EGG.get());
                a.accept(ModItems.SCROUNGER_SPAWN_EGG.get());
                a.accept(ModItems.RAKER_SPAWN_EGG.get());
                a.accept(ModItems.ILLAGIUM.get());
                a.accept(ModItems.ILLAGIUM_APPLE.get());
                a.accept(ModItems.RAKER_CLAW.get());
                a.accept(ModItems.BEAST_STAFF.get());
                a.accept(ModItems.MAULER_PELT.get());
                a.accept(ModItems.SCROUNGER_FEATHER.get());
                a.accept(ModItems.ILLAGIUM_CROSSBOW.get());
                a.accept(ModItems.ILLAGIUM_SWORD.get());
                a.accept(ModItems.ILLAGIUM_PICKAXE.get());
                a.accept(ModItems.ILLAGIUM_AXE.get());
                a.accept(ModItems.ILLAGIUM_SHOVEL.get());
                a.accept(ModItems.ILLAGIUM_HOE.get());
                a.accept(ModItems.ILLAGIUM_RUNED_BLADE.get());
                a.accept(ModItems.ILLAGIUM_ALT_RUNED_BLADE.get());
                a.accept(ModItems.OMINOUS_GRIMOIRE.get());
                a.accept(ModItems.SCROUNGER_POUCH.get());
                a.accept(ModItems.RAKER_ARMOR_IRON.get());
                a.accept(ModItems.RAKER_ARMOR_GOLD.get());
                a.accept(ModItems.RAKER_ARMOR_DIAMOND.get());
                a.accept(ModItems.RAKER_ARMOR_NETHERITE.get());
                a.accept(ModItems.RAKER_ARMOR_ILLAGIUM.get());
                a.accept(ModItems.CLAWS_ARMOR_IRON.get());
                a.accept(ModItems.CLAWS_ARMOR_GOLD.get());
                a.accept(ModItems.CLAWS_ARMOR_DIAMOND.get());
                a.accept(ModItems.CLAWS_ARMOR_NETHERITE.get());
                a.accept(ModItems.CLAWS_ARMOR_ILLAGIUM.get());
                a.accept(ModItems.MAULER_ARMOR_IRON.get());
                a.accept(ModItems.MAULER_ARMOR_GOLD.get());
                a.accept(ModItems.MAULER_ARMOR_DIAMOND.get());
                a.accept(ModItems.MAULER_ARMOR_NETHERITE.get());
                a.accept(ModItems.MAULER_ARMOR_ILLAGIUM.get());
                a.accept(ModItems.WILD_RAVAGER_ARMOR_IRON.get());
                a.accept(ModItems.WILD_RAVAGER_ARMOR_GOLD.get());
                a.accept(ModItems.WILD_RAVAGER_ARMOR_DIAMOND.get());
                a.accept(ModItems.WILD_RAVAGER_ARMOR_NETHERITE.get());
                a.accept(ModItems.WILD_RAVAGER_ARMOR_ILLAGIUM.get());
                a.accept(ModItems.HELMET_MINER.get());
                a.accept(ModItems.HELMET_MINER_REINFORCED.get());
                a.accept(ModItems.JUNK_AXE.get());
                a.accept(ModItems.GOGGLES_MINER.get());
                a.accept(ModItems.GOGGLES_MINER_REINFORCED.get());
                a.accept(ModItems.EVOKER_ROBE_ARMOR.get());
                a.accept(ModItems.ILLUSIONER_ROBE_ARMOR.get());
                a.accept(ModItems.VINDICATOR_JACKET_ARMOR.get());
                a.accept(ModItems.VINDICATOR_LEGGINS_ARMOR.get());
                a.accept(ModItems.PILLAGER_VEST_ARMOR.get());
                a.accept(ModItems.PILLAGER_LEGGINS_ARMOR.get());
                a.accept(ModItems.PILLAGER_BOOTS_ARMOR.get());
                a.accept(ModItems.ARROW_BEAST.get());
                a.accept(ModItems.RUNE_FRAGMENT_BONE.get());
                a.accept(ModItems.RUNE_FRAGMENT_FLESH.get());
                a.accept(ModItems.RUNE_FRAGMENT_UNDYING.get());
                a.accept(ModItems.RUNE_TABLET_UNDYING_BONE.get());
                a.accept(ModItems.RUNE_TABLET_UNDYING_FLESH.get());
                a.accept(ModBlocks.RUNE_TABLE_BLOCK.get());
                a.accept(ModBlocks.DRUM_HEAL.get());
                a.accept(ModBlocks.DRUM_DAMAGE.get());
                a.accept(ModBlocks.DRUM_SPEED.get());
            })
            .build());


}
