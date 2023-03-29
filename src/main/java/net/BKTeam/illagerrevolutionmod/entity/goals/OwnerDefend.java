package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;

import java.util.EnumSet;

public class OwnerDefend extends TargetGoal {

    private final ZombifiedEntity owner;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;
    

    public OwnerDefend(ZombifiedEntity pMob, boolean pMustSee) {
        super(pMob, pMustSee);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.owner =pMob;
    }
    
    public boolean canUse() {
        LivingEntity livingentity = this.owner.getOwner();
        if (livingentity == null) {
            return false;
        } else {
            this.ownerLastHurtBy = livingentity.getLastHurtByMob();
            int i = livingentity.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
        }

    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = this.owner.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
