package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.orderoftheknight.TheKnightOrders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class DataSaver extends SavedData{
    private TheKnightOrders orders;

    private final ServerLevel level;

    DataSaver(ServerLevel level){
        this.level=level;
        this.orders=new TheKnightOrders(level);
    }

    DataSaver(ServerLevel level,CompoundTag tag){
        this.level=level;
        this.orders = TheKnightOrders.load(level,tag);
    }

    public TheKnightOrders getTheOrderAttack() {
        return this.orders;
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        return null;
    }
}
