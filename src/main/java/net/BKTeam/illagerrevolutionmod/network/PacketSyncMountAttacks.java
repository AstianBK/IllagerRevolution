package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.entity.custom.MountEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketSyncMountAttacks {
    private final int key;
    private final byte pId;

    public PacketSyncMountAttacks(FriendlyByteBuf buf) {
        this.key = buf.readInt();
        this.pId = buf.readByte();
    }

    public PacketSyncMountAttacks(int pKey, byte pId){
        this.key = pKey;
        this.pId = pId;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(key);
        buf.writeByte(pId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            Player player=context.get().getSender();
            assert player!=null;
            LivingEntity vehicle = (LivingEntity) player.getVehicle();
            handlePlayActivateAnimation(vehicle);
        });
        context.get().setPacketHandled(true);
    }
    private void handlePlayActivateAnimation(LivingEntity vehicle) {
        if(vehicle instanceof MountEntity mount){
            mount.handledEventKey(this.pId);
        }
    }
}
