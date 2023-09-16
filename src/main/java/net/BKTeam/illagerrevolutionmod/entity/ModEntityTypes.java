package net.BKTeam.illagerrevolutionmod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.entity.projectile.*;


public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IllagerRevolutionMod.MOD_ID);

    public static final RegistryObject<EntityType<IllagerScavengerEntity>> ILLAGER_SCAVENGER =
            ENTITY_TYPES.register("illager_scavenger",
                    () -> EntityType.Builder.of(IllagerScavengerEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "illager_scavenger").toString()));

    public static final RegistryObject<EntityType<IllagerMinerEntity>> ILLAGER_MINER =
            ENTITY_TYPES.register("illager_miner",
                    () -> EntityType.Builder.of(IllagerMinerEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "illager_miner").toString()));

    public static final RegistryObject<EntityType<ScroungerEntity>> SCROUNGER =
            ENTITY_TYPES.register("scrounger",
                    () -> EntityType.Builder.of(ScroungerEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 1.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "scrounger").toString()));

    public static final RegistryObject<EntityType<RakerEntity>> RAKER = ENTITY_TYPES.register("raker",
            ()-> EntityType.Builder.of(RakerEntity::new, MobCategory.MONSTER)
                .sized(0.65f,1.3f).
                    build(new ResourceLocation(IllagerRevolutionMod.MOD_ID,"raker")
                        .toString()));

    public static final RegistryObject<EntityType<MaulerEntity>> MAULER = ENTITY_TYPES.register("mauler",
            ()-> EntityType.Builder.of(MaulerEntity::new, MobCategory.MONSTER)
                    .sized(1.5f,1.6f).
                    build(new ResourceLocation(IllagerRevolutionMod.MOD_ID,"mauler")
                            .toString()));


    public static final RegistryObject<EntityType<WildRavagerEntity>> WILD_RAVAGER = ENTITY_TYPES.register("wild_ravager",
            ()-> EntityType.Builder.of(WildRavagerEntity::new, MobCategory.MONSTER)
                    .sized(1.95f,2.2f).
                    build(new ResourceLocation(IllagerRevolutionMod.MOD_ID,"wild_ravager")
                            .toString()));


    public static final RegistryObject<EntityType<IllagerBeastTamerEntity>> ILLAGER_BEAST_TAMER =
            ENTITY_TYPES.register("illager_beast_tamer",
                    () -> EntityType.Builder.of(IllagerBeastTamerEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "illager_beast_tamer").toString()));

    public static final RegistryObject<EntityType<ZombifiedEntity>> ZOMBIFIED =
            ENTITY_TYPES.register("zombified",
                    () -> EntityType.Builder.of(ZombifiedEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.5f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "zombified").toString()));

    public static final RegistryObject<EntityType<FallenKnightEntity>> FALLEN_KNIGHT =
            ENTITY_TYPES.register("fallen_knight",
                    () -> EntityType.Builder.of(FallenKnightEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.5f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "fallen_knight").toString()));

    public static final RegistryObject<EntityType<BladeKnightEntity>> BLADE_KNIGHT =
            ENTITY_TYPES.register("blade_knight",
                    () -> EntityType.Builder.of(BladeKnightEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.4f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "blade_knight").toString()));


    public static final RegistryObject<EntityType<SoulSageEntity>> SOUL_SAGE =
            ENTITY_TYPES.register("soul_sage",
                    () -> EntityType.Builder.of(SoulSageEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.4f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "soul_sage").toString()));


    public static final RegistryObject<EntityType<SoulSlash>> SOUL_SLASH = ENTITY_TYPES
            .register("soul_slash", () -> EntityType.Builder.<SoulSlash>of(SoulSlash::new, MobCategory.MISC)
                    .fireImmune().sized(3.0F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_slash"));


    public static final RegistryObject<EntityType<SoulProjectile>> SOUL_PROJECTILE = ENTITY_TYPES
            .register("soul_projectile", () -> EntityType.Builder.<SoulProjectile>of(SoulProjectile::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_projectile"));

    public static final RegistryObject<EntityType<SoulBomb>> SOUL_BOMB = ENTITY_TYPES
            .register("soul_bomb", () -> EntityType.Builder.<SoulBomb>of(SoulBomb::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_bomb"));

    public static final RegistryObject<EntityType<SoulMissile>> SOUL_MISSILE = ENTITY_TYPES
            .register("soul_missile", () -> EntityType.Builder.<SoulMissile>of(SoulMissile::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_missile"));


    public static final RegistryObject<EntityType<SoulHunter>> SOUL_HUNTER = ENTITY_TYPES
            .register("soul_hunter", () -> EntityType.Builder.<SoulHunter>of(SoulHunter::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_hunter"));

    public static final RegistryObject<EntityType<SummonedSoul>> SUMMONED_SOUL = ENTITY_TYPES
            .register("summoned_soul", () -> EntityType.Builder.<SummonedSoul>of(SummonedSoul::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "summoned_soul"));

    public static final RegistryObject<EntityType<AreaFireColumnEntity>> AREA_FIRE_COLUMN = ENTITY_TYPES
            .register("area_fire_column", () -> EntityType.Builder.<AreaFireColumnEntity>of(AreaFireColumnEntity::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "area_fire_column"));

    public static final RegistryObject<EntityType<SoulEntity>> SOUL_ENTITY = ENTITY_TYPES
            .register("soul_entity", () -> EntityType.Builder.<SoulEntity>of(SoulEntity::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_entity"));

    public static final RegistryObject<EntityType<ArrowBeast>> ARROWBEAST = ENTITY_TYPES
            .register("arrow_beast", () -> EntityType.Builder.<ArrowBeast>of(ArrowBeast::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "arrow_beast"));

    public static final RegistryObject<EntityType<FeatherProjectile>> FEATHER_PROJECTILE = ENTITY_TYPES
            .register("feather_projectile", () -> EntityType.Builder.<FeatherProjectile>of(FeatherProjectile::new, MobCategory.MISC)
                    .fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "feather_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    public static void registerWaveMembers() {
        Raid.RaiderType.create("blade_knight", BLADE_KNIGHT.get(), new int[]{0, 0, 0, 0, 1, 0, 0, 1});
        Raid.RaiderType.create("illager_beast_tamer", ILLAGER_BEAST_TAMER.get(), new int[]{0, 0, 1, 0, 2, 0, 2, 3});
        Raid.RaiderType.create("illager_scavenger", ILLAGER_SCAVENGER.get(),new int[]{1, 1 ,0, 2, 3, 0, 3, 4});
    }
}