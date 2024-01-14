package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.AnimationVanillaG;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class WildRavagerGModel extends AnimatedGeoModel<WildRavagerEntity> {

    private static final Map<IllagerBeastEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(IllagerBeastEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager1.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager2.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager3.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager4.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager5.png"));
    });

    private static final ResourceLocation TEXTURE_UNDEAD = new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/zombie_wild_ravager.png");

    @Override
    public ResourceLocation getModelLocation(WildRavagerEntity object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/wild_ravager.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(WildRavagerEntity object) {
        if(object.isUndead()){
            return TEXTURE_UNDEAD;
        }else {
            return LOCATION_BY_VARIANT.get(object.getIdVariant());
        }
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WildRavagerEntity animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/wild_ravager.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCustomAnimations(WildRavagerEntity pEntity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(pEntity, instanceId,customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone neck = this.getAnimationProcessor().getBone("neck");
        IBone mouth = this.getBone("mouth");
        IBone body = this.getBone("body");
        IBone rightHindLeg = this.getAnimationProcessor().getBone("leg0");
        IBone leftHindLeg = this.getAnimationProcessor().getBone("leg1");
        IBone rightFrontLeg = this.getAnimationProcessor().getBone("leg2");
        IBone leftFrontLeg = this.getAnimationProcessor().getBone("leg3");
        int i = pEntity.getStunnedTick();
        int j = pEntity.getRoarTick();
        int l = pEntity.getAttackTick();
        float pPartialTick = customPredicate.getPartialTick();
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        if(!pEntity.isCharged() && !pEntity.isSitting()) {
            body.setPositionY(0.0F);
            body.setRotationX(-(float)Math.PI/2.0F);
            body.setScaleX(1.0f);
            body.setScaleY(1.0f);
            body.setScaleZ(1.0f);
            AnimationVanillaG.setPositionBone(leftFrontLeg,0.0F,0.0F,0.0F);
            AnimationVanillaG.setRotationBone(leftFrontLeg,0.0F,0.0F,0.0F);

            AnimationVanillaG.setPositionBone(rightFrontLeg,0.0F,0.0F,0.0F);
            AnimationVanillaG.setRotationBone(rightFrontLeg,0.0F,0.0F,0.0F);

            AnimationVanillaG.setPositionBone(leftHindLeg,0.0F,0.0F,0.0F);
            AnimationVanillaG.setRotationBone(leftHindLeg,0.0F,0.0F,0.0F);

            AnimationVanillaG.setPositionBone(rightHindLeg,0.0F,0.0F,0.0F);
            AnimationVanillaG.setRotationBone(rightHindLeg,0.0F,0.0F,0.0F);

            if (l > 0) {
                float f = Mth.triangleWave((float)l - pPartialTick, 10.0F);
                float f1 = (1.0F + f) * 0.5F;
                float f2 = f1 * f1 * f1 * 12.0F;
                float f3 = f2 * Mth.sin(neck.getRotationX());
                neck.setPositionZ(-6.5F+f2);
                neck.setPositionY(f3);
                if (l > 5) {
                    mouth.setRotationX(-Mth.sin(((float)(-4 + l) - pPartialTick) / 4.0F) * (float)Math.PI * 0.4F);
                } else {
                    mouth.setRotationX(0.15707964F * -Mth.sin((float)Math.PI * ((float)l - pPartialTick) / 10.0F));
                }
            } else {
                float f6 = -1.0F * Mth.sin(neck.getRotationX());
                boolean flag = i > 0;
                neck.setPositionY( flag ? f6 -3.0F : f6 );
                neck.setRotationX(flag ? -0.21991149F : 0.0F);
                mouth.setRotationX((float)Math.PI * (flag ? -0.05F : -0.01F));
                if (flag) {
                    double d0 = (double)i / 40.0D;
                    neck.setPositionX((float)Math.sin(d0 * 10.0D) * 3.0F);
                } else if (j > 0) {
                    float f7 = Mth.sin(((float)(20 - j) - pPartialTick) / 20.0F * (float)Math.PI * 0.25F);
                    mouth.setRotationX(((float)Math.PI / 2F) * -f7);
                }
            }

            if (head != null && !pEntity.isSitting()) {
                head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
                head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
            }

            float f = 0.4F * customPredicate.getLimbSwingAmount();
            float pLimbSwing = customPredicate.getLimbSwing();
            rightHindLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F) * f);
            leftHindLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * f );
            rightFrontLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * f );
            leftFrontLeg.setRotationX( Mth.cos(pLimbSwing * 0.6662F) * f);
        }
    }
}
