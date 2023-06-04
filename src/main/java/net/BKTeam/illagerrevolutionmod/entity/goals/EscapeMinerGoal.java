package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;

import java.util.EnumSet;


public class EscapeMinerGoal<T extends LivingEntity> extends AvoidEntityGoal {
    private final IllagerMinerEntity goalOwner;
    public EscapeMinerGoal(PathfinderMob pMob, Class<T> pEntityClassToAvoid, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        super(pMob,pEntityClassToAvoid,pMaxDistance,pWalkSpeedModifier,pSprintSpeedModifier);
        this.setFlags(EnumSet.of(Flag.MOVE,Flag.TARGET));
        this.goalOwner=(IllagerMinerEntity) pMob;
    }
    @Override
    public boolean canUse() {
        return super.canUse() && this.goalOwner.isHasItems();
    }
    @Override
    public void start() {
        super.start();
        this.goalOwner.playSound(SoundEvents.VINDICATOR_CELEBRATE, 0.35F, 1.0F / (this.goalOwner.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public void stop() {
        super.stop();
        this.goalOwner.removeEffect(MobEffects.INVISIBILITY);
    }

    @Override
    public void tick() {
        super.tick();
        this.goalOwner.setTarget(null);
    }

}
