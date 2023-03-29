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
    //ITEMS                                                                                     //
    //
    //
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IllagerRevolutionMod.MOD_ID);



    public static final RegistryObject<Item> ILLAGIUM = ITEMS.register("illagium",
            ()-> new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> ENCRUSTED_LAPIS = ITEMS.register("encrusted_lapis",()->
            new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SCRAPER_CLAW = ITEMS.register("scraper_claw",()->
            new Item(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SOUL_PROJECTILE = ITEMS.register("soul_projectile",
            ()->new Soul_ProjectileItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ARROW_BEAST = ITEMS.register("arrow_beast",
            ()->new ArrowBeastItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SOUL_HUNTER = ITEMS.register("soul_hunter",
            ()->new Soul_HunterItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SOUL_WITHER = ITEMS.register("soul_wither",
            ()->new Soul_WitherItem(new Item.Properties().stacksTo(1)));



    public static final RegistryObject<Item> ILLAGERMINERBADLANDS_SPAWN_EGG = ITEMS.register("illagerminerbadlands_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ILLAGERMINERBADLANDS,0x948e8d, 0x573f2c,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> ILLAGERMINER_SPAWN_EGG = ITEMS.register("illagerminer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ILLAGERMINER,0x948e8d, 0x3b3635,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> SCRAPPER_SPAWN_EGG = ITEMS.register("scrapper_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.RAKER,0x575554, 0xd1c299,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> BLADE_KNIGHT_SPAWN_EGG = ITEMS.register("blade_knight_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.BLADE_KNIGHT,0x5e7371, 0x3e6b5a,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));

    public static final RegistryObject<Item> ILLAGERBEASTTAMER_SPAWN_EGG = ITEMS.register("illagerbeasttamer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ILLAGERBEASTTAMER,0x848e8d, 0x7d8c7d,
                    new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB)));
    //
    //
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                           //
    //ARMOR                                                                                      //
    //
    //

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

    public static final RegistryObject<Item> SCRAPPER_ARMOR_ILLAGIUM = ITEMS.register("scrapper_armor_illagium",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1550),20,"illagium",ModArmorMaterials.ILLAGIUM,0.0d,0,EquipmentSlot.CHEST));

    public static final RegistryObject<Item> SCRAPPER_ARMOR_NETHERITE = ITEMS.register("scrapper_armor_netherite",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1000),30,"netherite",ArmorMaterials.NETHERITE,0.0d,0,EquipmentSlot.CHEST));

    public static final RegistryObject<Item> SCRAPPER_ARMOR_DIAMOND = ITEMS.register("scrapper_armor_diamond",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(700),25,"diamond",ArmorMaterials.DIAMOND,0.0d,0,EquipmentSlot.CHEST));

    public static final RegistryObject<Item> SCRAPPER_ARMOR_GOLD = ITEMS.register("scrapper_armor_gold",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(350),5,"gold",ArmorMaterials.GOLD,0.0d,0,EquipmentSlot.CHEST));

    public static final RegistryObject<Item> SCRAPPER_ARMOR_IRON = ITEMS.register("scrapper_armor_iron",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(500),10,"iron",ArmorMaterials.IRON,0.0d,0,EquipmentSlot.CHEST));



    public static final RegistryObject<Item> CLAWS_ARMOR_ILLAGIUM = ITEMS.register("claws_armor_illagium",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(1000),0,"illagium",ModArmorMaterials.ILLAGIUM,0.5d,60,EquipmentSlot.LEGS));

    public static final RegistryObject<Item> CLAWS_ARMOR_NETHERITE = ITEMS.register("claws_armor_netherite",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(500),0,"netherite",ArmorMaterials.NETHERITE,0.8d,70,EquipmentSlot.LEGS));

    public static final RegistryObject<Item> CLAWS_ARMOR_DIAMOND = ITEMS.register("claws_armor_diamond",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(300),0,"diamond",ArmorMaterials.DIAMOND,0.6d,60,EquipmentSlot.LEGS));

    public static final RegistryObject<Item> CLAWS_ARMOR_GOLD = ITEMS.register("claws_armor_gold",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(100),0,"gold",ArmorMaterials.GOLD,0.2d,40,EquipmentSlot.LEGS));

    public static final RegistryObject<Item> CLAWS_ARMOR_IRON = ITEMS.register("claws_armor_iron",()->
            new RakerArmorItem(new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB).durability(200),0,"iron",ArmorMaterials.IRON,0.3d,50,EquipmentSlot.LEGS));




    //
    //
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    //                                                                                           //
    //TOOLS                                                                                      //
    //
    //

    public static final RegistryObject<Item> ILLAGIUM_CLEAVER = ITEMS.register("illagium_cleaver",
            ()->new BleedingSwordItem(ModTiers.ILLAGIUM,8,-3.0F,props()));

    public static final RegistryObject<Item> ILLAGIUM_HELBERD = ITEMS.register("illagium_helberd",
            ()->new AxeItem(ModTiers.ILLAGIUM,7,-3.0F,props()));
    public static final RegistryObject<Item> ILLAGIUM_HAMMER = ITEMS.register("illagium_hammer",
            ()->new PickaxeItem(ModTiers.ILLAGIUM,1,-3.0F,props()));

    public static final RegistryObject<Item> ILLAGIUM_SCYTHE = ITEMS.register("illagium_scythe",
            ()->new HoeItem(ModTiers.ILLAGIUM,0,1,props()));

    public static final RegistryObject<Item> ILLAGIUM_MACE = ITEMS.register("illagium_mace",
            ()->new ShovelItem(ModTiers.ILLAGIUM,2,-3.0F,props()));

    public static final RegistryObject<Item> ILLAGIUM_RUNED_BLADE = ITEMS.register("illagium_runed_blade",
            ()->new SwordRuneBladeItem(ModTiers.ILLAGIUM,4,-2.5F,props()));
    private static Item.Properties props() {
        return new Item.Properties().tab(ModCreativeModeTab.ILLAGERREVOLUTION_TAB);
    }




    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
