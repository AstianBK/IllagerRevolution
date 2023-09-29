package net.BKTeam.illagerrevolutionmod.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.item.custom.*;

public class ModItems {

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                          //
    //                                                                                         //
    //
    //
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IllagerRevolutionMod.MOD_ID);
    
    //                                                                                            //
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                          //
    //RUNES                                                                                    //
    //
    //
    public static final RegistryObject<Item> RUNE_FRAGMENT_BONE = ITEMS.register("rune_fragment_bone",()->
            new FragmentItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(16),"rune_fragment_bone"));

    public static final RegistryObject<Item> RUNE_FRAGMENT_FLESH = ITEMS.register("rune_fragment_flesh",()->
            new FragmentItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(16),"rune_fragment_flesh"));

    public static final RegistryObject<Item> RUNE_FRAGMENT_UNDYING = ITEMS.register("rune_fragment_undying",()->
            new FragmentItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(16),"rune_fragment_undying"));

    public static final RegistryObject<Item> RUNE_TABLET_UNDYING_BONE = ITEMS.register("rune_tablet_undying_bone",()->
            new FragmentItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(16),"rune_tablet_undying_bone"));

    public static final RegistryObject<Item> RUNE_TABLET_UNDYING_FLESH = ITEMS.register("rune_tablet_undying_flesh",()->
            new FragmentItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(16),"rune_tablet_undying_flesh"));
    
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                          //
    //ITEMS                                                                                     //
    //
    //
  



    public static final RegistryObject<Item> ILLAGIUM = ITEMS.register("illagium",
            ()-> new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> BEAST_STAFF = ITEMS.register("beast_staff",
            ()-> new BeastStaffItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(1)));

    public static final RegistryObject<Item> RAKER_CLAW = ITEMS.register("raker_claw",()->
            new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SCROUNGER_FEATHER = ITEMS.register("scrounger_feather",
            ()-> new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> MAULER_PELT = ITEMS.register("mauler_pelt",
            ()-> new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SOUL_PROJECTILE = ITEMS.register("soul_projectile",
            ()->new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SOUL_SLASH = ITEMS.register("soul_slash",
            ()->new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ARROW_BEAST = ITEMS.register("arrow_beast",
            ()->new ArrowBeastItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SOUL_HUNTER = ITEMS.register("soul_hunter",
            ()->new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SOUL_WITHER = ITEMS.register("soul_wither",
            ()->new Item(new Item.Properties().stacksTo(1)));


    public static final RegistryObject<Item> ILLAGERMINERBADLANDS_SPAWN_EGG = ITEMS.register("illagerminerbadlands_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ILLAGER_SCAVENGER,0x948e8d, 0x573f2c,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> ILLAGERMINER_SPAWN_EGG = ITEMS.register("illagerminer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ILLAGER_MINER,0x948e8d, 0x3b3635,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> RAKER_SPAWN_EGG = ITEMS.register("raker_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.RAKER,0x575554, 0xd1c299,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> WILD_RAVAGER_SPAWN_EGG = ITEMS.register("wild_ravager_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.WILD_RAVAGER,0x575554, 0xd1c299,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> MAULER_SPAWN_EGG = ITEMS.register("mauler_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.MAULER,0x575554, 0xd1c299,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SCROUNGER_SPAWN_EGG = ITEMS.register("scrounger_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SCROUNGER,0x575554, 0xd1c299,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));
    public static final RegistryObject<Item> BLADE_KNIGHT_SPAWN_EGG = ITEMS.register("blade_knight_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.BLADE_KNIGHT,0x5e7371, 0x3e6b5a,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SOUL_SAGE_SPAWN_EGG = ITEMS.register("soul_sage_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SOUL_SAGE,0x232529, 0xf2ce61,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> ILLAGERBEASTTAMER_SPAWN_EGG = ITEMS.register("illagerbeasttamer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ILLAGER_BEAST_TAMER,0x848e8d, 0x7d8c7d,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));
    //
    //
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                           //
    //ARMOR                                                                                      //
    //
    //

    public static final RegistryObject<Item> EVOKER_ROBE_ARMOR = ITEMS.register("evoker_robe_armor",
            ()-> new ArmorEvokerRobeItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.CHEST,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> ILLUSIONER_ROBE_ARMOR = ITEMS.register("illusioner_robe_armor",
            ()-> new ArmorIllusionerRobeItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.CHEST,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> PILLAGER_VEST_ARMOR = ITEMS.register("pillager_vest_armor",
            ()-> new ArmorPillagerVestItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.CHEST,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> PILLAGER_LEGGINS_ARMOR = ITEMS.register("pillager_leggins_armor",
            ()-> new ArmorPillagerVestItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.LEGS,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));
    
    public static final RegistryObject<Item> PILLAGER_BOOTS_ARMOR = ITEMS.register("pillager_boots_armor",
            ()-> new ArmorPillagerVestItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.FEET,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> VINDICATOR_JACKET_ARMOR = ITEMS.register("vindicator_jacket_armor",
            ()-> new ArmorVindicatorJacketItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.CHEST,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> VINDICATOR_LEGGINS_ARMOR = ITEMS.register("vindicator_leggins_armor",
            ()-> new ArmorVindicatorJacketItem(ModArmorMaterials.ILLAGERARMOR, EquipmentSlot.LEGS,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));
    
    public static final RegistryObject<Item> HELMET_MINER_REINFORCED = ITEMS.register("helmet_miner_reinforced",
            ()-> new IllagiumArmorItem(ModArmorMaterials.ILLAGIUM, EquipmentSlot.HEAD,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> HELMET_MINER = ITEMS.register("helmet_miner",()->
            new IllagiumArmorItem(ModArmorMaterials.BASICILLAGIUM,EquipmentSlot.HEAD,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> GOGGLES_MINER = ITEMS.register("goggles_miner",()->
            new ArmorGogglesItem(ModArmorMaterials.BASICILLAGIUM,EquipmentSlot.HEAD,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> GOGGLES_MINER_REINFORCED = ITEMS.register("goggles_miner_reinforced",()->
            new ArmorGogglesItem(ModArmorMaterials.ILLAGIUM,EquipmentSlot.HEAD,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> WILD_RAVAGER_ARMOR_ILLAGIUM = ITEMS.register("wild_ravager_armor_illagium",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1550),20,ModArmorMaterials.ILLAGIUM,0.0d,0,EquipmentSlot.LEGS,Beast.WILD_RAVAGER));

    public static final RegistryObject<Item> WILD_RAVAGER_ARMOR_NETHERITE = ITEMS.register("wild_ravager_armor_netherite",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(800),30,ArmorMaterials.NETHERITE,0.0d,0,EquipmentSlot.LEGS,Beast.WILD_RAVAGER));

    public static final RegistryObject<Item> WILD_RAVAGER_ARMOR_DIAMOND = ITEMS.register("wild_ravager_armor_diamond",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(750),25,ArmorMaterials.DIAMOND,0.0d,0,EquipmentSlot.LEGS,Beast.WILD_RAVAGER));

    public static final RegistryObject<Item> WILD_RAVAGER_ARMOR_GOLD = ITEMS.register("wild_ravager_armor_gold",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(350),10,ArmorMaterials.GOLD,0.0d,0,EquipmentSlot.LEGS,Beast.WILD_RAVAGER));

    public static final RegistryObject<Item> WILD_RAVAGER_ARMOR_IRON = ITEMS.register("wild_ravager_armor_iron",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(500),15,ArmorMaterials.IRON,0.0d,0,EquipmentSlot.LEGS,Beast.WILD_RAVAGER));

    public static final RegistryObject<Item> MAULER_ARMOR_ILLAGIUM = ITEMS.register("mauler_armor_illagium",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1550),20,ModArmorMaterials.ILLAGIUM,0.0d,0,EquipmentSlot.LEGS,Beast.MAULER));

    public static final RegistryObject<Item> MAULER_ARMOR_NETHERITE = ITEMS.register("mauler_armor_netherite",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(800),30,ArmorMaterials.NETHERITE,0.0d,0,EquipmentSlot.LEGS,Beast.MAULER));

    public static final RegistryObject<Item> MAULER_ARMOR_DIAMOND = ITEMS.register("mauler_armor_diamond",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(750),25,ArmorMaterials.DIAMOND,0.0d,0,EquipmentSlot.LEGS,Beast.MAULER));

    public static final RegistryObject<Item> MAULER_ARMOR_GOLD = ITEMS.register("mauler_armor_gold",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(350),5,ArmorMaterials.GOLD,0.0d,0,EquipmentSlot.LEGS,Beast.MAULER));

    public static final RegistryObject<Item> MAULER_ARMOR_IRON = ITEMS.register("mauler_armor_iron",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(500),10,ArmorMaterials.IRON,0.0d,0,EquipmentSlot.LEGS,Beast.MAULER));

    public static final RegistryObject<Item> RAKER_ARMOR_ILLAGIUM = ITEMS.register("scrapper_armor_illagium",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1550),20,ModArmorMaterials.ILLAGIUM,0.0d,0,EquipmentSlot.FEET,Beast.RAKER));

    public static final RegistryObject<Item> RAKER_ARMOR_NETHERITE = ITEMS.register("scrapper_armor_netherite",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(800),30,ArmorMaterials.NETHERITE,0.0d,0,EquipmentSlot.FEET,Beast.RAKER));

    public static final RegistryObject<Item> RAKER_ARMOR_DIAMOND = ITEMS.register("scrapper_armor_diamond",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(750),25,ArmorMaterials.DIAMOND,0.0d,0,EquipmentSlot.FEET,Beast.RAKER));

    public static final RegistryObject<Item> RAKER_ARMOR_GOLD = ITEMS.register("scrapper_armor_gold",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(350),5,ArmorMaterials.GOLD,0.0d,0,EquipmentSlot.FEET,Beast.RAKER));

    public static final RegistryObject<Item> RAKER_ARMOR_IRON = ITEMS.register("scrapper_armor_iron",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(500),10,ArmorMaterials.IRON,0.0d,0,EquipmentSlot.FEET,Beast.RAKER));


    public static final RegistryObject<Item> SCROUNGER_POUCH = ITEMS.register("scrounger_pouch",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(200),0,ArmorMaterials.LEATHER,0.0d,0,EquipmentSlot.CHEST,Beast.SCROUNGER));


    public static final RegistryObject<Item> CLAWS_ARMOR_ILLAGIUM = ITEMS.register("claws_armor_illagium",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1500),0,ModArmorMaterials.ILLAGIUM,0.5d,30,EquipmentSlot.LEGS,Beast.RAKER));

    public static final RegistryObject<Item> CLAWS_ARMOR_NETHERITE = ITEMS.register("claws_armor_netherite",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(500),0,ArmorMaterials.NETHERITE,0.7d,30,EquipmentSlot.LEGS,Beast.RAKER));

    public static final RegistryObject<Item> CLAWS_ARMOR_DIAMOND = ITEMS.register("claws_armor_diamond",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(450),0,ArmorMaterials.DIAMOND,0.6d,30,EquipmentSlot.LEGS,Beast.RAKER));

    public static final RegistryObject<Item> CLAWS_ARMOR_GOLD = ITEMS.register("claws_armor_gold",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(100),0,ArmorMaterials.GOLD,0.2d,60,EquipmentSlot.LEGS,Beast.RAKER));

    public static final RegistryObject<Item> CLAWS_ARMOR_IRON = ITEMS.register("claws_armor_iron",()->
            new BeastArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(200),0,ArmorMaterials.IRON,0.3d,30,EquipmentSlot.LEGS,Beast.RAKER));




    //
    //
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                           //
    //TOOLS                                                                                      //
    //
    //

    public static final RegistryObject<Item> ILLAGIUM_SWORD = ITEMS.register("illagium_sword",
            ()->new BleedingSwordItem(ModTiers.ILLAGIUM,6,-2.5F,props()));

    public static final RegistryObject<Item> ILLAGIUM_AXE = ITEMS.register("illagium_axe",
            ()->new AxeItem(ModTiers.ILLAGIUM,8,-3.1F,props()));
    public static final RegistryObject<Item> ILLAGIUM_PICKAXE = ITEMS.register("illagium_pickaxe",
            ()->new PickaxeItem(ModTiers.ILLAGIUM,4,-3.0F,props()));

    public static final RegistryObject<Item> ILLAGIUM_HOE = ITEMS.register("illagium_hoe",
            ()->new HoeItem(ModTiers.ILLAGIUM,0,0,props()));

    public static final RegistryObject<Item> ILLAGIUM_SHOVEL = ITEMS.register("illagium_shovel",
            ()->new ShovelItem(ModTiers.ILLAGIUM,4,-3.1F,props()));

    public static final RegistryObject<Item> ILLAGIUM_CROSSBOW = ITEMS.register("illagium_crossbow",
            ()->new IllagiumCrossbowItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(1).durability(1000)));

    public static final RegistryObject<Item> ILLAGIUM_RUNED_BLADE = ITEMS.register("illagium_runed_blade",
            ()->new SwordRuneBladeItem(ModTiers.ILLAGIUM,8,-2.6F,props()));

    public static final RegistryObject<Item> ILLAGIUM_ALT_RUNED_BLADE = ITEMS.register("illagium_alt_runed_blade",
            ()->new VariantRuneBladeItem(ModTiers.ILLAGIUM,6,-2.6F,props()));

    public static final RegistryObject<Item> OMINOUS_GRIMOIRE = ITEMS.register("ominous_grimoire",
            ()->new AnimatedItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).stacksTo(1)));

    public static final RegistryObject<Item> JUNK_AXE = ITEMS.register("junk_axe",
            ()->new JunkAxeItem(ModTiers.JUNK,0,3.5F,props()));

    public static final RegistryObject<Item> FAKE_JUNK_AXE = ITEMS.register("fake_junk_axe",
            ()->new AxeItem(ModTiers.JUNK,0,3.5F,new Item.Properties()));

    public static final RegistryObject<Item> FAKE_RUNED_BLADE = ITEMS.register("fake_runed_blade",
            ()->new FakeSwordItem(ModTiers.ILLAGIUM,8,-2.6F,new Item.Properties()));

    public static final RegistryObject<Item> FAKE_ALT_RUNED_BLADE = ITEMS.register("fake_alt_runed_blade",
            ()->new FakeSwordItem(ModTiers.ILLAGIUM,6,-2.6F,new Item.Properties()));

    private static Item.Properties props() {
        return new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB);
    }



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
