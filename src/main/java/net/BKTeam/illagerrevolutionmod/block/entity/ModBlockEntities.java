package net.BKTeam.illagerrevolutionmod.block.entity;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.block.ModBlocks;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.DrumBlockDamageEntity;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.DrumBlockHealEntity;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.DrumBlockSpeedEntity;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.RuneTableEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, IllagerRevolutionMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<RuneTableEntity>> RUNE_TABLE_ENTITY =
            BLOCK_ENTITIES.register("rune_table_entity", () ->
                    BlockEntityType.Builder.of(RuneTableEntity::new,
                            ModBlocks.RUNE_TABLE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<DrumBlockSpeedEntity>> DRUM_ENTITY_SPEED =
            BLOCK_ENTITIES.register("drum_speed_entity", () ->
                    BlockEntityType.Builder.of(DrumBlockSpeedEntity::new,
                            ModBlocks.DRUM_SPEED.get()).build(null));

    public static final RegistryObject<BlockEntityType<DrumBlockHealEntity>> DRUM_ENTITY_HEAL =
            BLOCK_ENTITIES.register("drum_heal_entity", () ->
                    BlockEntityType.Builder.of(DrumBlockHealEntity::new,
                            ModBlocks.DRUM_HEAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<DrumBlockDamageEntity>> DRUM_ENTITY_DAMAGE =
            BLOCK_ENTITIES.register("drum_damage_entity", () ->
                    BlockEntityType.Builder.of(DrumBlockDamageEntity::new,
                            ModBlocks.DRUM_DAMAGE.get()).build(null));

    public static void register(IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
    }
}
