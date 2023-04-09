package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorPillagerVestItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PillagerPlayerArmorModel extends AnimatedGeoModel<ArmorPillagerVestItem> {

    @Override
    public ResourceLocation getModelLocation(ArmorPillagerVestItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/vindicator_player_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorPillagerVestItem object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/models/armor/pillager_armor/pillager_vest_armor.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ArmorPillagerVestItem animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illager_player_armor.animation.json");
    }

}
