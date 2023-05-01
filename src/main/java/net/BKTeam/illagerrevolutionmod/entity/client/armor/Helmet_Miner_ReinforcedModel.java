package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Helmet_Miner_ReinforcedModel extends AnimatedGeoModel<IllagiumArmorItem> {

    @Override
    public ResourceLocation getModelResource(IllagiumArmorItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/helmet_miner.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IllagiumArmorItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/miner_helmet/"+object.getMaterial().getName()+"_minerhelmet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IllagiumArmorItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/leather_minerhelmet.animation.json");
    }
}
