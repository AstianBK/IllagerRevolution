package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketRefreshPatreon {
    public PacketRefreshPatreon() {
    }

    public PacketRefreshPatreon(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            if(ctx.getSender().getServer().getPlayerList().isOp(ctx.getSender().getGameProfile())) {
                try {
                    URL url = new URL("https://pastebin.com/raw/" + "ULTufUUJ");


                    Scanner sc = new Scanner(url.openStream());
                    StringBuffer sb = new StringBuffer();
                    while (sc.hasNext()) {
                        sb.append(sc.next());
                        System.out.println(sc.next());
                    }

                    IllagerRevolutionMod.CUTE_SKIN_UUID = sb.toString();

                    IllagerRevolutionMod.CUTE_SKIN_UUID  = IllagerRevolutionMod.CUTE_SKIN_UUID .replaceAll("<[^>]*>", "");
                    System.out.println("Refreshing Illager Revolution Patron List");

                } catch (IOException ignored) {
                }
            }
        });
        return true;

    }
}
