package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.BKTeam.illagerrevolutionmod.entity.custom.ReanimatedEntity;

import java.util.List;

public interface IRelatedEntity {
    List<FallenKnight> getBondedMinions();
    void setBoundedMinios(List<FallenKnight> Minions);
}
