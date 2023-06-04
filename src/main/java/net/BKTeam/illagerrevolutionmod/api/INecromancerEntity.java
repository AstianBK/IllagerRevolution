package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;

import java.util.List;

public interface INecromancerEntity {
    List<FallenKnightEntity> getBondedMinions();
    List<ZombifiedEntity> getInvocations();
}
