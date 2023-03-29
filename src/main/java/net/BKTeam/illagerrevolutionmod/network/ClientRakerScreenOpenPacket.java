package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;

import java.util.function.Supplier;

public class ClientRakerScreenOpenPacket {
	private final int containerId;
	private final int entityId;

	public ClientRakerScreenOpenPacket(int containerIdIn, int entityIdIn) {
		this.containerId = containerIdIn;
		this.entityId = entityIdIn;
	}

	public static ClientRakerScreenOpenPacket read(FriendlyByteBuf buf) {
		int containerId = buf.readUnsignedByte();
		int entityId = buf.readInt();
		return new ClientRakerScreenOpenPacket(containerId, entityId);
	}

	public static void write(ClientRakerScreenOpenPacket packet, FriendlyByteBuf buf) {
		buf.writeByte(packet.containerId);
		buf.writeInt(packet.entityId);
	}

	public static void handle(ClientRakerScreenOpenPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
					Minecraft minecraft = Minecraft.getInstance();
					LocalPlayer clientPlayer = minecraft.player;
					Entity entity = null;
					if (clientPlayer != null) {
						entity = clientPlayer.level.getEntity(packet.entityId);
					}
					if (entity instanceof RakerEntity raker) {
						ClientPacketHandler.openRakerInventory(raker, clientPlayer, packet.containerId);
					}
				}
		);
		ctx.get().setPacketHandled(true);

	}
}