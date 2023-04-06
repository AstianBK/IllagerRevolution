package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.BKTeam.illagerrevolutionmod.entity.custom.ReanimatedEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SpawnReanimatedGoal extends Goal {
    private final ReanimatedEntity reanimated;

    public SpawnReanimatedGoal(ReanimatedEntity reanimated){
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK,Flag.TARGET,Flag.JUMP));
        this.reanimated=reanimated;
    }
    @Override
    public boolean canUse() {
        if(this.reanimated instanceof FallenKnight knight){
            return !knight.isArmed() && !knight.isRearmed() && !knight.isUnarmed();
        }else {
            return ((ZombifiedEntity)this.reanimated).getIsSpawned();
        }
    }

    @Override
    public void start() {
        this.reanimated.getNavigation().stop();
        super.start();
    }

    @Override
    public void tick() {
        this.reanimated.getNavigation().stop();
        super.tick();
    }
}
