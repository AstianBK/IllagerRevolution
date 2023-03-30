package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ZombifiedModel<I extends Monster> extends AnimatedGeoModel<ZombifiedEntity> {

    private static final ResourceLocation MODEL=new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/zombified_illager.geo.json");

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/zombified_entity.animation.json");
    @Override
    public ResourceLocation getModelLocation(ZombifiedEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(ZombifiedEntity object) {
        if(object.isHasSoul()){
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/zombified/zombified_"+object.getnameSoul()+".png");
        }
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                "textures/entity/zombified/zombified_pillager.png");

    }

    @Override
    public ResourceLocation getAnimationFileLocation(ZombifiedEntity animatable) {
        return ANIMATION_RESLOC;
    }
}
