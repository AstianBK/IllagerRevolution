package net.BKTeam.illagerrevolutionmod.entity.client;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class AnimationVanillaG {
    public static void resetMain(GeoBone main){
        for(GeoBone child:main.childBones){
            BoneSnapshot initial=child.getInitialSnapshot();
            child.setRotation(initial.rotationValueX, initial.rotationValueY, initial.rotationValueZ);
            child.setPosition(initial.positionOffsetX, initial.positionOffsetY, initial.positionOffsetZ);
            if(!child.childBones.isEmpty()){
                resetMain(child);
            }
        }
    }
    public static void setPositionBone(IBone bone, float x, float y, float z){
        bone.setPositionX(x);
        bone.setPositionY(y);
        bone.setPositionZ(z);
    }

    public static void setRotationBone(IBone bone, float x, float y, float z){
        bone.setRotationX(x);
        bone.setRotationY(y);
        bone.setRotationZ(z);
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
