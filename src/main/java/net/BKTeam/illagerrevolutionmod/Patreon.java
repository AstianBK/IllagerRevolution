package net.BKTeam.illagerrevolutionmod;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Patreon {

    public static boolean isPatreon(Player player, String string){
        if(string != null) {
            if (string.contains(player.getStringUUID())) {
                return true;
            }
        }
        return player.getGameProfile().getName().equals("Dev");
    }
}
