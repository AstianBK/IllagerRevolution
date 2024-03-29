package net.BKTeam.illagerrevolutionmod.event;


import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.armor.*;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulBombModel;
import net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers.ArrowBeastRender;
import net.BKTeam.illagerrevolutionmod.entity.layers.DrumModel;
import net.BKTeam.illagerrevolutionmod.entity.layers.GeckoLivingProtectionLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.LivingProtectionLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.PlayerLikedLayer;
import net.BKTeam.illagerrevolutionmod.gui.BKGui;
import net.BKTeam.illagerrevolutionmod.gui.HeartsEffect;
import net.BKTeam.illagerrevolutionmod.item.custom.*;
import net.BKTeam.illagerrevolutionmod.particle.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import static net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes.*;
import static net.BKTeam.illagerrevolutionmod.particle.ModParticles.*;


@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ModEventBusEvents {
    public static ModelLayerLocation DRUM = new ModelLayerLocation(
            new ResourceLocation(IllagerRevolutionMod.MOD_ID, "drum"), "drum");
    public static ModelLayerLocation ORB = new ModelLayerLocation(
            new ResourceLocation(IllagerRevolutionMod.MOD_ID, "orb"), "orb");


    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        if(SMOKE_BK_PARTICLES.isPresent()){
            manager.register(SMOKE_BK_PARTICLES.get(), BkSmokeParticles.Factory::new);
        }
        if(HEART_BK_PARTICLES.isPresent()){
            manager.register(HEART_BK_PARTICLES.get(), BkHeartParticles.Factory::new);
        }
        if (BKSOULS_PARTICLES.isPresent()){
            manager.register(BKSOULS_PARTICLES.get(), BKSoulsParticles.Factory::new);
        }
        if(SOUL_PROJECTILE_PARTICLES.isPresent()){
            manager.register(SOUL_PROJECTILE_PARTICLES.get(), SoulProjectilePParticles.Factory::new);
        }
        if(SOUL_FLAME.isPresent()){
            manager.register(SOUL_FLAME.get(), BKFireSoulParticles.Factory::new);
        }
        if (RUNE_CURSED_PARTICLES.isPresent()){
            manager.register(RUNE_CURSED_PARTICLES.get(), RuneCursedParticles.Factory::new);
        }
        if(RUNE_SOUL_PARTICLES.isPresent()){
            manager.register(RUNE_SOUL_PARTICLES.get(), RuneSoulParticles.Factory::new);
        }
        if(BLOOD_PARTICLES.isPresent()){
            manager.register(BLOOD_PARTICLES.get(), BloodBKParticles.Factory::new);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void registerArmorRenderers(EntityRenderersEvent.AddLayers event){
        event.getSkins().forEach(s -> {
            event.getSkin(s).addLayer(new PlayerLikedLayer(event.getSkin(s)));
            event.getSkin(s).addLayer(new LivingProtectionLayer(event.getSkin(s)));
        });
        Minecraft.getInstance().getEntityRenderDispatcher().renderers.values().forEach(s->{
            if(s instanceof LivingEntityRenderer<?,?> l){
                l.addLayer(new PlayerLikedLayer(l));
                l.addLayer(new LivingProtectionLayer(l));
            }
            if(s instanceof GeoEntityRenderer<?> l){
                l.addLayer(new GeckoLivingProtectionLayer(l));
            }
        });
        GeoArmorRenderer.registerArmorRenderer(IllagiumArmorItem.class, HelmetMinerReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorGogglesItem.class, GogglesMinerReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorEvokerRobeItem.class, EvokerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorIllusionerRobeItem.class, IllusionerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorPillagerVestItem.class, PillagerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorVindicatorJacketItem.class, VindicatorPlayerArmorRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ARROWBEAST.get(), ArrowBeastRender::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DRUM, DrumModel::createBodyLayer);
        event.registerLayerDefinition(ORB, SoulBombModel::createBodyLayer);
    }
}
