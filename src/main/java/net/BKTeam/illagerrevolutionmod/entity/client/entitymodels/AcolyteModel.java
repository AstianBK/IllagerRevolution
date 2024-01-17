package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.AnimationVanillaG;
import net.BKTeam.illagerrevolutionmod.entity.custom.AcolyteEntity;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.AbstractIllager;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class AcolyteModel<I extends AbstractIllager> extends GeoModel<AcolyteEntity> {

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

    public void setCustomAnimations(AcolyteEntity animatable, int instanceId, AnimationState animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        EntityModelData extraData = (EntityModelData) animationEvent.getData(DataTickets.ENTITY_MODEL_DATA);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
        CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
        CoreGeoBone rightLeg = this.getAnimationProcessor().getBone("right_leg");
        CoreGeoBone leftLeg = this.getAnimationProcessor().getBone("left_leg");
        CoreGeoBone cape = this.getAnimationProcessor().getBone("cape");
        float pLimbSwing = animationEvent.getLimbSwing();
        float pLimbSwingAmount = animationEvent.getLimbSwingAmount();
        float pAgeInTicks = animatable.tickCount;
        float pNetHeadYaw = extraData.netHeadYaw();
        float pHeadPitch = extraData.headPitch();
        float i = animatable.getAttackAnim(animationEvent.getPartialTick());
        float f = Mth.cos(pLimbSwing * 0.261799F) * pLimbSwingAmount * 0.5F;
        float f2 = -f;

        if(f2>0.0F){
            f2-=f-f*2F;
        }

        head.setRotY(pNetHeadYaw * ((float)Math.PI / 180F));
        head.setRotX(pHeadPitch * ((float)Math.PI / 180F));

        AnimationVanillaG.setRotBone(rightArm,Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotBone(leftArm,Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotBone(rightLeg,Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotBone(leftLeg,Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotBone(cape,f2,0.0F,0.0F);

        AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = animatable.getArmPose();
        if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING) {
            if (animatable.getMainHandItem().isEmpty()) {
                AnimationVanillaG.animateZombieArms(leftArm, rightArm, true, i, pAgeInTicks);
            } else {
                AnimationVanillaG.swingWeaponDown(rightArm, leftArm, animatable, i, pAgeInTicks);
            }
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            rightArm.setPosZ(0.0F);
            rightArm.setPosX(-1.0F);
            leftArm.setPosZ(0.0F);
            leftArm.setPosX(1.0F);
            rightArm.setRotX(Mth.cos(pAgeInTicks * 0.6662F) * 0.25F);
            leftArm.setRotX(Mth.cos(pAgeInTicks * 0.6662F) * 0.25F);
            rightArm.setRotZ(2.3561945F);
            leftArm.setRotZ(-2.3561945F);
            rightArm.setRotY(0.0F);
            leftArm.setRotY(0.0F);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            rightArm.setRotY(-0.1F + head.getRotY());
            rightArm.setRotX((-(float)Math.PI / 2F) + head.getRotX());
            leftArm.setRotX(-0.9424779F + head.getRotX());
            leftArm.setRotY(head.getRotY() - 0.4F);
            leftArm.setRotZ(((float)Math.PI / 2F));
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
            AnimationVanillaG.animateCrossbowHold(rightArm, leftArm, head, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
            AnimationVanillaG.animateCrossbowCharge(rightArm, leftArm, animatable, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
            rightArm.setPosZ(0.0F);
            rightArm.setPosX(-5.0F);
            rightArm.setRotX(Mth.cos(pAgeInTicks * 0.6662F) * 0.05F);
            rightArm.setRotZ(2.670354F);
            rightArm.setRotY(0.0F);
            leftArm.setPosZ(0.0F);
            leftArm.setPosX(5.0F);
            leftArm.setRotX( Mth.cos(pAgeInTicks * 0.6662F) * 0.05F);
            leftArm.setRotY(-2.3561945F);
            leftArm.setRotZ(0.0F);
        }

    }

    @Override
    public ResourceLocation getAnimationResource(AcolyteEntity animatable) {
        return null;
    }
}
