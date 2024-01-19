package net.BKTeam.illagerrevolutionmod;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class ModConstants {

    public static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    public static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;


    public static final Component ILLAGIUM_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.illagium_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    public static final Component ILLAGIUM_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.illagium_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    public static final Component ILLAGIUM_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.illagium_upgrade.base_slot_description")));
    public static final Component ILLAGIUM_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.illagium_upgrade.additions_slot_description")));

    public static final Component ILLAGIUM_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", new ResourceLocation("illagium_upgrade"))).withStyle(TITLE_FORMAT);

    public static final ResourceLocation EMPTY_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
    public static final ResourceLocation EMPTY_SLOT_HOE = new ResourceLocation("item/empty_slot_hoe");
    public static final ResourceLocation EMPTY_SLOT_AXE = new ResourceLocation("item/empty_slot_axe");
    public static final ResourceLocation EMPTY_SLOT_SWORD = new ResourceLocation("item/empty_slot_sword");
    public static final ResourceLocation EMPTY_SLOT_SHOVEL = new ResourceLocation("item/empty_slot_shovel");
    public static final ResourceLocation EMPTY_SLOT_PICKAXE = new ResourceLocation("item/empty_slot_pickaxe");
    public static final ResourceLocation EMPTY_SLOT_INGOT = new ResourceLocation("item/empty_slot_ingot");


    public static final ResourceLocation BEAST_INVENTORY=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/gui/containers/beast_inventory.png");
    public static final String CHANNEL_NAME = "main_channel";
    public static final String LEFT_HAND_BONE_IDENT = "itemHandLeft";
    public static final String RIGHT_HAND_BONE_IDENT = "itemHandRight";
    public static final String POTION_BONE_IDENT = "bipedPotionSlot";
    public static final List<String> LIST_NAME_ZOMBIFIED= Arrays.asList("vindicator","evoker","illager_beast_tamer","pillager");
}
