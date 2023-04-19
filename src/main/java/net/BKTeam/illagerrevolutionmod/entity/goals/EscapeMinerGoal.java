package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerBadlandsEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerEntity;

import java.util.EnumSet;


public class EscapeMinerGoal<T extends LivingEntity> extends AvoidEntityGoal {
    private final IllagerMinerBadlandsEntity goalOwner;
    public EscapeMinerGoal(PathfinderMob pMob, Class<T> pEntityClassToAvoid, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        super(pMob,pEntityClassToAvoid,pMaxDistance,pWalkSpeedModifier,pSprintSpeedModifier);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE,Flag.TARGET));
        this.goalOwner=(IllagerMinerBadlandsEntity) pMob;
    }
    @Override
    public boolean canUse() {
        return super.canUse() && this.goalOwner.isHasItems() && !this.goalOwner.isAttackLantern();
    }
    @Override
    public void start() {
        super.start();
        this.goalOwner.playSound(SoundEvents.WITCH_CELEBRATE, 1.0F, -2.5F / (this.goalOwner.getRandom().nextFloat() * 0.4F + 0.8F));
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
