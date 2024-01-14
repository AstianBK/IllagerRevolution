package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class ScroungerModel extends AnimatedGeoModel<ScroungerEntity> {

    private static final Map<MaulerEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(MaulerEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(MaulerEntity.Variant.VARIANT1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/scrounger/scrounger1.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/scrounger/scrounger2.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/scrounger/scrounger3.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/scrounger/scrounger4.png"));
        p_114874_.put(MaulerEntity.Variant.VARIANT5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/scrounger/scrounger5.png"));
    });

    private static final ResourceLocation TEXTURE_UNDEAD = new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/scrounger/zombie_scrounger.png");
    @Override
    public ResourceLocation getModelLocation(ScroungerEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/scrounger.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ScroungerEntity object) {
        return object.isUndead() ? TEXTURE_UNDEAD : LOCATION_BY_VARIANT.get(object.getIdVariant());
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ScroungerEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/scrounger.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(ScroungerEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null && !entity.isSitting()) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}