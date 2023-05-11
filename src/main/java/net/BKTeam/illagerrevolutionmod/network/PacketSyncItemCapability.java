package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.api.IItemCapability;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;


public class PacketSyncItemCapability {
    private final ItemStack stack;
    private CompoundTag tag;

    public PacketSyncItemCapability(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        this.stack = buf.readItem();
        this.tag = buf.readNbt();

    }

    public PacketSyncItemCapability(ItemStack stack){
        this.stack=stack;
        stack.getCapability(CapabilityHandler.SWORD_CAPABILITY).ifPresent((s)->{
            this.tag = ((INBTSerializable<CompoundTag>)s).serializeNBT();
        });
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

    @OnlyIn(Dist.CLIENT)
    private void consume() {
        Minecraft mc = Minecraft.getInstance();
        if(stack!=null){
            stack.getCapability(CapabilityHandler.SWORD_CAPABILITY).ifPresent((s) ->  {
                ((INBTSerializable<CompoundTag>)s).deserializeNBT(tag);
            });
        }
    }
}
