package net.BKTeam.illagerrevolutionmod.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface  IItemCapability extends INBTSerializable<CompoundTag> {
    void setTier(int pTier);
    int getTier();
}
