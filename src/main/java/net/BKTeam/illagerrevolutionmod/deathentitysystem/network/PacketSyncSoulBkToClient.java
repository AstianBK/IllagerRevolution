package net.BKTeam.illagerrevolutionmod.deathentitysystem.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.client.ClientSoulData;

import java.util.function.Supplier;

public class PacketSyncSoulBkToClient {

    private final float souls;

    public PacketSyncSoulBkToClient(float playerSoul) {
        this.souls = playerSoul;
    }

    public PacketSyncSoulBkToClient(FriendlyByteBuf buf) {
        souls = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(souls);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft.getInstance().player.getAttribute(SoulTick.SOUL).setBaseValue(souls);
            ClientSoulData.set(souls);
        });
        return true;
    }
}
