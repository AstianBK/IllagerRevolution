package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.minecraft.client.Minecraft;
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


public class PacketSmoke {
    private final Entity entity;

    public PacketSmoke(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        this.entity = mc.level.getEntity(buf.readInt());
    }

    public PacketSmoke(Entity pEntity){
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
        for (int i = 0; i < 24; i++) {
            double x1 = entity.getX();
            double x2 = entity.getY();
            double x3 = entity.getZ();
            mc.particleEngine.createParticle(ParticleTypes.LARGE_SMOKE, x1, x2, x3,random.nextFloat(-0.1f, 0.1f), 0.1f, random.nextFloat(-0.1f, 0.1f));
        }
    }
}
