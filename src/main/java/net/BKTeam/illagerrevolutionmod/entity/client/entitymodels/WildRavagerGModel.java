package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.AnimationVanillaG;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class WildRavagerGModel<T extends  WildRavagerEntity> extends GeoModel<T> {
    private static final Map<IllagerBeastEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(IllagerBeastEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager1.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager2.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager3.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager4.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager5.png"));
    });

    private static final ResourceLocation TEXTURE_UNDEAD = new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/zombie_wild_ravager.png");

    @Override
    public ResourceLocation getModelResource(T object) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "geo/wild_ravager.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        if(object.isUndead()){
            return TEXTURE_UNDEAD;
        }else {
            return LOCATION_BY_VARIANT.get(object.getIdVariant());
        }
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "animations/wild_ravager.animation.json");
    }

    @Override
    public void setCustomAnimations(T pEntity, long instanceId, AnimationState<T> customPredicate) {
        GeoBone main = (GeoBone) this.getAnimationProcessor().getBone("main");
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        GeoBone neck = (GeoBone) this.getAnimationProcessor().getBone("neck");
        CoreGeoBone mouth = this.getBone("mouth").get();
        CoreGeoBone body = this.getBone("body").get();
        CoreGeoBone rightHindLeg = this.getAnimationProcessor().getBone("leg0");
        CoreGeoBone leftHindLeg = this.getAnimationProcessor().getBone("leg1");
        CoreGeoBone rightFrontLeg = this.getAnimationProcessor().getBone("leg2");
        CoreGeoBone leftFrontLeg = this.getAnimationProcessor().getBone("leg3");
        super.setCustomAnimations(pEntity, instanceId, customPredicate);
        int i = pEntity.getStunnedTick();
        int j = pEntity.getRoarTick();
        int l = pEntity.getAttackTick();
        float pPartialTick = customPredicate.getPartialTick();
        EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        if(!pEntity.isCharged() && !pEntity.isSitting()) {
            AnimationVanillaG.resetMain(main);
            body.setRotX(-(float)Math.PI/2.0F);
            if (l > 0) {
                float f = Mth.triangleWave((float)l - pPartialTick, 10.0F);
                float f1 = (1.0F + f) * 0.5F;
                float f2 = f1 * f1 * f1 * 12.0F;
                float f3 = f2 * Mth.sin(neck.getRotX());
                neck.setPosZ(-6.5F+f2);
                neck.setPosY(f3);
                if (l > 5) {
                    mouth.setRotX(-Mth.sin(((float)(-4 + l) - pPartialTick) / 4.0F) * (float)Math.PI * 0.4F);
                } else {
                    mouth.setRotX(0.15707964F * -Mth.sin((float)Math.PI * ((float)l - pPartialTick) / 10.0F));
                }
            } else {
                float f6 = -1.0F * Mth.sin(neck.getRotX());
                boolean flag = i > 0;
                neck.setPosY( flag ? f6 -3.0F : f6 );
                neck.setRotX(flag ? -0.21991149F : 0.0F);
                mouth.setRotX((float)Math.PI * (flag ? -0.05F : -0.01F));
                if (flag) {
                    double d0 = (double)i / 40.0D;
                    neck.setPosX((float)Math.sin(d0 * 10.0D) * 3.0F);
                } else if (j > 0) {
                    float f7 = Mth.sin(((float)(20 - j) - pPartialTick) / 20.0F * (float)Math.PI * 0.25F);
                    mouth.setRotX(((float)Math.PI / 2F) * -f7);
                }
            }

            if (head != null && !pEntity.isSitting()) {
                head.setRotX(extraData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(extraData.netHeadYaw() * Mth.DEG_TO_RAD);
            }

            float f = 0.4F * customPredicate.getLimbSwingAmount();
            float pLimbSwing = customPredicate.getLimbSwing();
            rightHindLeg.setRotX(Mth.cos(pLimbSwing * 0.6662F) * f);
            leftHindLeg.setRotX(Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * f );
            rightFrontLeg.setRotX(Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * f );
            leftFrontLeg.setRotX( Mth.cos(pLimbSwing * 0.6662F) * f);
        }
    }

}
