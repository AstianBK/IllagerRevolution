package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FallenKnightModel<I extends Monster> extends AnimatedGeoModel<FallenKnightEntity> {

    private static final ResourceLocation MODEL=new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/fallen_knight.geo.json");

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/fallen_knight.animation.json");
    @Override
    public ResourceLocation getModelResource(FallenKnightEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FallenKnightEntity object) {
        if(object.getIsFrozen()){
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/fallen_knight/frozen_fallen_knight.png");
        }else {
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/fallen_knight/fallen_knight.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(FallenKnightEntity animatable) {
        return ANIMATION_RESLOC;
    }
}
