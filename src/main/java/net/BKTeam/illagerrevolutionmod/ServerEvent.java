package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes.*;

@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerEvent {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.ILLAGERMINERBADLANDS.get(), IllagerScavengerEntity.setAttributes());
        event.put(ModEntityTypes.RAKER.get(), RakerEntity.setAttributes());
        event.put(ModEntityTypes.ILLAGERMINER.get(), IllagerMinerEntity.setAttributes());
        event.put(ModEntityTypes.ILLAGERBEASTTAMER.get(), IllagerBeastTamerEntity.setAttributes());
        event.put(ModEntityTypes.ZOMBIFIED.get(), ZombifiedEntity.setAttributes());
        event.put(ModEntityTypes.BLADE_KNIGHT.get(), BladeKnightEntity.setAttributes());
        event.put(ModEntityTypes.FALLEN_KNIGHT.get(), FallenKnightEntity.setAttributes());
        event.put(MAULER.get(), MaulerEntity.setAttributes());
        event.put(WILD_RAVAGER.get(), WildRavagerEntity.createAttributes().build());
        event.put(SCROUNGER.get(),ScroungerEntity.setAttributes());
    }
}
