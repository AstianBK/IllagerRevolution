package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.entity.custom.ReanimatedEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Hunter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;

import java.util.EnumSet;

public class Owner_Defend extends TargetGoal {

    private final ReanimatedEntity owner;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;
    

    public Owner_Defend(ReanimatedEntity pMob, boolean pMustSee) {
        super(pMob, pMustSee);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.owner =pMob;
    }
    
    public boolean canUse() {
        LivingEntity livingentity = this.owner.getOwner();
        LivingEntity livingentity2 = this.owner.getNecromancer();
        if (livingentity == null && livingentity2 == null) {
            return false;
        } else {
            if(livingentity!=null){
                this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
            }else {
                this.ownerLastHurtBy= livingentity2.getLastHurtByMob();
                int i = livingentity2.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
            }
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = this.owner.getOwner();
        LivingEntity livingentity2 = this.owner.getNecromancer();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }else if(livingentity2 != null){
            this.timestamp=livingentity2.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
