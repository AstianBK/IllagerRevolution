package net.BKTeam.illagerrevolutionmod.event;


import net.BKTeam.illagerrevolutionmod.entity.client.armor.*;
import net.BKTeam.illagerrevolutionmod.entity.layers.PlayerLikedLayer;
import net.BKTeam.illagerrevolutionmod.item.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
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
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.particle.custom.*;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.geckolib3.world.storage.GeckoLibIdTracker;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

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
        event.put(ModEntityTypes.FALLEN_KNIGHT.get(),FallenKnight.setAttributes());

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
        GeoArmorRenderer.registerArmorRenderer(IllagiumArmorItem.class, Helmet_Miner_ReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorGogglesItem.class, Goggles_Miner_ReinforcedRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorEvokerRobeItem.class, EvokerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorIllusionerRobeItem.class, IllusionerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorPillagerVestItem.class, PillagerPlayerArmorRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(ArmorVindicatorJacketItem.class, VindicatorPlayerArmorRenderer::new);
    }
    
    @SubscribeEvent
    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>>
                                                           event) {
        event.getRegistry().registerAll(
                new RuneBoneFragmentLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"rune_bone_fragment_loot")),
                new RuneFleshFragmentLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"rune_flesh_fragment_loot")),
                new RuneUndyingFragmentLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"rune_undying_fragment_loot")),
                new IllusionerRobeLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"illusioner_robe_loot")),
                new EvokerRobeLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"evoker_robe_loot")),
                new PillagerVestLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"pillager_vest_loot")),
                new PillagerPantsLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"pillager_pants_loot")),
                new VindicatorJacketLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"vindicator_jacket_loot")),
                new VindicatorPantsLootDrop.Serializer().setRegistryName
                        (new ResourceLocation(IllagerRevolutionMod.MOD_ID,"vindicator_pants_loot"))
        );
    }
}
