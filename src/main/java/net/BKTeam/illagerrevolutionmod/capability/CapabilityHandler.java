package net.BKTeam.illagerrevolutionmod.capability;

import net.BKTeam.illagerrevolutionmod.api.IAplastarCapability;
import net.BKTeam.illagerrevolutionmod.api.IItemCapability;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class CapabilityHandler {
    public static final Capability<IItemCapability> SWORD_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAplastarCapability> APLASTAR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(IItemCapability.class);
        event.register(IAplastarCapability.class);
    }

    public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event){
        if(event.getObject().getItem() == ModItems.JUNK_AXE.get()){
            event.addCapability(ItemCapability.LOCATION,new ItemCapability.SwordProvider());
        }
    }

    public static void attachEntityCapability(AttachCapabilitiesEvent<LivingEntity> event){
        event.addCapability(AplastarCapability.LOCATION,new AplastarCapability.AplastarProvider());
    }

    @Nullable
    public static <T> T getItemCapability(ItemStack item, Capability<T> capability){
        if(item!=null){
            return item.getCapability(capability).isPresent() ? item.getCapability(capability).orElseThrow(() -> new IllegalArgumentException("Lazy optional must not be empty")) : null;
        }
        return null;
    }

    @Nullable
    public static <T> T getEntityCapability(LivingEntity entity, Capability<T> capability){
        if(entity!=null){
            return entity.getCapability(capability).isPresent() ? entity.getCapability(capability).orElseThrow(() -> new IllegalArgumentException("Lazy optional must not be empty")) : null;
        }
        return null;
    }
}
