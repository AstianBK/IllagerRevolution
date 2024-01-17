package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class RakerModel extends GeoModel<RakerEntity> {
    private static final Map<IllagerBeastEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(IllagerBeastEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker1.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker2.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker3.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker4.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker5.png"));
    });

    private static final ResourceLocation TEXTURE_UNDEAD = new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/zombie_raker.png");

    @Override
    public ResourceLocation getModelResource(RakerEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/raker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RakerEntity object) {
        if(object.isUndead()){
            return TEXTURE_UNDEAD;
        }else {
            return object.isScrapper() ? new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker_scrapper.png") : LOCATION_BY_VARIANT.get(object.getIdVariant());
        }
    }
    @Override
    public ResourceLocation getAnimationResource(RakerEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/raker.animation.json");
    }

    public void setCustomAnimations(RakerEntity entity, int instanceId, AnimationState customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        if (head != null && !entity.isSitting()) {
            head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(extraData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}