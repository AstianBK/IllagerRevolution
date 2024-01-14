package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class ChargedGoal extends Goal {
    private final BulkwarkEntity raider;
    private final double speedModifier;
    private Vec3 targetPos;
    public ChargedGoal(BulkwarkEntity p_37936_, double p_37937_) {
        this.raider = p_37936_;
        this.speedModifier = p_37937_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        return this.raider.getTarget()!=null && this.raider.isCharged() && !this.raider.isAbsorbMode() && !this.raider.slamMoment;
    }

    public boolean canContinueToUse() {
        if (this.raider.getNavigation().isDone()) {
            return false;
        } else {
            return this.raider.getTarget() != null;
        }
    }
    public void start() {
        super.start();
        if(this.raider.getTarget()!=null){
            this.targetPos = this.raider.vec3Charged.normalize().add(triangle(0.0D, 0.0172275D * (double)0.1f,raider.level.random), triangle(0.0D, 0.0172275D * (double)0.1F,raider.level.random), triangle(0.0D, 0.0172275D * (double)0.1F,raider.level.random)).scale((double)this.speedModifier);
            this.raider.setDeltaMovement(this.targetPos);
        }
    }

    public void tick() {
        this.targetPos=this.targetPos.scale(0.8F);
        this.raider.setDeltaMovement(this.targetPos);
    }
    public double triangle(double p_216329_, double p_216330_, Random random) {
        return p_216329_ + p_216330_ * (random.nextDouble() - random.nextDouble());
    }
}
