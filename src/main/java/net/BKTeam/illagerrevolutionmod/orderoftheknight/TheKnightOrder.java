package net.BKTeam.illagerrevolutionmod.orderoftheknight;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.goals.KnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.goals.SpellcasterKnight;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TheKnightOrder {
    private static final Component RAID_NAME_COMPONENT = Component.translatable("event.illagerrevolutionmod.raid");
    private static final Component VICTORY = Component.translatable("event.illagerrevolutionmod.raid.victory");
    private static final Component DEFEAT = Component.translatable("event.illagerrevolutionmod.raid.defeat");
    private static final Component RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
    private static final Component RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);
    private final Map<Integer, Raider> groupToLeaderMap = Maps.newHashMap();
    private final Map<Integer, Set<KnightEntity>> groupRaiderMap = Maps.newHashMap();
    private final Set<UUID> defender = Sets.newHashSet();
    private long ticksActive;
    private BlockPos center;
    private final ServerLevel level;
    private boolean started;
    private final int id;
    private float totalHealth;
    private boolean active;
    private int groupsSpawned;
    private final ServerBossEvent raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10);
    private int postRaidTicks;
    private int raidCooldownTicks;
    private final RandomSource random = RandomSource.create();
    private final int numGroups;
    private TheKnightOrder.Status status;
    private int celebrationTicks;
    private Optional<BlockPos> waveSpawnPos = Optional.empty();

    public TheKnightOrder(int p_37692_, ServerLevel p_37693_, BlockPos p_37694_) {
        this.id = p_37692_;
        this.level = p_37693_;
        this.active = true;
        this.raidCooldownTicks = 300;
        this.raidEvent.setProgress(0.0F);
        this.center = p_37694_;
        this.numGroups = 5;
        this.status = Status.TOBEGIN;
    }
    public TheKnightOrder(ServerLevel p_37696_, CompoundTag p_37697_){
        this.level = p_37696_;
        this.id = p_37697_.getInt("Id");
        this.started = p_37697_.getBoolean("Started");
        this.active = p_37697_.getBoolean("Active");
        this.ticksActive = p_37697_.getLong("TicksActive");
        this.groupsSpawned = p_37697_.getInt("GroupsSpawned");
        this.raidCooldownTicks = p_37697_.getInt("PreRaidTicks");
        this.postRaidTicks = p_37697_.getInt("PostRaidTicks");
        this.totalHealth = p_37697_.getFloat("TotalHealth");
        this.center = new BlockPos(p_37697_.getInt("CX"), p_37697_.getInt("CY"), p_37697_.getInt("CZ"));
        this.numGroups = p_37697_.getInt("NumGroups");
        this.status = TheKnightOrder.Status.getByName(p_37697_.getString("Status"));
        this.defender.clear();
        if (p_37697_.contains("Defender", 9)) {
            ListTag listtag = p_37697_.getList("Defender", 11);

            for(int i = 0; i < listtag.size(); ++i) {
                this.defender.add(NbtUtils.loadUUID(listtag.get(i)));
            }
        }

    }

    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }

    public boolean isBetweenWaves() {
        return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
    }

    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }

    public boolean isStopped() {
        return this.status == Status.STOPPED;
    }

    public boolean isVictory() {
        return this.status == Status.VICTORY;
    }

    public boolean isLoss() {
        return this.status == Status.DEFEAT;
    }

    public float getTotalHealth() {
        return this.totalHealth;
    }

    public Set<KnightEntity> getAllRaiders() {
        Set<KnightEntity> set = Sets.newHashSet();

        for(Set<KnightEntity> set1 : this.groupRaiderMap.values()) {
            set.addAll(set1);
        }

        return set;
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void stop() {
        this.active = false;
        this.raidEvent.removeAllPlayers();
        this.status = Status.STOPPED;
    }

    public void tick() {
        if (!this.isStopped()) {
            if (this.status == Status.TOBEGIN) {
                boolean flag = this.active;
                this.active = this.level.hasChunkAt(this.center);
                if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                    this.stop();
                    return;
                }

                if (flag != this.active) {
                    this.raidEvent.setVisible(this.active);
                }

                if (!this.active) {
                    return;
                }

                ++this.ticksActive;
                if (this.ticksActive >= 48000L) {
                    this.stop();
                    return;
                }

                int i = this.getTotalRaidersAlive();
                if (i == 0 && this.hasMoreWaves()) {
                    if (this.raidCooldownTicks <= 0) {
                        if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                            this.raidCooldownTicks = 300;
                            this.raidEvent.setName(RAID_NAME_COMPONENT);
                            return;
                        }
                    } else {
                        boolean flag1 = this.waveSpawnPos.isPresent();
                        boolean flag2 = !flag1 && this.raidCooldownTicks % 5 == 0;
                        if (flag1 && !this.level.isPositionEntityTicking(this.waveSpawnPos.get())) {
                            flag2 = true;
                        }

                        if (flag2) {
                            int j = 0;
                            if (this.raidCooldownTicks < 100) {
                                j = 1;
                            } else if (this.raidCooldownTicks < 40) {
                                j = 2;
                            }

                            this.waveSpawnPos = this.getValidSpawnPos(j);
                        }

                        if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                            this.updatePlayers();
                        }

                        --this.raidCooldownTicks;
                        this.raidEvent.setProgress(Mth.clamp((float)(300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
                    }
                }

                if (this.ticksActive % 20L == 0L) {
                    this.updatePlayers();
                    this.updateRaiders();
                    if (i > 0) {
                        if (i <= 2) {
                            this.raidEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", i)));
                        } else {
                            this.raidEvent.setName(RAID_NAME_COMPONENT);
                        }
                    } else {
                        this.raidEvent.setName(RAID_NAME_COMPONENT);
                    }
                }

                boolean flag3 = false;
                int k = 0;

                while(this.shouldSpawnGroup()) {
                    BlockPos blockpos = this.waveSpawnPos.isPresent() ? this.waveSpawnPos.get() : this.findRandomSpawnPos(k, 20);
                    if (blockpos != null) {
                        this.started = true;
                        this.spawnGroup(blockpos);
                        if (!flag3) {
                            this.playSound(blockpos);
                            flag3 = true;
                        }
                    } else {
                        ++k;
                    }

                    if (k > 3) {
                        this.stop();
                        break;
                    }
                }

                if (this.isStarted() && !this.hasMoreWaves() && i == 0) {
                    if (this.postRaidTicks < 40) {
                        ++this.postRaidTicks;
                    } else {
                        this.status = Status.VICTORY;

                        for(UUID uuid : this.defender) {
                            Entity entity = this.level.getEntity(uuid);
                            if (entity instanceof LivingEntity && !entity.isSpectator()) {
                                LivingEntity livingentity = (LivingEntity)entity;
                                livingentity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, 2, false, false, true));
                                if (livingentity instanceof ServerPlayer) {
                                    ServerPlayer serverplayer = (ServerPlayer)livingentity;
                                    serverplayer.awardStat(Stats.RAID_WIN);
                                    CriteriaTriggers.RAID_WIN.trigger(serverplayer);
                                }
                            }
                        }
                    }
                }
                this.setDirty();
            } else if (this.isOver()) {
                ++this.celebrationTicks;
                //System.out.print("\n se termino la raid");
                if (this.celebrationTicks >= 600) {
                    this.stop();
                    return;
                }
                this.raidEvent.setVisible(true);
                if (this.isVictory()) {
                    this.raidEvent.setProgress(0.0F);
                    this.raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
                } else {
                    this.raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
                }
            }

        }
    }

    private Optional<BlockPos> getValidSpawnPos(int p_37764_) {
        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = this.findRandomSpawnPos(p_37764_, 1);
            if (blockpos != null) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    private void updatePlayers() {
        Set<ServerPlayer> set = Sets.newHashSet(this.raidEvent.getPlayers());
        List<ServerPlayer> list = this.level.getPlayers(this.validPlayer());

        for(ServerPlayer serverplayer : list) {
            if (!set.contains(serverplayer)) {
                this.raidEvent.addPlayer(serverplayer);
            }
        }

        for(ServerPlayer serverplayer1 : set) {
            if (!list.contains(serverplayer1)) {
                this.raidEvent.removePlayer(serverplayer1);
            }
        }

    }

    private Predicate<ServerPlayer> validPlayer() {
        return (p_37723_) -> {
            ServerLevel level1 = p_37723_.getLevel();
            BlockPos pos = p_37723_.getOnPos();
            return p_37723_.isAlive() && IllagerRevolutionMod.getTheOrders(level1).getNearbyRaid(pos,9612) == this;
        };
    }

    private boolean hasMoreWaves() {
        if (this.hasBonusWave()) {
            return !this.hasSpawnedBonusWave();
        } else {
            return !this.isFinalWave();
        }
    }

    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }

    private boolean isFinalWave() {
        return this.getGroupsSpawned() == this.numGroups;
    }

    private boolean hasBonusWave() {
        return false;
    }

    private boolean hasSpawnedBonusWave() {
        return this.getGroupsSpawned() > this.numGroups;
    }

    private boolean shouldSpawnBonusGroup() {
        return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
    }

    private void updateRaiders() {
        Iterator<Set<KnightEntity>> iterator = this.groupRaiderMap.values().iterator();
        Set<KnightEntity> set = Sets.newHashSet();

        while(iterator.hasNext()) {
            Set<KnightEntity> set1 = iterator.next();

            for(KnightEntity raider : set1) {
                BlockPos blockpos = raider.blockPosition();
                if (!raider.isRemoved() && raider.level.dimension() == this.level.dimension() && !(this.center.distSqr(blockpos) >= 12544.0D)) {
                    if (raider.tickCount > 600) {
                        if (this.level.getEntity(raider.getUUID()) == null) {
                            set.add(raider);
                        }

                        if (!this.level.isVillage(blockpos) && raider.getNoActionTime() > 2400) {
                            raider.setTicksOutsideRaid(raider.getTicksOutsideRaid() + 1);
                        }

                        if (raider.getTicksOutsideRaid() >= 30) {
                            set.add(raider);
                        }
                    }
                } else {
                    set.add(raider);
                }
            }
        }

        for(KnightEntity raider1 : set) {
            this.removeFromRaid(raider1, true);
        }

    }

    private void playSound(BlockPos p_37744_) {
        float f = 13.0F;
        int i = 64;
        Collection<ServerPlayer> collection = this.raidEvent.getPlayers();
        long j = this.random.nextLong();

        for(ServerPlayer serverplayer : this.level.players()) {
            Vec3 vec3 = serverplayer.position();
            Vec3 vec31 = Vec3.atCenterOf(p_37744_);
            double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z));
            double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x);
            double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z);
            if (d0 <= 64.0D || collection.contains(serverplayer)) {
                serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, 64.0F, 1.0F, j));
            }
        }

    }

    private void spawnGroup(BlockPos p_37756_) {
        boolean flag = false;
        int i = this.groupsSpawned + 1;
        this.totalHealth = 0.0F;
        DifficultyInstance difficultyinstance = this.level.getCurrentDifficultyAt(p_37756_);
        boolean flag1 = this.shouldSpawnBonusGroup();

        for(KnightType knightType : KnightType.VALUES) {
            int j = this.getDefaultNumSpawns(knightType, i, flag1) + this.getPotentialBonusSpawns(knightType, this.random, i, difficultyinstance, flag1);
            int k = 0;

            for(int l = 0; l < j; ++l) {
                KnightEntity raider = knightType.entityType.create(this.level);
                this.joinRaid(i, raider, p_37756_, false);
            }
        }
        if(i==this.numGroups){
            int k =this.level.random.nextInt(0,2);
            for(BossType bossType : BossType.VALUES){
                KnightEntity boss = bossType.entityType.create(this.level);
                this.joinRaid(i,boss,p_37756_,false);
            }
        }
        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;
        this.updateBossbar();
        this.setDirty();
    }

    public void joinRaid(int pWave, KnightEntity p_37715_, @Nullable BlockPos p_37716_, boolean p_37717_) {
        boolean flag = this.addWaveMob(pWave, p_37715_);
        if (flag) {
            p_37715_.setRaidOfOrder(this);
            p_37715_.setWaveOfOrder(pWave);
            p_37715_.setCanJoinRaidOfTheOrder(true);
            p_37715_.setTicksOutsideRaidTheOrder(0);
            if (!p_37717_ && p_37716_ != null) {
                p_37715_.setPos((double)p_37716_.getX() + 0.5D, (double)p_37716_.getY() + 1.0D, (double)p_37716_.getZ() + 0.5D);
                p_37715_.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(p_37716_), MobSpawnType.EVENT, (SpawnGroupData)null, (CompoundTag)null);
                p_37715_.setOnGround(true);
                this.level.addFreshEntityWithPassengers(p_37715_);
            }
        }

    }

    public void updateBossbar() {
        this.raidEvent.setProgress(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
    }

    public float getHealthOfLivingRaiders() {
        float f = 0.0F;

        for(Set<KnightEntity> set : this.groupRaiderMap.values()) {
            for(KnightEntity raider : set) {
                f += raider.getHealth();
            }
        }
        return f;
    }

    private boolean shouldSpawnGroup() {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
    }

    public int getTotalRaidersAlive() {
        return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
    }

    public void removeFromRaid(KnightEntity p_37741_, boolean p_37742_) {
        Set<KnightEntity> set = this.groupRaiderMap.get(p_37741_.getWaveOfTheOrder());
        if (set != null) {
            boolean flag = set.remove(p_37741_);
            if (flag) {
                if (p_37742_) {
                    this.totalHealth -= p_37741_.getHealth();
                }

                p_37741_.setRaidOfOrder((TheKnightOrder) null);
                this.updateBossbar();
                this.setDirty();
            }
        }

    }
    @Nullable
    private BlockPos findRandomSpawnPos(int p_37708_, int p_37709_) {
        int i = p_37708_ == 0 ? 2 : 2 - p_37708_;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int i1 = 0; i1 < p_37709_; ++i1) {
            float f = this.level.random.nextFloat() * ((float)Math.PI * 2F);
            int j = this.center.getX() + Mth.floor(Mth.cos(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
            int l = this.center.getZ() + Mth.floor(Mth.sin(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
            int k = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, j, l);
            blockpos$mutableblockpos.set(j, k, l);
            if (!this.level.isVillage(blockpos$mutableblockpos) || p_37708_ >= 2) {
                int j1 = 10;
                if (this.level.hasChunksAt(blockpos$mutableblockpos.getX() - 10, blockpos$mutableblockpos.getZ() - 10, blockpos$mutableblockpos.getX() + 10, blockpos$mutableblockpos.getZ() + 10) && this.level.isPositionEntityTicking(blockpos$mutableblockpos) && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, blockpos$mutableblockpos, EntityType.RAVAGER) || this.level.getBlockState(blockpos$mutableblockpos.below()).is(Blocks.SNOW) && this.level.getBlockState(blockpos$mutableblockpos).isAir())) {
                    return blockpos$mutableblockpos;
                }
            }
        }

        return null;
    }

    private boolean addWaveMob(int p_37753_, KnightEntity p_37754_) {
        return this.addWaveMob(p_37753_, p_37754_, true);
    }

    public boolean addWaveMob(int p_37719_, KnightEntity p_37720_, boolean p_37721_) {
        this.groupRaiderMap.computeIfAbsent(p_37719_, (p_37746_) -> {
            return Sets.newHashSet();
        });
        Set<KnightEntity> set = this.groupRaiderMap.get(p_37719_);
        KnightEntity raider = null;

        for(KnightEntity raider1 : set) {
            if (raider1.getUUID().equals(p_37720_.getUUID())) {
                raider = raider1;
                break;
            }
        }

        if (raider != null) {
            set.remove(raider);
            set.add(p_37720_);
        }

        set.add(p_37720_);
        if (p_37721_) {
            this.totalHealth += p_37720_.getHealth();
        }

        this.updateBossbar();
        this.setDirty();
        return true;
    }
    public BlockPos getCenter() {
        return this.center;
    }

    private void setCenter(BlockPos p_37761_) {
        this.center = p_37761_;
    }

    public int getId() {
        return this.id;
    }

    private int getDefaultNumSpawns(KnightType p_37731_, int p_37732_, boolean p_37733_) {
        return p_37733_ ? p_37731_.spawnsPerWaveBeforeBonus[this.numGroups] : p_37731_.spawnsPerWaveBeforeBonus[p_37732_];
    }

    private int getPotentialBonusSpawns(KnightType p_219829_, RandomSource p_219830_, int p_219831_, DifficultyInstance p_219832_, boolean p_219833_) {
        Difficulty difficulty = p_219832_.getDifficulty();
        boolean flag = difficulty == Difficulty.EASY;
        boolean flag1 = difficulty == Difficulty.NORMAL;

        return 1;
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundTag save(CompoundTag pNbt) {
        pNbt.putInt("Id", this.id);
        pNbt.putBoolean("Started", this.started);
        pNbt.putBoolean("Active", this.active);
        pNbt.putLong("TicksActive", this.ticksActive);
        pNbt.putInt("GroupsSpawned", this.groupsSpawned);
        pNbt.putInt("PreRaidTicks", this.raidCooldownTicks);
        pNbt.putInt("PostRaidTicks", this.postRaidTicks);
        pNbt.putFloat("TotalHealth", this.totalHealth);
        pNbt.putInt("NumGroups", this.numGroups);
        pNbt.putString("Status", this.status.getName());
        pNbt.putInt("CX", this.center.getX());
        pNbt.putInt("CY", this.center.getY());
        pNbt.putInt("CZ", this.center.getZ());
        ListTag listtag = new ListTag();

        for(UUID uuid : this.defender) {
            listtag.add(NbtUtils.createUUID(uuid));
        }

        pNbt.put("Defender", listtag);
        return pNbt;
    }

    public int getNumGroups(Difficulty pDifficulty) {
        switch (pDifficulty) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }



    public void addDefender(Entity p_37727_) {
        this.defender.add(p_37727_.getUUID());
    }

    private void setDirty() {
        this.level.getRaids().setDirty();
    }

    public enum Status {
        TOBEGIN,
        VICTORY,
        DEFEAT,
        STOPPED;

        private static final TheKnightOrder.Status[] VALUES = values();

        static TheKnightOrder.Status getByName(String pName) {
            for(TheKnightOrder.Status raid$raidstatus : VALUES) {
                if (pName.equalsIgnoreCase(raid$raidstatus.name())) {
                    return raid$raidstatus;
                }
            }

            return TOBEGIN;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static enum KnightType implements net.minecraftforge.common.IExtensibleEnum {
        BLADE_KNIGHT(ModEntityTypes.BLADE_KNIGHT.get(), new int[]{1, 0, 1, 2, 0, 2, 2, 2}),
        SOUL_SAGE(ModEntityTypes.SOUL_SAGE.get(), new int[]{0,1, 1, 0, 2, 2, 2, 2}),
        ACOLYTE(ModEntityTypes.ACOLYTE.get(), new int[]{5, 5, 6, 6, 7, 8, 9, 9});
        static KnightType[] VALUES = values();
        final EntityType<? extends SpellcasterKnight> entityType;
        final int[] spawnsPerWaveBeforeBonus;

        private KnightType(EntityType<? extends SpellcasterKnight> p_37821_, int[] p_37822_) {
            this.entityType = p_37821_;
            this.spawnsPerWaveBeforeBonus = p_37822_;
        }

        public static KnightType create(String name, EntityType<? extends SpellcasterKnight> typeIn, int[] waveCountsIn) {
            throw new IllegalStateException("Enum not extended");
        }

        @Override
        @Deprecated
        public void init() {
            VALUES = values();
        }
    }

    public static enum BossType implements net.minecraftforge.common.IExtensibleEnum {
        SHIELD_MASTER(ModEntityTypes.ILLAGER_BEAST_TAMER.get());

        static BossType[] VALUES = values();
        final EntityType<? extends SpellcasterKnight> entityType;

        private BossType(EntityType<? extends SpellcasterKnight> p_37821_) {
            this.entityType = p_37821_;
        }

        public static BossType create(String name, EntityType<? extends SpellcasterKnight> typeIn) {
            throw new IllegalStateException("Enum not extended");
        }
        @Override
        @Deprecated
        public void init() {
            VALUES = values();
        }
    }
}
