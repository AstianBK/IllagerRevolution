package net.BKTeam.illagerrevolutionmod.item.client;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.AnimatedItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AnimatedItemModel extends AnimatedGeoModel<AnimatedItem> {

    @Override
    public ResourceLocation getModelResource(AnimatedItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/ominous_grimoire.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AnimatedItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/item/ominous_grimoire_"+object.getSouls()+".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AnimatedItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/ominous_grimoire.animation.json");
    }
}
