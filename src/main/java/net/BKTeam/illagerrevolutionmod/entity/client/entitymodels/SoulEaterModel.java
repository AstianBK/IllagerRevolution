package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.SoulEaterEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import software.bernie.geckolib.model.GeoModel;

public class SoulEaterModel<I extends AbstractIllager> extends GeoModel<SoulEaterEntity> {

    protected final ResourceLocation MODEL_RESLOC;
    protected final ResourceLocation TEXTURE_DEFAULT;
    protected final ResourceLocation TEXTURE_HURT;
    protected final String ENTITY_REGISTRY_PATH_NAME;

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/soul_eater.animation.json");

    public SoulEaterModel(ResourceLocation model, ResourceLocation textureDefault, ResourceLocation textureHurt, String entityName) {
        super();
        this.MODEL_RESLOC = model;
        this.TEXTURE_HURT=textureHurt;
        this.TEXTURE_DEFAULT =textureDefault;
        this.ENTITY_REGISTRY_PATH_NAME = entityName;
    }

    @Override
    public ResourceLocation getModelResource(SoulEaterEntity object) {
        return MODEL_RESLOC;
    }

    @Override
    public ResourceLocation getTextureResource(SoulEaterEntity object) {
        return object.isCharging() ? TEXTURE_HURT : TEXTURE_DEFAULT ;
    }

    @Override
    public ResourceLocation getAnimationResource(SoulEaterEntity animatable) {
        return ANIMATION_RESLOC;
    }
}
