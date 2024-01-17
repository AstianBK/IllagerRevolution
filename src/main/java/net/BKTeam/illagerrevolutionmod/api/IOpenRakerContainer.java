package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.minecraft.world.Container;

public interface IOpenRakerContainer {
	void openRakerInventory(RakerEntity raker, Container container);
}