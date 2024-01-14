package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;


public class PacketSand {
    private final BlockPos posOrigin;

    private final BlockPos posTarget;

    public PacketSand(FriendlyByteBuf buf) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        this.posTarget=buf.readBlockPos();
        this.posOrigin=buf.readBlockPos();
    }

    public PacketSand(Entity pEntity,Entity pTarget){
        this.posOrigin = pEntity.getOnPos();
        this.posTarget = new BlockPos(pTarget.getX(),pTarget.getY()+pTarget.getBbHeight(),pTarget.getZ());
    }

    public PacketSand(BlockPos posOrigin,BlockPos posTarget){
        this.posOrigin = posOrigin;
        this.posTarget = posTarget;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(posOrigin);
        buf.writeBlockPos(posTarget);
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
        mc.level.playSound(null,posTarget.getX(),posTarget.getY(),posTarget.getZ(),SoundEvents.SAND_BREAK, SoundSource.HOSTILE,5.0f,-1.0f/(random.nextFloat() * 0.4F + 0.8F));
        for (int i = 0; i < 20; i++) {
            double x1 = posTarget.getX() + random.nextDouble(-0.3d,0.3d);
            double x2 = posTarget.getY() - 0.2d;
            double x3 = posTarget.getZ() + random.nextDouble(-0.3d,0.3d);
            Particle particle = mc.particleEngine.createParticle(ParticleTypes.SMOKE, x1, x2, x3,((float)posOrigin.getX()-(float) posTarget.getX())*0.2f, random.nextFloat(-0.2f,0.1f), ((float)posOrigin.getZ()-(float)posTarget.getZ())*0.2f);
            assert particle != null;
            particle.setColor(0.9255f,0.8863f,0.7765f);
        }
    }
}