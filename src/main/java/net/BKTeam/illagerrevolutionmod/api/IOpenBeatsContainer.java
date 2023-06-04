package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.world.Container;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;

public interface IOpenBeatsContainer {
	void openRakerInventory(RakerEntity raker, Container container);

	void openMaulerInventory(MaulerEntity mauler, Container container);

	void openRavagerInventory(WildRavagerEntity ravager, Container container);
}