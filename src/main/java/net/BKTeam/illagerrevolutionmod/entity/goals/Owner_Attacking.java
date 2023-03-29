package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;

import java.util.EnumSet;

public class Owner_Attacking extends TargetGoal {
    private final ZombifiedEntity owner;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public Owner_Attacking(ZombifiedEntity powner) {
        super(powner, false);
        this.owner = powner;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }
    public boolean canUse() {
        LivingEntity livingentity = this.owner.getOwner();
        if (livingentity == null) {
            return false;
        } else {
            this.ownerLastHurt = livingentity.getLastHurtMob();
            int i = livingentity.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
        }
    }
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingentity = this.owner.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
