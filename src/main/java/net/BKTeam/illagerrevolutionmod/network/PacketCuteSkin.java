package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.Patreon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketCuteSkin {
    UUID entity;
    public PacketCuteSkin(UUID entity) {
        this.entity =  entity;
    }

    public PacketCuteSkin(FriendlyByteBuf buf) {
        this.entity = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(entity);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            assert ctx.getSender()!=null;
            Player player = ctx.getSender().level.getPlayerByUUID(entity);
            if(!Patreon.acolytes.contains(player)) {
                Patreon.acolytes.add(player);
            }
        });
        return true;

    }
}
