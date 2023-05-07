package net.BKTeam.illagerrevolutionmod.capability;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IItemCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemCapability implements IItemCapability {
    public static ResourceLocation LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"sword_tier");
    int tier=0;
    int countHit=0;
    @Override
    public void setTier(int pTier) {
        tier=pTier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public int getCountHit() {
        return countHit;
    }

    @Override
    public void setCountHit(int pCount) {
        countHit=pCount;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag=new CompoundTag();
        tag.putInt("swordTier",getTier());
        tag.putInt("countHit",getCountHit());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setTier(nbt.getInt("swordTier"));
        setCountHit(nbt.getInt("countHit"));
    }

    public static class SwordProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>{

        private final LazyOptional<IItemCapability> instance = LazyOptional.of(ItemCapability::new);

        @NonNull
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityHandler.SWORD_CAPABILITY.orEmpty(cap,instance.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }
    }
}
