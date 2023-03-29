package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerBadlandsEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerEntity;

public class HurtByTargetGoalIllager extends HurtByTargetGoal{
    public HurtByTargetGoalIllager(PathfinderMob pMob, Class<?>... pToIgnoreDamage) {
        super(pMob, pToIgnoreDamage);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && (this.mob instanceof IllagerMinerEntity bk && !bk.isHasItems()  || this.mob instanceof IllagerMinerBadlandsEntity bk1 && !bk1.isHasItems());
    }
}
