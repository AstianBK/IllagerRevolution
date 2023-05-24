package net.BKTeam.illagerrevolutionmod.deathentitysystem.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.client.ClientSoulData;

import java.util.function.Supplier;

public class PacketSyncSoulBkToClient {

    private final float playerSoul;

    public PacketSyncSoulBkToClient(float playerSoul) {
        this.playerSoul = playerSoul;
    }

    public PacketSyncSoulBkToClient(FriendlyByteBuf buf) {
        playerSoul = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(playerSoul);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft.getInstance().player.getAttribute(SoulTick.SOUL).setBaseValue(playerSoul);

            ClientSoulData.set(playerSoul);
        });
        return true;
    }
}
