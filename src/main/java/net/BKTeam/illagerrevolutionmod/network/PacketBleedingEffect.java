package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketBleedingEffect {
    private final Entity entity;

    public PacketBleedingEffect(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        this.entity = mc.level.getEntity(buf.readInt());
    }

    public PacketBleedingEffect(Entity pEntity){
        this.entity=pEntity;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entity.getId());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            assert context.get().getDirection()== NetworkDirection.PLAY_TO_CLIENT;
            handlePlayActivateAnimation();
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handlePlayActivateAnimation() {
        Minecraft mc = Minecraft.getInstance();
        mc.particleEngine.createTrackingEmitter(entity , ModParticles.BLOOD_PARTICLES.get() , 17);
    }
}
