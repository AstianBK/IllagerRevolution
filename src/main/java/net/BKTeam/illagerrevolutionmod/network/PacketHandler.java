package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.network.PacketSyncSoulBkToClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel MOD_CHANNEL;

    public static void registerMessages() {
        int index = 0;
        SimpleChannel channel=NetworkRegistry.ChannelBuilder.named(
                        new ResourceLocation(IllagerRevolutionMod.MOD_ID, "messages"))
                .networkProtocolVersion(()-> PROTOCOL_VERSION)
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        MOD_CHANNEL=channel;

        channel.messageBuilder(PacketSyncSoulBkToClient.class,index++)
                .decoder(PacketSyncSoulBkToClient::new).
                encoder(PacketSyncSoulBkToClient::toBytes)
                .consumerNetworkThread(PacketSyncSoulBkToClient::handle).add();

        channel.registerMessage(index++, PacketEffectSwordRuned.class, PacketEffectSwordRuned::encode,
                PacketEffectSwordRuned::new, PacketEffectSwordRuned::handle);

        channel.registerMessage(index++, ClientBeastScreenOpenPacket.class, ClientBeastScreenOpenPacket::write,
                ClientBeastScreenOpenPacket::read, ClientBeastScreenOpenPacket::handle);

        channel.registerMessage(index++, PacketProcBleedingEffect.class, PacketProcBleedingEffect::encode,
                PacketProcBleedingEffect::new, PacketProcBleedingEffect::handle);

        channel.registerMessage(index++, PacketBleedingEffect.class, PacketBleedingEffect::encode,
                PacketBleedingEffect::new, PacketBleedingEffect::handle);

        channel.registerMessage(index++, PacketWhistle.class, PacketWhistle::encode,
                PacketWhistle::new, PacketWhistle::handle);

        channel.registerMessage(index++, PacketSpawnedZombified.class, PacketSpawnedZombified::encode,
                PacketSpawnedZombified::new, PacketSpawnedZombified::handle);

        channel.registerMessage(index++, PacketSmoke.class, PacketSmoke::encode,
                PacketSmoke::new, PacketSmoke::handle);

        channel.registerMessage(index++, PacketSand.class, PacketSand::encode,
                PacketSand::new, PacketSand::handle);

        channel.registerMessage(index++, PacketGlowEffect.class, PacketGlowEffect::encode,
                PacketGlowEffect::new, PacketGlowEffect::handle);

        channel.registerMessage(index++, PacketSyncMountAttacks.class, PacketSyncMountAttacks::encode,
                PacketSyncMountAttacks::new, PacketSyncMountAttacks::handle);

        channel.registerMessage(index++, PacketSyncItemCapability.class, PacketSyncItemCapability::encode,
                PacketSyncItemCapability::new, PacketSyncItemCapability::handle);

        channel.registerMessage(index++, PacketStopSound.class,PacketStopSound::write,PacketStopSound::new,
                PacketStopSound::handle);

        channel.registerMessage(index++, PacketRefreshPatreon.class,PacketRefreshPatreon::toBytes,PacketRefreshPatreon::new,
                PacketRefreshPatreon::handle);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        MOD_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),message);
    }

    public static <MSG> void sendToServer(MSG message) {
        MOD_CHANNEL.sendToServer(message);
    }

    public static <MSG> void sendToAllTracking(MSG message, LivingEntity entity) {
        MOD_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
}
