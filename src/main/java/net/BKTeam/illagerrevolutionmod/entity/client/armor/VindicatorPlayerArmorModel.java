package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorVindicatorJacketItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VindicatorPlayerArmorModel extends AnimatedGeoModel<ArmorVindicatorJacketItem> {

    @Override
    public ResourceLocation getModelResource(ArmorVindicatorJacketItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/vindicator_player_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArmorVindicatorJacketItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/vindicator_armor/vindicator_jacket_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArmorVindicatorJacketItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illager_player_armor.animation.json");
    }

}
