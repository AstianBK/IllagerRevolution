package net.BKTeam.illagerrevolutionmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketStopSound {
	@Nullable
	private final ResourceLocation name;
	@Nullable
	private final SoundSource source;

	public PacketStopSound(@org.jetbrains.annotations.Nullable ResourceLocation id, @org.jetbrains.annotations.Nullable SoundSource source) {
		this.name=id;
		this.source=source;
	}

	public PacketStopSound(FriendlyByteBuf buf) {
		int i = buf.readByte();
		if ((i & 1) > 0) {
			this.source = buf.readEnum(SoundSource.class);
		} else {
			this.source = null;
		}

		if ((i & 2) > 0) {
			this.name = buf.readResourceLocation();
		} else {
			this.name = null;
		}
	}

	public void write(FriendlyByteBuf buf) {
		if (this.source != null) {
			if (this.name != null) {
				buf.writeByte(3);
				buf.writeEnum(this.source);
				buf.writeResourceLocation(this.name);
			} else {
				buf.writeByte(1);
				buf.writeEnum(this.source);
			}
		} else if (this.name != null) {
			buf.writeByte(2);
			buf.writeResourceLocation(this.name);
		} else {
			buf.writeByte(0);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
					Minecraft minecraft = Minecraft.getInstance();
					assert minecraft.getConnection()!=null;
					minecraft.getConnection().handleStopSoundEvent(new ClientboundStopSoundPacket(this.name,this.source));

				}
		);
		ctx.get().setPacketHandled(true);
	}
}