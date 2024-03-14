package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.orderoftheknight.TheKnightOrder;
import net.BKTeam.illagerrevolutionmod.orderoftheknight.TheKnightOrders;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class KnightEntity extends AbstractIllager {

    private TheKnightOrder order;
    private int waveOfTheOrder;

    private boolean canJoinRaidOfTheOrder;

    private int ticksOutsideRaidTheOrder;

    protected KnightEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_) {
        super(p_32105_, p_32106_);
    }

    public TheKnightOrder getRaidOfOrder(){
        return this.order;
    }
    public void setRaidOfOrder(TheKnightOrder raidOfOrder){
        this.order=raidOfOrder;
    }
    public void setWaveOfOrder(int wave) {
        this.waveOfTheOrder = wave;
    }

    public int getWaveOfTheOrder(){
        return this.waveOfTheOrder;
    }

    public void setCanJoinRaidOfTheOrder(boolean isCanJoin){
        this.canJoinRaidOfTheOrder=isCanJoin;
    }

    public void setTicksOutsideRaidTheOrder(int ticksOutsideRaidTheOrder) {
        this.ticksOutsideRaidTheOrder = ticksOutsideRaidTheOrder;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3,new PathfindToRaidGoal<>(this));
        this.goalSelector.addGoal(3,new KnightMoveThroughVillageGoal(this,(double)1.05F, 1));
    }

    @Override
    public void die(DamageSource pCause) {
        if(this.level() instanceof ServerLevel){
            Entity entity = pCause.getEntity();
            TheKnightOrder raid = this.getRaidOfOrder();

            if (raid != null) {
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    raid.addDefender(entity);
                    if(entity instanceof Player player){
                        raid.setScoreKillForUUID(player);
                    }
                }else if(entity instanceof TamableAnimal animal && animal.isTame()) {
                    if(animal.getOwner() instanceof Player player){
                        raid.setScoreKillForUUID(player);
                        raid.addDefender(player);
                    }
                }
                raid.removeFromRaid(this, false);
            }
        }

        super.die(pCause);
    }
    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if(pEntity instanceof LivingEntity target){
            if(target==this.getTarget()){
                return false;
            }
            if(target.getMobType() == MobType.ILLAGER){
                return true;
            }else if(target instanceof IllagerBeastEntity beast){
                return !beast.isTame();
            }
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public void heal(float pHealAmount) {
        if (this.hasActiveRaidOfOrder() && this.isAlive()) {
            this.getRaidOfOrder().updateBossbar();
        }
        super.heal(pHealAmount);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.hasActiveRaidOfOrder()) {
            this.getRaidOfOrder().updateBossbar();
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel && this.isAlive()) {
            TheKnightOrder raid = this.getRaidOfOrder();
            if (this.canJoinRaid()) {
                if (raid == null) {
                    if (this.level().getGameTime() % 20L == 0L) {
                        TheKnightOrder raid1 = IllagerRevolutionMod.getTheOrders((ServerLevel) this.level()).getNearbyRaid(this.blockPosition(),9123);
                        if (raid1 != null && TheKnightOrders.canJoinRaid(this,raid1)) {
                            raid1.joinRaid(raid1.getGroupsSpawned(), this, (BlockPos)null, true);
                        }
                    }
                } else {
                    LivingEntity livingentity = this.getTarget();
                    if (livingentity != null && (livingentity.getType() == EntityType.PLAYER || livingentity.getType() == EntityType.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_37845_) {

    }

    @Override
    public boolean hasActiveRaid() {
        return super.hasActiveRaid();
    }

    public boolean hasActiveRaidOfOrder(){
        return (this.getRaidOfOrder() != null && this.getRaidOfOrder().isActive());
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }
}
