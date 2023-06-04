package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketSyncOpenInvetoryMauler3 {
    private final int key;

    public PacketSyncOpenInvetoryMauler3(FriendlyByteBuf buf) {
        this.key = buf.readInt();
    }

    public PacketSyncOpenInvetoryMauler3(int pKey){
        this.key=pKey;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(key);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            Player player=context.get().getSender();
            assert player!=null;
            LivingEntity vehicle = (LivingEntity) player.getVehicle();
            handlePlayActivateAnimation(player,vehicle);
        });
        context.get().setPacketHandled(true);
    }
    private void handlePlayActivateAnimation(Player player, LivingEntity vehicle) {
        if(vehicle instanceof MaulerEntity mauler){
            mauler.openInventory(player);
        }
    }
}
