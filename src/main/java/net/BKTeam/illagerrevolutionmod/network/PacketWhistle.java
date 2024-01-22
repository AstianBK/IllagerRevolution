package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketWhistle {
    private final double x;
    private final double y;
    private final double z;


    public PacketWhistle(FriendlyByteBuf buf) {
        this.x=buf.readDouble();
        this.y=buf.readDouble();
        this.z=buf.readDouble();
    }

    public PacketWhistle(Entity pEntity){
        this.x=pEntity.getX();
        this.y=pEntity.getY();
        this.z=pEntity.getZ();
    }
    public PacketWhistle(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            assert context.get().getDirection()== NetworkDirection.PLAY_TO_CLIENT;
            handleEffect();
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleEffect() {
        Minecraft mc = Minecraft.getInstance();
        mc.particleEngine.createParticle(ParticleTypes.NOTE,x,y+0.3d,z,0.0f,0.5f,0.0f);
    }
}
