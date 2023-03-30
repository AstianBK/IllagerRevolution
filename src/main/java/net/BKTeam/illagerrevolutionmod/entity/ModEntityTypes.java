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
            DeferredRegister.create(ForgeRegistries.ENTITIES, IllagerRevolutionMod.MOD_ID);

    public static final RegistryObject<EntityType<IllagerMinerBadlandsEntity>> ILLAGERMINERBADLANDS =
            ENTITY_TYPES.register("illagerminerbadlands",
                    () -> EntityType.Builder.of(IllagerMinerBadlandsEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "illagerminerbadlands").toString()));

    public static final RegistryObject<EntityType<IllagerMinerEntity>> ILLAGERMINER =
            ENTITY_TYPES.register("illagerminer",
                    () -> EntityType.Builder.of(IllagerMinerEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "illagerminer").toString()));

    public static final RegistryObject<EntityType<RakerEntity>> RAKER = ENTITY_TYPES.register("raker",
            ()-> EntityType.Builder.of(RakerEntity::new, MobCategory.MONSTER)
                .sized(0.65f,1.0f).
                    build(new ResourceLocation(IllagerRevolutionMod.MOD_ID,"raker")
                        .toString()));



    public static final RegistryObject<EntityType<IllagerBeastTamerEntity>> ILLAGERBEASTTAMER =
            ENTITY_TYPES.register("illagerbeasttamer",
                    () -> EntityType.Builder.of(IllagerBeastTamerEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.0f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "illagerbeasttamer").toString()));



    public static final RegistryObject<EntityType<ZombifiedEntity>> ZOMBIFIED =
            ENTITY_TYPES.register("zombified",
                    () -> EntityType.Builder.of(ZombifiedEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.5f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "zombified").toString()));

    public static final RegistryObject<EntityType<FallenKnight>> FALLEN_KNIGHT =
            ENTITY_TYPES.register("fallen_knight",
                    () -> EntityType.Builder.of(FallenKnight::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.5f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "fallen_knight").toString()));


    public static final RegistryObject<EntityType<Blade_KnightEntity>> BLADE_KNIGHT =
            ENTITY_TYPES.register("blade_knight",
                    () -> EntityType.Builder.of(Blade_KnightEntity::new, MobCategory.MONSTER)
                            .sized(0.60f, 2.4f)
                            .build(new ResourceLocation(IllagerRevolutionMod.MOD_ID, "blade_knight").toString()));

    public static final RegistryObject<EntityType<Soul_Projectile>> SOUL_PROJECTILE = ENTITY_TYPES.register("soul_projectile", () -> EntityType.Builder.<Soul_Projectile>of(Soul_Projectile::new, MobCategory.MISC).fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_projectile"));

    public static final RegistryObject<EntityType<Soul_Hunter>> SOUL_HUNTER = ENTITY_TYPES.register("soul_hunter", () -> EntityType.Builder.<Soul_Hunter>of(Soul_Hunter::new, MobCategory.MISC).fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_hunter"));

    public static final RegistryObject<EntityType<Summoned_Soul>> SUMMONED_SOUL = ENTITY_TYPES.register("summoned_soul", () -> EntityType.Builder.<Summoned_Soul>of(Summoned_Soul::new, MobCategory.MISC).fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "summoned_soul"));

    public static final RegistryObject<EntityType<Soul_Entity>> SOUL_ENTITY = ENTITY_TYPES.register("soul_entity", () -> EntityType.Builder.<Soul_Entity>of(Soul_Entity::new, MobCategory.MISC).fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "soul_entity"));

    public static final RegistryObject<EntityType<ArrowBeast>> ARROWBEAST = ENTITY_TYPES.register("arrowbeast", () -> EntityType.Builder.<ArrowBeast>of(ArrowBeast::new, MobCategory.MISC).fireImmune().sized(0.2F, 0.2F).build(IllagerRevolutionMod.MOD_ID + "arrowbeast"));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    public static void registerWaveMembers() {
        Raid.RaiderType.create("blade_knight", BLADE_KNIGHT.get(), new int[]{0, 0, 0, 0, 1, 0, 0, 1});
        Raid.RaiderType.create("illagerbeasttamer", ILLAGERBEASTTAMER.get(), new int[]{0, 0, 1, 0, 2, 0, 2, 3});
    }
}
