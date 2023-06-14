package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.BKTeam.illagerrevolutionmod.orderoftheknigth.TheKnightOrder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;

public class KnightEntity extends AbstractIllager {

    private TheKnightOrder order;
    private int waveOfTheOrder;

    private boolean canJoinRaidOfTheOrder;

    private int ticksOutsideRaidTheOrder;
    protected KnightEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_) {
        super(p_32105_, p_32106_);
    }

    public void setRaidOfOrder(TheKnightOrder raidOfOrder){
        this.order=raidOfOrder;
    }
    public void setWaveOfOrder(int wave) {
        this.waveOfTheOrder = wave;
    }

    public void setCanJoinRaidOfTheOrder(boolean isCanJoin){
        this.canJoinRaidOfTheOrder=isCanJoin;
    }

    public void setTicksOutsideRaidTheOrder(int ticksOutsideRaidTheOrder) {
        this.ticksOutsideRaidTheOrder = ticksOutsideRaidTheOrder;
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }
}
