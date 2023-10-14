package net.BKTeam.illagerrevolutionmod.capability;

import net.BKTeam.illagerrevolutionmod.api.IAbilityKnightCapability;
import net.BKTeam.illagerrevolutionmod.api.IMauledCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class CapabilityHandler {
    public static final Capability<IMauledCapability> MAULED_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAbilityKnightCapability> ABILITY_KNIGHT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(IMauledCapability.class);
        event.register(IAbilityKnightCapability.class);
    }

    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof LivingEntity){
            event.addCapability(MauledCapability.LOCATION,new MauledCapability.AplastarProvider());
            event.addCapability(AbilityKnightCapability.LOCATION,new AbilityKnightCapability.AbilityKnightProvider());
        }
    }

    @Nullable
    public static <T> T getEntityCapability(Entity entity, Capability<T> capability){
        if(entity!=null){
            if(entity.isAlive()){
                return entity.getCapability(capability).isPresent() ? entity.getCapability(capability).orElseThrow(() -> new IllegalArgumentException("Lazy optional must not be empty")) : null;
            }
        }
        return null;
    }
}
