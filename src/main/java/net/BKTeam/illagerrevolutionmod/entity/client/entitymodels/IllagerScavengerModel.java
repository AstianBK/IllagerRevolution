package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class IllagerScavengerModel extends GeoModel<IllagerScavengerEntity> {
    @Override
    public ResourceLocation getModelResource(IllagerScavengerEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/illagerminerbadlands.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IllagerScavengerEntity object) {
        if(object.hasCustomName()){
            return object.getCustomName().getString().equals("Swat Scavenger") ? new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/illagerminerbadlands/swat_scavenger.png")  : new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer.png") ;
        }return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IllagerScavengerEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/illagerminerbadlands.animation.json");
    }

    public void setCustomAnimations(IllagerScavengerEntity entity, int instanceId, AnimationState customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        if (head != null) {
            head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(extraData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
