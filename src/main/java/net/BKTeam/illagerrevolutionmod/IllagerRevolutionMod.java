package net.BKTeam.illagerrevolutionmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.data.DeathEntityEvent;
import net.BKTeam.illagerrevolutionmod.setup.Messages;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.enchantment.Init_enchantment;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers.*;
import net.BKTeam.illagerrevolutionmod.gui.Hearts_Effect;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import org.slf4j.Logger;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

import static net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes.*;


@Mod(IllagerRevolutionMod.MOD_ID)
public class IllagerRevolutionMod {
    public static final String MOD_ID = "illagerrevolutionmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IllagerRevolutionMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        GeckoLib.initialize();
        GeckoLibMod.DISABLE_IN_DEV=true;
        ModEntityTypes.register(eventBus);

        ModParticles.register(eventBus);
        ModSounds.register(eventBus);
        init_effect.REGISTRY.register(eventBus);
        Init_enchantment.REGISTRY.register(eventBus);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::RuleSetup);

        PacketHandler.registerMessages();
        setupD();

        ModItems.register(eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->{
            eventBus.addListener(this::registerRenderers);
        });

        DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, IllagerRevolutionMod.MOD_ID);
        ATTRIBUTES.register("soul",()->SoulTick.SOUL);
        ATTRIBUTES.register(eventBus);
    }
    private void clientSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModEntityTypes::registerWaveMembers);
    }

    @OnlyIn(Dist.CLIENT)
    private void registerRenderers(FMLCommonSetupEvent event){
        EntityRenderers.register(ModEntityTypes.ILLAGERMINERBADLANDS.get(), IllagerMinerBadlandsRenderer::new);
        EntityRenderers.register(RAKER.get(), RakerRenderer::new);
        EntityRenderers.register(ModEntityTypes.ILLAGERMINER.get(), IllagerMinerRenderer::new);
        EntityRenderers.register(ModEntityTypes.ILLAGERBEASTTAMER.get(), Illager_Beast_TamerRenderer::new);
        EntityRenderers.register(ModEntityTypes.ZOMBIFIED.get(), ZombifiedRenderer::new);
        EntityRenderers.register(ModEntityTypes.BLADE_KNIGHT.get(), Blade_knightRenderer::new);
        EntityRenderers.register(ModEntityTypes.SOUL_PROJECTILE.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(SOUL_HUNTER.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(SUMMONED_SOUL.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(SOUL_ENTITY.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(ARROWBEAST.get(), ArrowBeastRender::new);
        event.enqueueWork(() -> {
            OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, IllagerRevolutionMod.MOD_ID + ":hearts", new Hearts_Effect());
        });
    }
    private void RuleSetup(FMLCommonSetupEvent event){
        event.enqueueWork(()->{
            SpawnPlacements.register(ILLAGERBEASTTAMER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING,
                    SpellcasterIllager::checkMobSpawnRules);

            SpawnPlacements.register(ILLAGERMINER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    AbstractIllager::checkMobSpawnRules);

            SpawnPlacements.register(ILLAGERMINERBADLANDS.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    AbstractIllager::checkMobSpawnRules);
        });
        Messages.register();
    }
    public static void setupD() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(DeathEntityEvent::onLivintDeathEvent);
    }

    public static ResourceLocation rl (String s ){
        return new ResourceLocation(MOD_ID, s);
    }

}