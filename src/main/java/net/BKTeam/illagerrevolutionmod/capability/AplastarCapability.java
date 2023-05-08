package net.BKTeam.illagerrevolutionmod.capability;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IAplastarCapability;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AplastarCapability implements IAplastarCapability {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private static final UUID NATURAL_ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    public static ResourceLocation LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"aplastar_effect");
    int oldArmorTotal = 0;
    int ArmorTotal = 0;
    double[] armorForItem = new double[4];

    @Override
    public void setOldArmorTotal(int pArmorTotal) {
        oldArmorTotal =pArmorTotal;
    }

    @Override
    public int getOldArmorTotal() {
        return oldArmorTotal;
    }

    @Override
    public void updateAttributeArmor(LivingEntity living, MobEffectInstance effect) {
        if(living!=null && effect.getEffect()== init_effect.APLASTAR.get()) {
            if (!living.level.isClientSide) {
                for(ItemStack itemStack: living.getArmorSlots() ){
                    if(itemStack.getItem() instanceof ArmorItem armorItem) {
                        armorForItem[armorItem.getSlot().getIndex()]=armorItem.getDefense();
                        double armor = (double) armorItem.getDefense() / 2;
                        living.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()]);
                        living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()],"aplastar modifier" , armor, AttributeModifier.Operation.ADDITION));
                    }
                }
                if(living.getAttributes().hasModifier(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID)){
                    double armor = living.getAttributes().getModifierValue(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID) / 2;
                    living.getAttribute(Attributes.ARMOR).removeModifier(NATURAL_ARMOR_MODIFIER_UUID);
                    living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(NATURAL_ARMOR_MODIFIER_UUID,"natural armor",armor, AttributeModifier.Operation.ADDITION));
                }
            }
            oldArmorTotal=living.getArmorValue();
            ArmorTotal=living.getArmorValue();
        }
    }

    @Override
    public void onTick(LivingEntity entity,MobEffectInstance instance) {

        if(hasChanged()){
            updateAttributeArmor(entity,instance);
        }else {
            setArmorTotal(entity.getArmorValue());
        }
    }

    @Override
    public boolean hasChanged() {
        return oldArmorTotal != ArmorTotal;
    }

    @Override
    public void removeAttributeAmor(LivingEntity living,MobEffectInstance effect) {
        if(living!=null) {
            if (!living.level.isClientSide) {
                for(ItemStack itemStack: living.getArmorSlots() ){
                    if(itemStack.getItem() instanceof ArmorItem armorItem) {
                        living.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()]);
                        living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()],"aplastar modifier", armorItem.getDefense() , AttributeModifier.Operation.ADDITION));
                    }
                }
                if(living.getAttributes().hasModifier(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID)){
                    double armor = living.getAttributes().getModifierValue(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID) * 2;
                    living.getAttribute(Attributes.ARMOR).removeModifier(NATURAL_ARMOR_MODIFIER_UUID);
                    living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(NATURAL_ARMOR_MODIFIER_UUID,"natural Armor",armor, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

    @Override
    public void setArmorTotal(int pInitialArmor) {
        ArmorTotal = pInitialArmor;
    }

    @Override
    public int getArmorTotal() {
        return ArmorTotal;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag=new CompoundTag();
        tag.putInt("armorTotal", getArmorTotal());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setArmorTotal(nbt.getInt("armorTotal"));
    }
    public static class AplastarProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<IAplastarCapability> instance = LazyOptional.of(AplastarCapability::new);

        @NonNull
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityHandler.APLASTAR_CAPABILITY.orEmpty(cap,instance.cast());
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
