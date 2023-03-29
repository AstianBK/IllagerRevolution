package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class Helmet_Miner_ReinforcedModel extends AnimatedGeoModel<IllagiumArmorItem> {

    @Override
    public ResourceLocation getModelLocation(IllagiumArmorItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/helmet_miner.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(IllagiumArmorItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/"+object.getMaterial().getName()+"_minerhelmet.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(IllagiumArmorItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/leather_minerhelmet.animation.json");
    }
}
