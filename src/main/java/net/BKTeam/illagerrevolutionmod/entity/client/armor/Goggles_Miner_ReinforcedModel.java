package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorGogglesItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Goggles_Miner_ReinforcedModel extends AnimatedGeoModel<ArmorGogglesItem> {

    @Override
    public ResourceLocation getModelLocation(ArmorGogglesItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/goggles_miner.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorGogglesItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/"+object.getMaterial().getName()+"_goggles.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ArmorGogglesItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illagium_goggles.animation.json");
    }
}
