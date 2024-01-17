package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketSyncItemCapability {
    private final ItemStack stack;
    private final CompoundTag tag;

    public PacketSyncItemCapability(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
        this.tag = buf.readNbt();

    }

    public PacketSyncItemCapability(ItemStack stack,CompoundTag tag){
        this.stack=stack;
        this.tag=tag;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            assert context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;
            consume();
        });
        context.get().setPacketHandled(true);
    }

    private void consume() {
        if(stack!=null && tag!=null){
            stack.setTag(stack.save(tag));
        }
    }
}