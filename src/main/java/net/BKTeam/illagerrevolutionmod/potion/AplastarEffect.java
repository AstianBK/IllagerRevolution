package net.BKTeam.illagerrevolutionmod.potion;

import net.BKTeam.illagerrevolutionmod.api.IAplastarCapability;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.BKTeam.illagerrevolutionmod.procedures.Events;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AplastarEffect extends MobEffect {

    public AplastarEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public String getDescriptionId() {
        return "effect.illagerrevolutionmod.death_mark";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(!pLivingEntity.level.isClientSide){
            IAplastarCapability capability= CapabilityHandler.getEntityCapability(pLivingEntity,CapabilityHandler.APLASTAR_CAPABILITY);
            if(capability!=null){
                capability.onTick(pLivingEntity,pLivingEntity.getEffect(this));
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if(!pLivingEntity.level.isClientSide){
            IAplastarCapability capability= CapabilityHandler.getEntityCapability(pLivingEntity,CapabilityHandler.APLASTAR_CAPABILITY);
            if(capability!=null){
                capability.removeAttributeAmor(pLivingEntity,pLivingEntity.getEffect(this));
            }
        }
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }
}
