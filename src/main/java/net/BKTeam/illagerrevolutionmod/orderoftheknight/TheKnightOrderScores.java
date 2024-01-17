package net.BKTeam.illagerrevolutionmod.orderoftheknight;

import net.BKTeam.illagerrevolutionmod.DataSaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class TheKnightOrderScores extends SavedData {
    public int kill;
    public UUID uuid;
    TheKnightOrderScores(UUID uuid,int score){
        this.kill=score;
        this.uuid=uuid;
    }

    TheKnightOrderScores(CompoundTag compoundTag){
        this.uuid = compoundTag.getUUID("idPlayer");
        this.kill = compoundTag.getInt("killPlayer");
    }
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.putUUID("idPlayer",this.uuid);
        pCompoundTag.putInt("killPlayer",this.kill);
        return pCompoundTag;
    }
}
