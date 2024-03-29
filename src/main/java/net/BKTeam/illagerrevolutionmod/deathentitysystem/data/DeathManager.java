package net.BKTeam.illagerrevolutionmod.deathentitysystem.data;

import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.network.PacketSyncSoulBkToClient;

public class DeathManager {
    private static int count=0;
    public static void tick(Level level){
        count++;
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                float playerMana = (float) serverPlayer.getAttribute(SoulTick.SOUL).getValue();
                PacketHandler.sendToPlayer(new PacketSyncSoulBkToClient(playerMana), serverPlayer);
            }
        });

    }
}
