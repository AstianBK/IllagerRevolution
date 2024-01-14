package net.BKTeam.illagerrevolutionmod.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface IAbilityKnightCapability extends INBTSerializable<CompoundTag> {
    void setShieldSoul(boolean pBoolean);
    boolean hasShieldSoul();
    boolean hasProtection();
    void setProtection(boolean pBoolean);
    int getSourceMagic();

    void setSourceMagic(int pId);

}