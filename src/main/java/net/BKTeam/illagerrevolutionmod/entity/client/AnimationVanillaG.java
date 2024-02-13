package net.BKTeam.illagerrevolutionmod.entity.client;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.state.BoneSnapshot;

public class AnimationVanillaG {

    public static void resetMain(GeoBone main){
        if(main!=null && !main.getChildBones().isEmpty()){
            for(GeoBone child:main.getChildBones()){
                BoneSnapshot initial=child.getInitialSnapshot();
                setRotBone(child,initial.getRotX(), initial.getRotY(), initial.getRotZ());
                setPositionBone(child, initial.getOffsetX(), initial.getOffsetY(), initial.getOffsetZ());
                if(!child.getChildBones().isEmpty()){
                    resetMain(child);
                }
            }
        }
    }
    public static void setPositionBone(CoreGeoBone bone, float x, float y, float z){
        bone.setPosX(x);
        bone.setPosY(y);
        bone.setPosZ(z);
    }

    public static void setRotBone(CoreGeoBone bone, float x, float y, float z){
        bone.setRotX(x);
        bone.setRotY(y);
        bone.setRotZ(z);
    }
    public static void animateCrossbowHold(CoreGeoBone pRightArm, CoreGeoBone pLeftArm, CoreGeoBone pHead, boolean pRightHanded) {
        CoreGeoBone modelpart = pRightHanded ? pRightArm : pLeftArm;
        CoreGeoBone modelpart1 = pRightHanded ? pLeftArm : pRightArm;
        pRightArm.setRotY((pRightHanded ? 0.3F : -0.3F) - pHead.getRotY());
        pLeftArm.setRotY((pRightHanded ? -0.6F : 0.6F) - pHead.getRotY());
        pRightArm.setRotX(((float)Math.PI / 2F) - pHead.getRotX() - 0.1F);
        pLeftArm.setRotX(1.5F - pHead.getRotX());
    }

    public static void animateCrossbowCharge(CoreGeoBone pRightArm, CoreGeoBone pLeftArm, LivingEntity pLivingEntity, boolean pRightHanded) {
        CoreGeoBone modelpart = pRightHanded ? pRightArm : pLeftArm;
        CoreGeoBone modelpart1 = pRightHanded ? pLeftArm : pRightArm;
        pRightArm.setRotZ(pRightHanded ? -0.8F : 0.8F);
        pRightArm.setRotX(0.97079635F);
        pLeftArm.setRotX(pRightArm.getRotX());
        float f = (float) CrossbowItem.getChargeDuration(pLivingEntity.getUseItem());
        float f1 = Mth.clamp((float)pLivingEntity.getTicksUsingItem(), 0.0F, f);
        float f2 = f1 / f;
        pLeftArm.setRotY(Mth.lerp(f2, -0.4F, -0.85F) * (float)(pRightHanded ? 1 : -1));
        pLeftArm.setRotX(Mth.lerp(f2, pLeftArm.getRotX(), ((float)Math.PI / 2F)));
    }

    public static <T extends Mob> void swingWeaponDown(CoreGeoBone pRightArm, CoreGeoBone pLeftArm, T pMob, float pAttackTime, float pAgeInTicks) {
        float f = Mth.sin(pAttackTime * (float)Math.PI);
        float f1 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * (float)Math.PI);
        pRightArm.setRotZ(0.0F);
        pLeftArm.setRotZ(0.0F);
        pRightArm.setRotY(0.15707964F);
        pLeftArm.setRotY(-0.15707964F);
        if (pMob.getMainArm() == HumanoidArm.RIGHT) {
            pRightArm.setRotX(1.8849558F - Mth.cos(pAgeInTicks * 0.09F) * 0.15F);
            pLeftArm.setRotX(0.0F - Mth.cos(pAgeInTicks * 0.19F) * 0.5F);
            pRightArm.setRotX(pRightArm.getRotX() - f * 2.2F - f1 * 0.4F);
            pLeftArm.setRotX(pLeftArm.getRotX() - f * 1.2F - f1 * 0.4F) ;
        } else {
            pRightArm.setRotX(-0.0F + Mth.cos(pAgeInTicks * 0.19F) * 0.5F) ;
            pLeftArm.setRotX(-1.0472F + Mth.cos(pAgeInTicks * 0.09F) * 0.15F);
            pRightArm.setRotX(pRightArm.getRotX()-f * 1.2F - f1 * 0.4F) ;
            pLeftArm.setRotX(pLeftArm.getRotX()-f * 2.2F - f1 * 0.4F) ;
        }

        bobArms(pRightArm, pLeftArm, pAgeInTicks);
    }

    public static void bobModelPart(CoreGeoBone pModelPart, float pAgeInTicks, float pMultiplier) {
        pModelPart.setRotZ(pModelPart.getRotZ() + pMultiplier * (Mth.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F));
        pModelPart.setRotX(pModelPart.getRotX() + pMultiplier * Mth.sin(pAgeInTicks * 0.067F) * 0.05F);
    }

    public static void bobArms(CoreGeoBone pRightArm, CoreGeoBone pLeftArm, float pAgeInTicks) {
        bobModelPart(pRightArm, pAgeInTicks, 1.0F);
        bobModelPart(pLeftArm, pAgeInTicks, -1.0F);
    }

    public static void animateZombieArms(CoreGeoBone pLeftArm, CoreGeoBone pRightArm, boolean pIsAggressive, float pAttackTime, float pAgeInTicks) {
        float f = Mth.sin(pAttackTime * (float)Math.PI);
        float f1 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * (float)Math.PI);
        pRightArm.setRotZ(0.0F);
        pLeftArm.setRotZ(0.0F);
        pRightArm.setRotY(-(0.1F - f * 0.6F));
        pLeftArm.setRotY(0.1F - f * 0.6F);
        float f2 = -(float)Math.PI / (pIsAggressive ? 1.5F : 2.25F);
        pRightArm.setRotX(f2);
        pLeftArm.setRotX(f2);
        pRightArm.setRotX(pRightArm.getRotX()+f * 1.2F - f1 * 0.4F);
        pLeftArm.setRotX(pLeftArm.getRotX()+f * 1.2F - f1 * 0.4F);
        bobArms(pRightArm, pLeftArm, pAgeInTicks);
    }
}
