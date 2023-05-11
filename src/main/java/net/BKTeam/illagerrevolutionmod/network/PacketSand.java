package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.particle.custom.Bk_SmokeParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;


public class PacketSand {
    private final Entity entity;
    private final Entity target;

    public PacketSand(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        this.target = mc.level.getEntity(buf.readInt());
        this.entity = mc.level.getEntity(buf.readInt());
    }

    public PacketSand(Entity pEntity,Entity pTarget){
        this.entity=pEntity;
        this.target=pTarget;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entity.getId());
        buf.writeInt(target.getId());
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
        assert mc.level!=null;
        for (int i = 0; i < 20; i++) {
            double x1 = target.getX() + random.nextDouble(-0.3d,0.3d);
            double x2 = target.getY() + target.getBbHeight() - 0.2d;
            double x3 = target.getZ() + random.nextDouble(-0.3d,0.3d);
            Particle particle =mc.particleEngine.createParticle(ParticleTypes.SMOKE, x1, x2, x3,((float)entity.getX()-(float) target.getX())*0.2f, random.nextFloat(-0.2f,0.1f), (entity.getZ()-target.getZ())*0.2d);
            particle.setColor(0.9255f,0.8863f,0.7765f);
        }
    }
}
