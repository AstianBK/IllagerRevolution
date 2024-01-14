package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.entity.custom.ReanimatedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class OwnerAttacking extends TargetGoal {
    private final ReanimatedEntity owner;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public OwnerAttacking(ReanimatedEntity powner) {
        super(powner, false);
        this.owner = powner;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }
    public boolean canUse() {
        LivingEntity livingentity = this.owner.getOwner();
        LivingEntity livingentity2 = this.owner.getNecromancer();
        if (livingentity == null && livingentity2 == null) {
            return false;
        } else {
            if(livingentity!=null){
                this.ownerLastHurt = livingentity.getLastHurtMob();
                int i = livingentity.getLastHurtMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
            }else {
                this.ownerLastHurt = livingentity2.getLastHurtMob();
                int i = livingentity2.getLastHurtMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
            }
        }
    }
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingentity = this.owner.getOwner();
        LivingEntity livingentity2 = this.owner.getNecromancer();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
        } else if (livingentity2 != null) {
            this.timestamp = livingentity2.getLastHurtMobTimestamp();
        }
        super.start();
    }
}
