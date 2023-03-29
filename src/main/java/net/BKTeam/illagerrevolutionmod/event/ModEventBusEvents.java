package net.BKTeam.illagerrevolutionmod.event;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.client.armor.Goggles_Miner_ReinforcedRenderer;
import net.BKTeam.illagerrevolutionmod.entity.client.armor.Helmet_Miner_ReinforcedRenderer;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorGogglesItem;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import net.BKTeam.illagerrevolutionmod.particle.custom.*;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.function.Supplier;

import static net.BKTeam.illagerrevolutionmod.particle.ModParticles.*;


@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {


    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.ILLAGERMINERBADLANDS.get(), IllagerMinerBadlandsEntity.setAttributes());
        event.put(ModEntityTypes.RAKER.get(), RakerEntity.setAttributes());
        event.put(ModEntityTypes.ILLAGERMINER.get(), IllagerMinerEntity.setAttributes());
        event.put(ModEntityTypes.ILLAGERBEASTTAMER.get(), IllagerBeastTamerEntity.setAttributes());
        event.put(ModEntityTypes.ZOMBIFIED.get(), ZombifiedEntity.setAttributes());
        event.put(ModEntityTypes.BLADE_KNIGHT.get(), Blade_KnightEntity.setAttributes());

    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        if(SMOKE_BK_PARTICLES.isPresent()){
            manager.register(SMOKE_BK_PARTICLES.get(), Bk_SmokeParticles.Factory::new);
        }
        if (BKSOULS_PARTICLES.isPresent()){
            manager.register(BKSOULS_PARTICLES.get(), BKSoulsParticles.Factory::new);
        }
        if(SOUL_PROJECTILE_PARTICLES.isPresent()){
            manager.register(SOUL_PROJECTILE_PARTICLES.get(), Soul_ProjectilePParticles.Factory::new);
        }
        if (RUNE_CURSED_PARTICLES.isPresent()){
            manager.register(RUNE_CURSED_PARTICLES.get(), Rune_CursedParticles.Factory::new);
        }
        if(RUNE_SOUL_PARTICLES.isPresent()){
            manager.register(RUNE_SOUL_PARTICLES.get(), Rune_SoulParticles.Factory::new);
        }
        if(BLOOD_PARTICLES.isPresent()){
            manager.register(BLOOD_PARTICLES.get(), BloodBK_Particles.Factory::new);
        }
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerArmorRenderers(final EntityRenderersEvent.AddLayers event){
        GeoArmorRenderer.registerArmorRenderer(IllagiumArmorItem.class, Helmet_Miner_ReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorGogglesItem.class, Goggles_Miner_ReinforcedRenderer::new);
    }
}

