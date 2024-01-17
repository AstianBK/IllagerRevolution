package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.gui.BeastInventoryMenu;
import net.BKTeam.illagerrevolutionmod.gui.BeastInventoryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientPacketHandler {
	public static <T extends IllagerBeastEntity> void openBeastInventory(T beast, LocalPlayer clientPlayer, int containerId) {
		if (beast != null) {
			BeastInventoryMenu beastInventoryMenu = new BeastInventoryMenu(containerId, clientPlayer.getInventory(), ((IHasInventory) beast).getContainer(), beast);
			clientPlayer.containerMenu = beastInventoryMenu;
			BeastInventoryScreen beastInventoryScreen = new BeastInventoryScreen(beastInventoryMenu, clientPlayer.getInventory(), beast);
			Minecraft.getInstance().setScreen(beastInventoryScreen);
		}
	}
}