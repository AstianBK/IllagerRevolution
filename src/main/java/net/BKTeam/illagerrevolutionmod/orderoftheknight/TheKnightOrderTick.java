package net.BKTeam.illagerrevolutionmod.orderoftheknight;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.ServerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "illagerrevolutionmod", bus = Mod.EventBusSubscriber.Bus.FORGE)

public class TheKnightOrderTick {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event){
        if(event.world.isClientSide)return;
        ServerLevel serverLevel = (ServerLevel) event.world;
        IllagerRevolutionMod.getTheOrders(serverLevel).tick();
    }
}
