package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.minecraft.world.Container;

public interface IOpenBeatsContainer {
	void openRakerInventory(IllagerBeastEntity beast, Container container);
}