package net.BKTeam.illagerrevolutionmod.deathentitysystem.data;

import net.minecraftforge.event.TickEvent;

public class DeathEntityEvent {
    public static void onLivintDeathEvent(TickEvent.WorldTickEvent event){
        DeathManager.tick(event.world);
    }

}
