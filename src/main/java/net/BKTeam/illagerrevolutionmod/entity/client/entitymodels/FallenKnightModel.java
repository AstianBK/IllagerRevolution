package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FallenKnightModel<I extends Monster> extends AnimatedGeoModel<FallenKnight> {

    private static final ResourceLocation MODEL=new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/fallen_knight.geo.json");

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/fallen_knight.animation.json");
    @Override
    public ResourceLocation getModelLocation(FallenKnight object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(FallenKnight object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                "textures/entity/fallen_knight/fallen_knight.png");

    }

    @Override
    public ResourceLocation getAnimationFileLocation(FallenKnight animatable) {
        return ANIMATION_RESLOC;
    }
}
