package net.BKTeam.illagerrevolutionmod.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAbilityKnightCapability extends INBTSerializable<CompoundTag> {
    void setShieldSoul(boolean pBoolean);
    boolean hasShieldSoul();
    boolean hasProtection();
    void setProtection(boolean pBoolean);
    int getSourceMagic();

    void setSourceMagic(int pId);

}