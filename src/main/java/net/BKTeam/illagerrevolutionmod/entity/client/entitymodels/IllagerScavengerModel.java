package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class IllagerScavengerModel extends AnimatedGeoModel<IllagerScavengerEntity> {
    @Override
    public ResourceLocation getModelLocation(IllagerScavengerEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/illagerminerbadlands.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(IllagerScavengerEntity object) {
        if(object.hasCustomName()){
            return object.getCustomName().getString().equals("Swat Scavenger") ? new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/illagerminerbadlands/swat_scavenger.png")  : new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer.png") ;
        }return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(IllagerScavengerEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illagerminerbadlands.animation.json");
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(IllagerScavengerEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
