package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientMaulerScreenOpenPacket {
	private final int containerId;
	private final int entityId;

	public ClientMaulerScreenOpenPacket(int containerIdIn, int entityIdIn) {
		this.containerId = containerIdIn;
		this.entityId = entityIdIn;
	}

	public static ClientMaulerScreenOpenPacket read(FriendlyByteBuf buf) {
		int containerId = buf.readUnsignedByte();
		int entityId = buf.readInt();
		return new ClientMaulerScreenOpenPacket(containerId, entityId);
	}

	public static void write(ClientMaulerScreenOpenPacket packet, FriendlyByteBuf buf) {
		buf.writeByte(packet.containerId);
		buf.writeInt(packet.entityId);
	}

	public static void handle(ClientMaulerScreenOpenPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
					Minecraft minecraft = Minecraft.getInstance();
					LocalPlayer clientPlayer = minecraft.player;
					Entity entity = null;
					if (clientPlayer != null) {
						entity = clientPlayer.level.getEntity(packet.entityId);
					}
					if (entity instanceof MaulerEntity mauler) {
						ClientPacketHandler.openMaulerInventory(mauler, clientPlayer, packet.containerId);
					}
				}
		);
		ctx.get().setPacketHandled(true);
	}
}