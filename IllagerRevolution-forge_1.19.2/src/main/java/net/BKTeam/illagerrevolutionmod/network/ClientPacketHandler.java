package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.gui.RakerInventoryMenu;
import net.BKTeam.illagerrevolutionmod.gui.RakerInventoryScreen;
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
}