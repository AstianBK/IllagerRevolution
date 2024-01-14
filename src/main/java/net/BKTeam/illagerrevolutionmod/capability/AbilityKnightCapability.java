package net.BKTeam.illagerrevolutionmod.capability;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IAbilityKnightCapability;
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

public class AbilityKnightCapability implements IAbilityKnightCapability {
    public static ResourceLocation LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"ability_knight");
    private boolean isShieldSoul;
    private boolean isProtection;
    private int sourceUUID;

    @Override
    public void setShieldSoul(boolean pBoolean) {
        isShieldSoul=pBoolean;
    }

    @Override
    public boolean hasShieldSoul() {
        return isShieldSoul;
    }

    @Override
    public boolean hasProtection() {
        return isProtection;
    }

    @Override
    public void setProtection(boolean pBoolean) {
        isProtection=pBoolean;
    }

    @Override
    public int getSourceMagic() {
        return sourceUUID;
    }

    @Override
    public void setSourceMagic(int pUUID) {
        sourceUUID=pUUID;
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag=new CompoundTag();
        tag.putBoolean("isProtection",isProtection);
        tag.putBoolean("isShieldSoul",isShieldSoul);
        tag.putInt("sourceUUID",sourceUUID);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setShieldSoul(nbt.getBoolean("isShieldSoul"));
        this.setProtection(nbt.getBoolean("isProtection"));
        this.setSourceMagic(nbt.getInt("sourceUUID"));
    }

    public static class AbilityKnightProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<IAbilityKnightCapability> instance = LazyOptional.of(AbilityKnightCapability::new);

        @NonNull
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityHandler.ABILITY_KNIGHT_CAPABILITY.orEmpty(cap,instance.cast());
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