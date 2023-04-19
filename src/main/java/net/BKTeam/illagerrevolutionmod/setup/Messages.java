package net.BKTeam.illagerrevolutionmod.setup;

import net.BKTeam.illagerrevolutionmod.network.PacketBleedingEffect;
import net.BKTeam.illagerrevolutionmod.network.PacketSpawnedZombified;
import net.BKTeam.illagerrevolutionmod.network.PacketWhistle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.network.PacketSyncSoulBkToClient;
import net.BKTeam.illagerrevolutionmod.network.ClientRakerScreenOpenPacket;

public class Messages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation("illagerrevolutionmod", "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;
        net.messageBuilder(PacketSyncSoulBkToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSyncSoulBkToClient::new)
                .encoder(PacketSyncSoulBkToClient::toBytes)
                .consumer(PacketSyncSoulBkToClient::handle)
                .add();
        net.messageBuilder(ClientRakerScreenOpenPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientRakerScreenOpenPacket::read)
                .encoder(ClientRakerScreenOpenPacket::write)
                .consumer(ClientRakerScreenOpenPacket::handle)
                .add();
        net.messageBuilder(PacketBleedingEffect.class,id(),NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketBleedingEffect::new)
                .encoder(PacketBleedingEffect::encode)
                .consumer(PacketBleedingEffect::handle)
                .add();
        net.messageBuilder(PacketWhistle.class,id(),NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketWhistle::new)
                .encoder(PacketWhistle::encode)
                .consumer(PacketWhistle::handle)
                .add();
        net.messageBuilder(PacketSpawnedZombified.class,id(),NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSpawnedZombified::new)
                .encoder(PacketSpawnedZombified::encode)
                .consumer(PacketSpawnedZombified::handle)
                .add();

}
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllTracking(MSG message, LivingEntity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
}
