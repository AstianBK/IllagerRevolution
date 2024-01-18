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

    public static SimpleChannel MOD_CHANNEL=NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(IllagerRevolutionMod.MOD_ID, "packets"))
            .networkProtocolVersion(()-> PROTOCOL_VERSION)
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();;

    public static void registerMessages() {
        int index = 0;

        MOD_CHANNEL.registerMessage(index++,PacketSyncSoulBkToClient.class,
                PacketSyncSoulBkToClient::toBytes,
                PacketSyncSoulBkToClient::new,
                PacketSyncSoulBkToClient::handle);

        MOD_CHANNEL.registerMessage(index++, PacketEffectSwordRuned.class, PacketEffectSwordRuned::encode,
                PacketEffectSwordRuned::new, PacketEffectSwordRuned::handle);

        MOD_CHANNEL.registerMessage(index++, ClientBeastScreenOpenPacket.class, ClientBeastScreenOpenPacket::write,
                ClientBeastScreenOpenPacket::read, ClientBeastScreenOpenPacket::handle);

        MOD_CHANNEL.registerMessage(index++, PacketProcBleedingEffect.class, PacketProcBleedingEffect::encode,
                PacketProcBleedingEffect::new, PacketProcBleedingEffect::handle);

        MOD_CHANNEL.registerMessage(index++, PacketBleedingEffect.class, PacketBleedingEffect::encode,
                PacketBleedingEffect::new, PacketBleedingEffect::handle);

        MOD_CHANNEL.registerMessage(index++, PacketWhistle.class, PacketWhistle::encode,
                PacketWhistle::new, PacketWhistle::handle);

        MOD_CHANNEL.registerMessage(index++, PacketSpawnedZombified.class, PacketSpawnedZombified::encode,
                PacketSpawnedZombified::new, PacketSpawnedZombified::handle);

        MOD_CHANNEL.registerMessage(index++, PacketSmoke.class, PacketSmoke::encode,
                PacketSmoke::new, PacketSmoke::handle);

        MOD_CHANNEL.registerMessage(index++, PacketSand.class, PacketSand::encode,
                PacketSand::new, PacketSand::handle);

        MOD_CHANNEL.registerMessage(index++, PacketGlowEffect.class, PacketGlowEffect::encode,
                PacketGlowEffect::new, PacketGlowEffect::handle);

        MOD_CHANNEL.registerMessage(index++, PacketSyncMountAttacks.class, PacketSyncMountAttacks::encode,
                PacketSyncMountAttacks::new, PacketSyncMountAttacks::handle);

        MOD_CHANNEL.registerMessage(index++, PacketSyncItemCapability.class, PacketSyncItemCapability::encode,
                PacketSyncItemCapability::new, PacketSyncItemCapability::handle);

        MOD_CHANNEL.registerMessage(index++, PacketStopSound.class,PacketStopSound::write,PacketStopSound::new,
                PacketStopSound::handle);

        MOD_CHANNEL.registerMessage(index++, PacketRefreshPatreon.class,PacketRefreshPatreon::toBytes,PacketRefreshPatreon::new,
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
