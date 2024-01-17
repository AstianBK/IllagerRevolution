package net.BKTeam.illagerrevolutionmod.deathentitysystem;

import net.minecraftforge.common.ForgeConfigSpec;

public class SoulConfig {
    public static ForgeConfigSpec.IntValue CHUNK_MIN_SOUL;
    public static ForgeConfigSpec.IntValue CHUNK_MAX_SOUL;
    
    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for the soul system").push("soul");

        CHUNK_MIN_SOUL = SERVER_BUILDER
                .comment("Minumum amount of soul in a chunk")
                .defineInRange("minsoul", 10, 0, Integer.MAX_VALUE);
        CHUNK_MAX_SOUL = SERVER_BUILDER
                .comment("Maximum amount of soul in a chunk (relative to minsoul)")
                .defineInRange("maxsoul", 100, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }
}
