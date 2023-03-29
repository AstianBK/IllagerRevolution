package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class IllagerMinerModel<I extends AbstractIllager> extends AnimatedGeoModel<IllagerMinerEntity> {

    protected final ResourceLocation MODEL_RESLOC;
    protected final ResourceLocation TEXTURE_DEFAULT;
    protected final String ENTITY_REGISTRY_PATH_NAME;

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/illagerminer.animation.json");

    public IllagerMinerModel(ResourceLocation model, ResourceLocation textureDefault,
                             String entityName) {
        super();
        this.MODEL_RESLOC = model;
        this.TEXTURE_DEFAULT = textureDefault;
        this.ENTITY_REGISTRY_PATH_NAME = entityName;
    }

    @Override
    public ResourceLocation getModelLocation(IllagerMinerEntity object) {
        return MODEL_RESLOC;
    }

    @Override
    public ResourceLocation getTextureLocation(IllagerMinerEntity object) {
        return TEXTURE_DEFAULT;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(IllagerMinerEntity animatable) {
        return ANIMATION_RESLOC;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(IllagerMinerEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
