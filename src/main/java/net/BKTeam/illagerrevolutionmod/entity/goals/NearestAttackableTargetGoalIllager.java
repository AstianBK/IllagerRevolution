package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerBadlandsEntity;

public class NearestAttackableTargetGoalIllager<T extends LivingEntity> extends   NearestAttackableTargetGoal<T> {

    public NearestAttackableTargetGoalIllager(Mob pMob, Class<T> pTargetType, boolean pMustSee,boolean pMustReach) {
        super(pMob, pTargetType, pMustSee,pMustReach);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.mob instanceof IllagerMinerBadlandsEntity entity && !entity.isHasItems();
    }
}
