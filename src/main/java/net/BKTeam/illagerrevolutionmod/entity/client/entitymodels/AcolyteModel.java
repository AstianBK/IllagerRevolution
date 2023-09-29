package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.AcolyteEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.AcolyteEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import net.minecraft.Util;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CrossbowItem;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class AcolyteModel<I extends AbstractIllager> extends AnimatedGeoModel<AcolyteEntity> {

    private static final Map<AcolyteEntity.ProfessionTier, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(AcolyteEntity.ProfessionTier.class), (p_114874_) -> {
        p_114874_.put(AcolyteEntity.ProfessionTier.NONE, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/acolyte/acolyte.png"));
        p_114874_.put(AcolyteEntity.ProfessionTier.FIGHTER, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/acolyte/acolyte.png"));
        p_114874_.put(AcolyteEntity.ProfessionTier.MAGE, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/acolyte/acolyte2.png"));
        p_114874_.put(AcolyteEntity.ProfessionTier.RANGED, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/acolyte/acolyte3.png"));
    });

    private static final ResourceLocation MODEL=new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/acolyte.geo.json");
    @Override
    public ResourceLocation getModelResource(AcolyteEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(AcolyteEntity object) {
        return LOCATION_BY_VARIANT.get(object.getProfession());
    }

    @Override
    public ResourceLocation getAnimationResource(AcolyteEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(AcolyteEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone rightArm = this.getAnimationProcessor().getBone("right_arm");
        IBone leftArm = this.getAnimationProcessor().getBone("left_arm");
        IBone rightLeg = this.getAnimationProcessor().getBone("right_leg");
        IBone leftLeg = this.getAnimationProcessor().getBone("left_leg");
        float pLimbSwing = animationEvent.getLimbSwing();
        float pLimbSwingAmount = animationEvent.getLimbSwingAmount();
        float pAgeInTicks = animatable.tickCount;
        float pNetHeadYaw = extraData.netHeadYaw;
        float pHeadPitch = extraData.headPitch;
        float i = animatable.getAttackAnim(animationEvent.getPartialTick());
        
        head.setRotationY(pNetHeadYaw * ((float)Math.PI / 180F));
        head.setRotationX(pHeadPitch * ((float)Math.PI / 180F));

        rightArm.setRotationX(Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F);
        rightArm.setRotationY(0.0F);
        rightArm.setRotationZ(0.0F);
        leftArm.setRotationX(Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F);
        leftArm.setRotationY(0.0F);
        leftArm.setRotationZ(0.0F);
        rightLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * 0.5F);
        rightLeg.setRotationY(0.0F);
        rightLeg.setRotationZ(0.0F);
        leftLeg.setRotationX(Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount * 0.5F);
        leftLeg.setRotationY(0.0F);
        leftLeg.setRotationZ(0.0F);

        AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = animatable.getArmPose();
        if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING) {
            if (animatable.getMainHandItem().isEmpty()) {
                animateZombieArms(leftArm, rightArm, true, i, pAgeInTicks);
            } else {
                swingWeaponDown(rightArm, leftArm, animatable, i, pAgeInTicks);
            }
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            rightArm.setPositionZ(0.0F);
            rightArm.setPositionX(-1.0F);
            leftArm.setPositionZ(0.0F);
            leftArm.setPositionX(1.0F);
            rightArm.setRotationX(Mth.cos(pAgeInTicks * 0.6662F) * 0.25F);
            leftArm.setRotationX(Mth.cos(pAgeInTicks * 0.6662F) * 0.25F);
            rightArm.setRotationZ(2.3561945F);
            leftArm.setRotationZ(-2.3561945F);
            rightArm.setRotationY(0.0F);
            leftArm.setRotationY(0.0F);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            rightArm.setRotationY(-0.1F + head.getRotationY());
            rightArm.setRotationX((-(float)Math.PI / 2F) + head.getRotationX());
            leftArm.setRotationX(-0.9424779F + head.getRotationX());
            leftArm.setRotationY(head.getRotationY() - 0.4F);
            leftArm.setRotationZ(((float)Math.PI / 2F));
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
            animateCrossbowHold(rightArm, leftArm, head, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
            animateCrossbowCharge(rightArm, leftArm, animatable, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
            rightArm.setPositionZ(0.0F);
            rightArm.setPositionX(-5.0F);
            rightArm.setRotationX(Mth.cos(pAgeInTicks * 0.6662F) * 0.05F);
            rightArm.setRotationZ(2.670354F);
            rightArm.setRotationY(0.0F);
            leftArm.setPositionZ(0.0F);
            leftArm.setPositionX(5.0F);
            leftArm.setRotationX( Mth.cos(pAgeInTicks * 0.6662F) * 0.05F);
            leftArm.setRotationY(-2.3561945F);
            leftArm.setRotationZ(0.0F);
        }

    }

    public static void animateCrossbowHold(IBone pRightArm, IBone pLeftArm, IBone pHead, boolean pRightHanded) {
        IBone modelpart = pRightHanded ? pRightArm : pLeftArm;
        IBone modelpart1 = pRightHanded ? pLeftArm : pRightArm;
        pRightArm.setRotationY((pRightHanded ? 0.3F : -0.3F) - pHead.getRotationY());
        pLeftArm.setRotationY((pRightHanded ? -0.6F : 0.6F) - pHead.getRotationY());
        pRightArm.setRotationX(((float)Math.PI / 2F) - pHead.getRotationX() - 0.1F);
        pLeftArm.setRotationX(1.5F - pHead.getRotationX());
    }

    public static void animateCrossbowCharge(IBone pRightArm, IBone pLeftArm, LivingEntity pLivingEntity, boolean pRightHanded) {
        IBone modelpart = pRightHanded ? pRightArm : pLeftArm;
        IBone modelpart1 = pRightHanded ? pLeftArm : pRightArm;
        pRightArm.setRotationZ(pRightHanded ? -0.8F : 0.8F);
        pRightArm.setRotationX(0.97079635F);
        pLeftArm.setRotationX(pRightArm.getRotationX());
        float f = (float) CrossbowItem.getChargeDuration(pLivingEntity.getUseItem());
        float f1 = Mth.clamp((float)pLivingEntity.getTicksUsingItem(), 0.0F, f);
        float f2 = f1 / f;
        pLeftArm.setRotationY(Mth.lerp(f2, -0.4F, -0.85F) * (float)(pRightHanded ? 1 : -1));
        pLeftArm.setRotationX(Mth.lerp(f2, pLeftArm.getRotationX(), ((float)Math.PI / 2F)));
    }

    public static <T extends Mob> void swingWeaponDown(IBone pRightArm, IBone pLeftArm, T pMob, float pAttackTime, float pAgeInTicks) {
        float f = Mth.sin(pAttackTime * (float)Math.PI);
        float f1 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * (float)Math.PI);
        pRightArm.setRotationZ(0.0F);
        pLeftArm.setRotationZ(0.0F);
        pRightArm.setRotationY(0.15707964F);
        pLeftArm.setRotationY(-0.15707964F);
        if (pMob.getMainArm() == HumanoidArm.RIGHT) {
            pRightArm.setRotationX(1.8849558F - Mth.cos(pAgeInTicks * 0.09F) * 0.15F);
            pLeftArm.setRotationX(0.0F - Mth.cos(pAgeInTicks * 0.19F) * 0.5F);
            pRightArm.setRotationX(pRightArm.getRotationX() - f * 2.2F - f1 * 0.4F);
            pLeftArm.setRotationX(pLeftArm.getRotationX() - f * 1.2F - f1 * 0.4F) ;
        } else {
            pRightArm.setRotationX(-0.0F + Mth.cos(pAgeInTicks * 0.19F) * 0.5F) ;
            pLeftArm.setRotationX(-1.0472F + Mth.cos(pAgeInTicks * 0.09F) * 0.15F);
            pRightArm.setRotationX(pRightArm.getRotationX()-f * 1.2F - f1 * 0.4F) ;
            pLeftArm.setRotationX(pLeftArm.getRotationX()-f * 2.2F - f1 * 0.4F) ;
        }

        bobArms(pRightArm, pLeftArm, pAgeInTicks);
    }

    public static void bobModelPart(IBone pModelPart, float pAgeInTicks, float pMultiplier) {
        pModelPart.setRotationZ(pModelPart.getRotationZ() + pMultiplier * (Mth.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F));
        pModelPart.setRotationX(pModelPart.getRotationX() + pMultiplier * Mth.sin(pAgeInTicks * 0.067F) * 0.05F);
    }

    public static void bobArms(IBone pRightArm, IBone pLeftArm, float pAgeInTicks) {
        bobModelPart(pRightArm, pAgeInTicks, 1.0F);
        bobModelPart(pLeftArm, pAgeInTicks, -1.0F);
    }

    public static void animateZombieArms(IBone pLeftArm, IBone pRightArm, boolean pIsAggressive, float pAttackTime, float pAgeInTicks) {
        float f = Mth.sin(pAttackTime * (float)Math.PI);
        float f1 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * (float)Math.PI);
        pRightArm.setRotationZ(0.0F);
        pLeftArm.setRotationZ(0.0F);
        pRightArm.setRotationY(-(0.1F - f * 0.6F));
        pLeftArm.setRotationY(0.1F - f * 0.6F);
        float f2 = -(float)Math.PI / (pIsAggressive ? 1.5F : 2.25F);
        pRightArm.setRotationX(f2);
        pLeftArm.setRotationX(f2);
        pRightArm.setRotationX(pRightArm.getRotationX()+f * 1.2F - f1 * 0.4F);
        pLeftArm.setRotationX(pLeftArm.getRotationX()+f * 1.2F - f1 * 0.4F);
        bobArms(pRightArm, pLeftArm, pAgeInTicks);
    }
}
