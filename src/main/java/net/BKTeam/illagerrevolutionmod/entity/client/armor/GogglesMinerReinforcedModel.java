package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorGogglesItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GogglesMinerReinforcedModel extends GeoModel<ArmorGogglesItem> {

    @Override
    public ResourceLocation getModelResource(ArmorGogglesItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/goggles_miner.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArmorGogglesItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/goggles/"+object.getMaterial().getName()+"_goggles.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArmorGogglesItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illagium_goggles.animation.json");
    }
}
