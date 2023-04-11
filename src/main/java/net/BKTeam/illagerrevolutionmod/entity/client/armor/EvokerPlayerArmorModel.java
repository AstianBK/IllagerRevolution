package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorEvokerRobeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EvokerPlayerArmorModel extends AnimatedGeoModel<ArmorEvokerRobeItem> {

    @Override
    public ResourceLocation getModelLocation(ArmorEvokerRobeItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/evoker_player_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorEvokerRobeItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/evoker_armor/evoker_armor.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ArmorEvokerRobeItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illager_player_armor.animation.json");
    }
}
