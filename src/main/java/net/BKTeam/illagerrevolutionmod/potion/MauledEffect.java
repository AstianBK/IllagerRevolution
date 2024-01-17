package net.BKTeam.illagerrevolutionmod.potion;

import net.BKTeam.illagerrevolutionmod.api.IMauledCapability;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class MauledEffect extends MobEffect {

    public MauledEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public String getDescriptionId() {
        return "effect.illagerrevolutionmod.mauled";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        IMauledCapability capability= CapabilityHandler.getEntityCapability(pLivingEntity,CapabilityHandler.MAULED_CAPABILITY);
        if(capability!=null){
            capability.removeAttributeAmor(pLivingEntity,pLivingEntity.getEffect(this));
        }
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }
}
