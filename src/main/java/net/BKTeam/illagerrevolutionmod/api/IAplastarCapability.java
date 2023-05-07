package net.BKTeam.illagerrevolutionmod.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAplastarCapability extends INBTSerializable<CompoundTag> {
    void setOldArmorTotal(int pArmorTotal);
    int getOldArmorTotal();
    void updateAttributeArmor(LivingEntity entity, MobEffectInstance instance);
    void onTick(LivingEntity entity, MobEffectInstance instance);
    boolean hasChanged(int armor);
    void removeAttributeAmor(LivingEntity living,MobEffectInstance effect);
    void setInitialArmor(int initialArmor);
    int getInitialArmor();
}
