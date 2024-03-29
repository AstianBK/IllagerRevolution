package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;


public class PacketGlowEffect {
    private final Entity entity;

    public PacketGlowEffect(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        this.entity = mc.level.getEntity(buf.readInt());
    }

    public PacketGlowEffect(Entity pEntity){
        this.entity=pEntity;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entity.getId());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            assert context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;
            handlePlayActivateAnimation();
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handlePlayActivateAnimation() {
        Minecraft mc = Minecraft.getInstance();
        Random random = new Random();
        double box=entity.getBbWidth();
        double xp=entity.getX() + random.nextDouble(-box,box);
        double yp=entity.getY() + random.nextDouble(0.0d,entity.getBbHeight());
        double zp=entity.getZ() + random.nextDouble(-box,box);
        assert mc.level!=null;
        Particle particle = mc.particleEngine.createParticle(ParticleTypes.GLOW, xp, yp, zp,0.0f, 0.0f, 0.0f);
        assert particle != null;
        particle.setColor(1f,1f,1f);
    }
}