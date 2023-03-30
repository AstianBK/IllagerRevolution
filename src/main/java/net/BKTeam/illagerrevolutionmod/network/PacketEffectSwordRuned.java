package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;

import java.util.function.Supplier;

public class PacketEffectSwordRuned {
    private final ItemStack stack;
    private final Entity entity;

    public PacketEffectSwordRuned(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        this.stack = buf.readItem();
        assert mc.level != null;
        this.entity = mc.level.getEntity(buf.readInt());
    }

    public PacketEffectSwordRuned(ItemStack stack, Entity entity) {
        this.stack = stack;
        this.entity = entity;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.stack);
        buf.writeInt(this.entity.getId());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handlePlayActivateAnimation));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handlePlayActivateAnimation() {
        Minecraft mc = Minecraft.getInstance();
        mc.particleEngine.createTrackingEmitter(this.entity, ModParticles.BKSOULS_PARTICLES.get(), 10);
        var level = mc.level;
        if (level != null) {
            level.playLocalSound(this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.TOTEM_USE, this.entity.getSoundSource(), 1.0F, 1.0F, false);
        }
        if (entity == mc.player) {
            mc.gameRenderer.displayItemActivation(this.stack);
        }
    }
}
