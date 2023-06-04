package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class RakerModel extends AnimatedGeoModel<RakerEntity> {
    ResourceLocation TEXTURE_REGULAR = IllagerRevolutionMod.rl("textures/entity/raker/raker.png");

        @Override
        public ResourceLocation getModelResource(RakerEntity object) {
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/raker.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(RakerEntity object) {
                return TEXTURE_REGULAR;
        }

        @Override
        public ResourceLocation getAnimationResource(RakerEntity animatable) {
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/raker.animation.json");
        }@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(RakerEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null && !entity.isSitting()) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}