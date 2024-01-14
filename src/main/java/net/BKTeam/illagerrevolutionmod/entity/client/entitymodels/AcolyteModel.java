package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.AnimationVanillaG;
import net.BKTeam.illagerrevolutionmod.entity.custom.AcolyteEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.AcolyteEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import net.minecraft.Util;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
    public ResourceLocation getModelLocation(AcolyteEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(AcolyteEntity object) {
        return LOCATION_BY_VARIANT.get(object.getProfession());
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
        IBone cape = this.getAnimationProcessor().getBone("cape");
        float pLimbSwing = animationEvent.getLimbSwing();
        float pLimbSwingAmount = animationEvent.getLimbSwingAmount();
        float pAgeInTicks = animatable.tickCount;
        float pNetHeadYaw = extraData.netHeadYaw;
        float pHeadPitch = extraData.headPitch;
        float i = animatable.getAttackAnim(animationEvent.getPartialTick());
        float f = Mth.cos(pLimbSwing * 0.261799F) * pLimbSwingAmount * 0.5F;
        float f2 = -f;

        if(f2>0.0F){
            f2-=f-f*2F;
        }

        head.setRotationY(pNetHeadYaw * ((float)Math.PI / 180F));
        head.setRotationX(pHeadPitch * ((float)Math.PI / 180F));

        AnimationVanillaG.setRotationBone(rightArm,Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotationBone(leftArm,Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotationBone(rightLeg,Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotationBone(leftLeg,Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount * 0.5F,0.0F,0.0F);
        AnimationVanillaG.setRotationBone(cape,f2,0.0F,0.0F);

        AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = animatable.getArmPose();
        if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING) {
            if (animatable.getMainHandItem().isEmpty()) {
                AnimationVanillaG.animateZombieArms(leftArm, rightArm, true, i, pAgeInTicks);
            } else {
                AnimationVanillaG.swingWeaponDown(rightArm, leftArm, animatable, i, pAgeInTicks);
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
            AnimationVanillaG.animateCrossbowHold(rightArm, leftArm, head, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
            AnimationVanillaG.animateCrossbowCharge(rightArm, leftArm, animatable, true);
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

    @Override
    public ResourceLocation getAnimationFileLocation(AcolyteEntity animatable) {
        return null;
    }
}
