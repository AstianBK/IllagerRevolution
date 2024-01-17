package net.BKTeam.illagerrevolutionmod.orderoftheknight;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "illagerrevolutionmod", bus = Mod.EventBusSubscriber.Bus.FORGE)

public class TheKnightOrderTick {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event){
        IllagerRevolutionMod.getTheOrders(event.getServer().overworld()).tick();
    }
}
