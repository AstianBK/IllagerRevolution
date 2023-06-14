package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.api.IProxy;
import net.BKTeam.illagerrevolutionmod.orderoftheknigth.TheKnightOrder;
import net.BKTeam.illagerrevolutionmod.orderoftheknigth.TheKnightOrders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class DataSaver extends SavedData{
    private TheKnightOrders orders;

    private final ServerLevel level;

    DataSaver(ServerLevel level){
        this.level=level;
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
