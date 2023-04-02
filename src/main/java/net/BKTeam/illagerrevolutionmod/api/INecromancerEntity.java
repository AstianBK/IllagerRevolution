package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;

import java.util.List;

public interface INecromancerEntity {
    List<FallenKnight> getBondedMinions();
    List<ZombifiedEntity> getInvocations();
}
