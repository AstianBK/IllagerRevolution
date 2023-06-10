package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class MaulerModel extends AnimatedGeoModel<MaulerEntity> {

    private static final Map<MaulerEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(MaulerEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(MaulerEntity.Variant.VARIANT1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/mauler/mauler1.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/mauler/mauler2.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/mauler/mauler3.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/mauler/mauler4.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/mauler/mauler5.png"));
    });

    @Override
    public ResourceLocation getModelResource(MaulerEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/mauler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MaulerEntity object) {
        return LOCATION_BY_VARIANT.get(object.getIdVariant());
    }
    @Override
    public ResourceLocation getAnimationResource(MaulerEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/mauler.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(MaulerEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");
        IBone jaw = this.getAnimationProcessor().getBone("Jaw");
        int i=entity.attackTimer;

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null && !entity.isSitting()) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
        if(i>0){
            float f7 = Mth.sin(((float)(10 - i) - customPredicate.getPartialTick()) / 20.0F * (float)Math.PI * 0.25F);
            jaw.setRotationZ(((float)Math.PI / 2F) * f7);
        }
    }
}