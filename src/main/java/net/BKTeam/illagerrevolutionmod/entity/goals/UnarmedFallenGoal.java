package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class UnarmedFallenGoal extends Goal {
    private final FallenKnight reanimated;

    public UnarmedFallenGoal(FallenKnight fallenKnight){
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK,Flag.TARGET,Flag.JUMP));
        this.reanimated=fallenKnight;
    }
    @Override
    public boolean canUse() {
        return !this.reanimated.isArmed() && !this.reanimated.isRearmed() && !this.reanimated.isUnarmed();
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
