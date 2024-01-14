package net.BKTeam.illagerrevolutionmod.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMauledCapability extends INBTSerializable<CompoundTag> {
    void setArmorNatural(double pArmorNatural);
    double getArmorNatural();
    void updateAttributeArmor(LivingEntity entity, MobEffectInstance instance);
    void onTick(LivingEntity entity, MobEffectInstance instance);
    boolean hasChanged();
    void removeAttributeAmor(LivingEntity living,MobEffectInstance effect);
    void setArmorTotal(int initialArmor);
    int getArmorTotal();
}