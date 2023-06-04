package net.BKTeam.illagerrevolutionmod.block.entity;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.block.ModBlocks;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.RuneTableEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, IllagerRevolutionMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<RuneTableEntity>> RUNE_TABLE_ENTITY =
            BLOCK_ENTITIES.register("rune_table_entity", () ->
                    BlockEntityType.Builder.of(RuneTableEntity::new,
                            ModBlocks.RUNE_TABLE_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
    }
}
