package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.orderoftheknigth.TheKnightOrder;
import net.minecraft.core.BlockPos;

public interface IProxy {
    TheKnightOrder getTheOrderAttack(BlockPos pos);

    boolean isTheOrderAttack(BlockPos pos);


}
