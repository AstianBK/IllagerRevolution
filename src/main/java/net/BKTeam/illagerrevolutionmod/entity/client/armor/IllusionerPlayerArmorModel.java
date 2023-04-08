package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorIllusionerRobeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class IllusionerPlayerArmorModel extends AnimatedGeoModel<ArmorIllusionerRobeItem> {

    @Override
    public ResourceLocation getModelLocation(ArmorIllusionerRobeItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/illusioner_player_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorIllusionerRobeItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/illusioner_robe_armor.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ArmorIllusionerRobeItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illager_player_armor.animation.json");
    }

}
