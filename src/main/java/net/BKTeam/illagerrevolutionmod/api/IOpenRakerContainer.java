package net.BKTeam.illagerrevolutionmod.api;

import net.minecraft.world.Container;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;

public interface IOpenRakerContainer {
	void openRakerInventory(RakerEntity raker, Container container);
}