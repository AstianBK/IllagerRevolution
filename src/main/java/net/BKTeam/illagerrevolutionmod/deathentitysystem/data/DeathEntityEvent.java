package net.BKTeam.illagerrevolutionmod.deathentitysystem.data;

import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.network.PacketSyncSoulBkToClient;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;

public class DeathEntityEvent {
    public static void onLivintDeathEvent(TickEvent.LevelTickEvent event){
        event.level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                float souls = (float) serverPlayer.getAttribute(SoulTick.SOUL).getValue();
                PacketHandler.sendToPlayer(new PacketSyncSoulBkToClient(souls), serverPlayer);
            }
        });
    }

}
