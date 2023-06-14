package net.BKTeam.illagerrevolutionmod.orderoftheknigth;

import com.google.common.collect.Maps;
import net.BKTeam.illagerrevolutionmod.entity.goals.KnightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TheKnightOrders extends SavedData {
    private final Map<Integer, TheKnightOrder> raidMap = Maps.newHashMap();
    private final ServerLevel level;
    private int nextAvailableID;
    private int tick;

    public TheKnightOrders(ServerLevel p_37956_) {
        this.level = p_37956_;
        this.nextAvailableID = 1;
        this.setDirty();
    }

    public TheKnightOrder get(int pId) {
        return this.raidMap.get(pId);
    }

    public void tick() {
        ++this.tick;
        Iterator<TheKnightOrder> iterator = this.raidMap.values().iterator();

        while(iterator.hasNext()) {
            TheKnightOrder raid = iterator.next();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                raid.stop();
            }

            if (raid.isStopped()) {
                iterator.remove();
                this.setDirty();
            } else {
                raid.tick();
            }
        }

        if (this.tick % 200 == 0) {
            this.setDirty();
        }
    }

    public static boolean canJoinRaid(KnightEntity pRaider, TheKnightOrder pRaid) {
        if (pRaider != null && pRaid != null && pRaid.getLevel() != null) {
            return pRaider.isAlive() && pRaider.canJoinRaid() && pRaider.getNoActionTime() <= 2400 && pRaider.level.dimensionType() == pRaid.getLevel().dimensionType();
        } else {
            return false;
        }
    }

    @Nullable
    public TheKnightOrder createOrExtendRaid(ServerPlayer pPlayer) {
        if (pPlayer.isSpectator()) {
            return null;
        } else if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        } else {
            DimensionType dimensiontype = pPlayer.level.dimensionType();
            if (!dimensiontype.hasRaids()) {
                return null;
            } else {
                BlockPos blockpos = pPlayer.blockPosition();
                List<PoiRecord> list = this.level.getPoiManager().getInRange((p_219845_) -> {
                    return p_219845_.is(PoiTypeTags.VILLAGE);
                }, blockpos, 64, PoiManager.Occupancy.IS_OCCUPIED).toList();
                int i = 0;
                Vec3 vec3 = Vec3.ZERO;

                for(PoiRecord poirecord : list) {
                    BlockPos blockpos2 = poirecord.getPos();
                    vec3 = vec3.add((double)blockpos2.getX(), (double)blockpos2.getY(), (double)blockpos2.getZ());
                    ++i;
                }

                BlockPos blockpos1;
                if (i > 0) {
                    vec3 = vec3.scale(1.0D / (double)i);
                    blockpos1 = new BlockPos(vec3);
                } else {
                    blockpos1 = blockpos;
                }

                TheKnightOrder raid = this.getOrCreateRaid(pPlayer.getLevel(), blockpos1);
                boolean flag = false;
                if (!raid.isStarted()) {
                    if (!this.raidMap.containsKey(raid.getId())) {
                        this.raidMap.put(raid.getId(), raid);
                    }

                    flag = true;
                }
                this.setDirty();
                return raid;
            }
        }
    }

    private TheKnightOrder getOrCreateRaid(ServerLevel pLevel, BlockPos pPos) {
        TheKnightOrder raid = this.getNearbyRaid(pPos, 9123);
        return raid != null ? raid : new TheKnightOrder(this.getUniqueId(), pLevel, pPos);
    }

    public static TheKnightOrders load(ServerLevel p_150236_, CompoundTag p_150237_) {
        TheKnightOrders raids = new TheKnightOrders(p_150236_);
        raids.nextAvailableID = p_150237_.getInt("NextAvailableID");
        raids.tick = p_150237_.getInt("Tick");
        ListTag listtag = p_150237_.getList("Raids", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            TheKnightOrder raid = new TheKnightOrder(p_150236_, compoundtag);
            raids.raidMap.put(raid.getId(), raid);
        }

        return raids;
    }
    public CompoundTag save(CompoundTag pCompound) {
        pCompound.putInt("NextAvailableID", this.nextAvailableID);
        pCompound.putInt("Tick", this.tick);
        ListTag listtag = new ListTag();

        for(TheKnightOrder raid : this.raidMap.values()) {
            CompoundTag compoundtag = new CompoundTag();
            raid.save(compoundtag);
            listtag.add(compoundtag);
        }

        pCompound.put("Raids", listtag);
        return pCompound;
    }

    public static String getFileId(Holder<DimensionType> p_211597_) {
        return p_211597_.is(BuiltinDimensionTypes.END) ? "raids_end" : "raids";
    }

    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public TheKnightOrder getNearbyRaid(BlockPos pPos, int pDistance) {
        TheKnightOrder raid = null;
        double d0 = (double)pDistance;

        for(TheKnightOrder raid1 : this.raidMap.values()) {
            double d1 = raid1.getCenter().distSqr(pPos);
            if (raid1.isActive() && d1 < d0) {
                raid = raid1;
                d0 = d1;
            }
        }

        return raid;
    }
}
