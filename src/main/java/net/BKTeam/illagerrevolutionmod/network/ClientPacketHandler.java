package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientPacketHandler {
	public static void openRakerInventory(RakerEntity raker, LocalPlayer clientPlayer, int containerId) {
		if (raker != null) {
			RakerInventoryMenu rakerInventoryContainer = new RakerInventoryMenu(containerId, clientPlayer.getInventory(), ((IHasInventory)raker).getContainer(), raker);
			clientPlayer.containerMenu = rakerInventoryContainer;
			RakerInventoryScreen rakerInventoryScreen = new RakerInventoryScreen(rakerInventoryContainer, clientPlayer.getInventory(), raker);
			Minecraft.getInstance().setScreen(rakerInventoryScreen);
		}
	}

	public static void openMaulerInventory(MaulerEntity mauler, LocalPlayer clientPlayer, int containerId) {
		if (mauler != null) {
			MaulerInventoryMenu rakerInventoryContainer = new MaulerInventoryMenu(containerId, clientPlayer.getInventory(), ((IHasInventory)mauler).getContainer(), mauler);
			clientPlayer.containerMenu = rakerInventoryContainer;
			MaulerInventoryScreen rakerInventoryScreen = new MaulerInventoryScreen(rakerInventoryContainer, clientPlayer.getInventory(), mauler);
			Minecraft.getInstance().setScreen(rakerInventoryScreen);
		}
	}

	public static void openRavagerInventory(WildRavagerEntity mauler, LocalPlayer clientPlayer, int containerId) {
		if (mauler != null) {
			WildRavagerInventoryMenu rakerInventoryContainer = new WildRavagerInventoryMenu(containerId, clientPlayer.getInventory(), ((IHasInventory)mauler).getContainer(), mauler);
			clientPlayer.containerMenu = rakerInventoryContainer;
			WildRavagerInventoryScreen rakerInventoryScreen = new WildRavagerInventoryScreen(rakerInventoryContainer, clientPlayer.getInventory(), mauler);
			Minecraft.getInstance().setScreen(rakerInventoryScreen);
		}
	}
}