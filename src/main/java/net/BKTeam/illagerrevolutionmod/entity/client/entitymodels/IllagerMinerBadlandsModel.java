package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerBadlandsEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class IllagerMinerBadlandsModel extends AnimatedGeoModel<IllagerMinerBadlandsEntity> {
    @Override
    public ResourceLocation getModelLocation(IllagerMinerBadlandsEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/illagerminerbadlands.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(IllagerMinerBadlandsEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(IllagerMinerBadlandsEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illagerminerbadlands.animation.json");
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(IllagerMinerBadlandsEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
