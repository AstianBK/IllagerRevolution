package net.BKTeam.illagerrevolutionmod.event;


import net.BKTeam.illagerrevolutionmod.entity.client.armor.*;
import net.BKTeam.illagerrevolutionmod.entity.layers.PlayerLikedLayer;
import net.BKTeam.illagerrevolutionmod.gui.HeartsEffect;
import net.BKTeam.illagerrevolutionmod.item.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.particle.custom.*;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import static net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes.*;
import static net.BKTeam.illagerrevolutionmod.particle.ModParticles.*;


@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.ILLAGERMINERBADLANDS.get(), IllagerScavengerEntity.setAttributes());
        event.put(ModEntityTypes.RAKER.get(), RakerEntity.setAttributes());
        event.put(ModEntityTypes.ILLAGERMINER.get(), IllagerMinerEntity.setAttributes());
        event.put(ModEntityTypes.ILLAGERBEASTTAMER.get(), IllagerBeastTamerEntity.setAttributes());
        event.put(ModEntityTypes.ZOMBIFIED.get(), ZombifiedEntity.setAttributes());
        event.put(ModEntityTypes.BLADE_KNIGHT.get(), BladeKnightEntity.setAttributes());
        event.put(ModEntityTypes.FALLEN_KNIGHT.get(), FallenKnightEntity.setAttributes());

    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        if(SMOKE_BK_PARTICLES.isPresent()){
            event.register(SMOKE_BK_PARTICLES.get(), BkSmokeParticles.Factory::new);
        }
        if (BKSOULS_PARTICLES.isPresent()){
            event.register(BKSOULS_PARTICLES.get(), BKSoulsParticles.Factory::new);
        }
        if(SOUL_PROJECTILE_PARTICLES.isPresent()){
            event.register(SOUL_PROJECTILE_PARTICLES.get(), SoulProjectilePParticles.Factory::new);
        }
        if (RUNE_CURSED_PARTICLES.isPresent()){
            event.register(RUNE_CURSED_PARTICLES.get(), RuneCursedParticles.Factory::new);
        }
        if(RUNE_SOUL_PARTICLES.isPresent()){
            event.register(RUNE_SOUL_PARTICLES.get(), RuneSoulParticles.Factory::new);
        }
        if(BLOOD_PARTICLES.isPresent()){
            event.register(BLOOD_PARTICLES.get(), BloodBKParticles.Factory::new);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void registerArmorRenderers(EntityRenderersEvent.AddLayers event){
        event.getSkins().forEach(s -> {
            event.getSkin(s).addLayer(new PlayerLikedLayer(event.getSkin(s)));
        });
        Minecraft.getInstance().getEntityRenderDispatcher().renderers.values().forEach(s->{
            if(s instanceof LivingEntityRenderer l){
                l.addLayer(new PlayerLikedLayer(l));
            }
        });
        GeoArmorRenderer.registerArmorRenderer(IllagiumArmorItem.class, HelmetMinerReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorGogglesItem.class, GogglesMinerReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorEvokerRobeItem.class, EvokerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorIllusionerRobeItem.class, IllusionerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorPillagerVestItem.class, PillagerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorVindicatorJacketItem.class, VindicatorPlayerArmorRenderer::new);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void registerGui(RegisterGuiOverlaysEvent event){
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "hearts",new HeartsEffect());
    }

    @SubscribeEvent
    public static void registerRulesSpawn(SpawnPlacementRegisterEvent event){
        event.register(ILLAGERMINER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ILLAGERMINERBADLANDS.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ILLAGERBEASTTAMER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
