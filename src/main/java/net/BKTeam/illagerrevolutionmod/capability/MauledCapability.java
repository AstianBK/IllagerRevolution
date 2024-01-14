package net.BKTeam.illagerrevolutionmod.capability;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IMauledCapability;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class MauledCapability implements IMauledCapability {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private static final UUID NATURAL_ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    public static ResourceLocation LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"mauled_effect");
    int oldArmorTotal = 0;
    int ArmorTotal = 0;
    double[] armorForItem = new double[4];
    double armorNatural = 0;

    @Override
    public void setArmorNatural(double pArmorNatural) {
        armorNatural =pArmorNatural;
    }

    @Override
    public double getArmorNatural() {
        return armorNatural;
    }

    @Override
    public void updateAttributeArmor(LivingEntity living, MobEffectInstance effect) {
        if(living!=null && effect.getEffect()== InitEffect.MAULED.get()) {
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
                    double armor = living.getAttributes().getModifierValue(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID) /2;
                    armorNatural=living.getAttributes().getModifierValue(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID);
                    living.getAttribute(Attributes.ARMOR).removeModifier(NATURAL_ARMOR_MODIFIER_UUID);
                    living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(NATURAL_ARMOR_MODIFIER_UUID,"natural armor",armor, AttributeModifier.Operation.ADDITION));
                }
            }
            this.checkArmor0(living);

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

    public void checkArmor0(LivingEntity living){
        if(living.getArmorValue()<1.0d){
            for(ItemStack itemStack: living.getArmorSlots() ){
                if(itemStack.getItem() instanceof ArmorItem armorItem) {
                    if(armorItem.getDefense()<1.0d){
                        double armor = 0;
                        living.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()]);
                        living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()],"aplastar modifier" , armor, AttributeModifier.Operation.ADDITION));

                    }else {
                        double armor = 1;
                        living.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()]);
                        living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[armorItem.getSlot().getIndex()],"aplastar modifier" , armor, AttributeModifier.Operation.ADDITION));

                    }
                }
            }
            if(living.getAttributes().hasModifier(Attributes.ARMOR,NATURAL_ARMOR_MODIFIER_UUID)){
                double armor = 1;
                living.getAttribute(Attributes.ARMOR).removeModifier(NATURAL_ARMOR_MODIFIER_UUID);
                living.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(NATURAL_ARMOR_MODIFIER_UUID,"natural armor",armor, AttributeModifier.Operation.ADDITION));
            }
        }
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
                    double armor = armorNatural;
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
        tag.putDouble("armorNatural", getArmorNatural());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setArmorNatural(nbt.getDouble("armorNatural"));
    }

    public static class AplastarProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<IMauledCapability> instance = LazyOptional.of(MauledCapability::new);

        @NonNull
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CapabilityHandler.MAULED_CAPABILITY.orEmpty(cap,instance.cast());
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