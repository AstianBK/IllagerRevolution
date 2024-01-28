package net.BKTeam.illagerrevolutionmod;

import com.mojang.logging.LogUtils;
import net.BKTeam.illagerrevolutionmod.block.ModBlocks;
import net.BKTeam.illagerrevolutionmod.block.entity.ModBlockEntities;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.BKTeam.illagerrevolutionmod.data.server.tags.BKEntityTypeTagsProvider;
import net.BKTeam.illagerrevolutionmod.data.server.tags.BkBlockTagsProvider;
import net.BKTeam.illagerrevolutionmod.data.server.tags.BkItemTagsProvider;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.data.DeathEntityEvent;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.enchantment.InitEnchantment;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers.*;
import net.BKTeam.illagerrevolutionmod.gui.BKGui;
import net.BKTeam.illagerrevolutionmod.gui.HeartsEffect;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.keybind.BKKeybinds;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.orderoftheknight.TheKnightOrders;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.screen.ModMenuTypes;
import net.BKTeam.illagerrevolutionmod.screen.RuneTableScreen;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

import javax.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes.*;


@Mod(IllagerRevolutionMod.MOD_ID)
public class IllagerRevolutionMod {
    public static final String MOD_ID = "illagerrevolutionmod";
    public static String ACOLYTES_SKIN_UUID;

    public static String MAGES_SKIN_UUID;
    public static String KNIGHTS_SKIN_UUID;
    private static final Logger LOGGER = LogUtils.getLogger();

    public IllagerRevolutionMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        GeckoLib.initialize();
        GeckoLibMod.DISABLE_IN_DEV=true;
        
        ModEntityTypes.register(eventBus);
        ModParticles.register(eventBus);
        ModSounds.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModBlocks.register(eventBus);
        ModMenuTypes.register(eventBus);

        
        InitEffect.REGISTRY.register(eventBus);
        InitEnchantment.REGISTRY.register(eventBus);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(CapabilityHandler::registerCapabilities);
        eventBus.addListener(this::dataSetup);

        PacketHandler.registerMessages();
        setupD();

        URL url = null;
        try {
            try {
                url = new URL("https://pastebin.com/raw/ULTufUUJ");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                if(con.getResponseCode()!=200){
                    throw new RuntimeException(String.valueOf(con.getResponseCode()));
                }else{
                    Scanner sc = new Scanner(url.openStream());
                    StringBuilder sb = new StringBuilder();
                    while (sc.hasNext()) {
                        sb.append(sc.next());
                        //System.out.println(sc.next());
                    }
                    sc.close();
                    ACOLYTES_SKIN_UUID = sb.toString();

                    ACOLYTES_SKIN_UUID = ACOLYTES_SKIN_UUID.replaceAll("<[^>]*>", "");
                }

                System.out.println("Refreshing Illager Revolution Patreon List");
            }catch (MalformedURLException e){
                System.out.println("Failed");
            }

        } catch (IOException ignored) {
            System.out.println("Refreshing Illager Revolution Patreon List failed");
        }
        try {
            try {
                URL url1 = new URL("https://pastebin.com/raw/s9ZtFtFL");
                HttpURLConnection con = (HttpURLConnection) url1.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                if(con.getResponseCode()!=200){
                    throw new RuntimeException(String.valueOf(con.getResponseCode()));
                }else{
                    Scanner sc = new Scanner(url1.openStream());
                    StringBuilder sb = new StringBuilder();
                    while (sc.hasNext()) {
                        sb.append(sc.next());
                        //System.out.println(sc.next());
                    }
                    sc.close();
                    KNIGHTS_SKIN_UUID = sb.toString();

                    KNIGHTS_SKIN_UUID = KNIGHTS_SKIN_UUID.replaceAll("<[^>]*>", "");
                }
                System.out.println("Refreshing Illager Revolution Patreon List");
            }catch (MalformedURLException e){
                System.out.println("Failed");
            }

        } catch (IOException ignored) {
            System.out.println("Refreshing Illager Revolution Patreon List failed");
        }
        try {
            try {
                URL url2 = new URL("https://pastebin.com/raw/qak4v4Vd");
                HttpURLConnection con = (HttpURLConnection) url2.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                if(con.getResponseCode()!=200){
                    throw new RuntimeException(String.valueOf(con.getResponseCode()));
                }else{
                    Scanner sc = new Scanner(url2.openStream());
                    StringBuilder sb = new StringBuilder();
                    while (sc.hasNext()) {
                        sb.append(sc.next());
                        //System.out.println(sc.next());
                    }
                    sc.close();
                    MAGES_SKIN_UUID = sb.toString();

                    MAGES_SKIN_UUID = MAGES_SKIN_UUID.replaceAll("<[^>]*>", "");
                }
                System.out.println("Refreshing Illager Revolution Patreon List");
            }catch (MalformedURLException e){
                System.out.println("Failed");
            }

        } catch (IOException ignored) {
            System.out.println("Refreshing Illager Revolution Patreon List failed");
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->{
            eventBus.addListener(this::registerRenderers);
            eventBus.addListener(BKKeybinds::register);
        });

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,CapabilityHandler::attachEntityCapability);
        MinecraftForge.EVENT_BUS.addListener(this::onLoadingLevel);
        DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, IllagerRevolutionMod.MOD_ID);
        ATTRIBUTES.register("soul",()->SoulTick.SOUL);
        ATTRIBUTES.register(eventBus);
    }
    
    private void clientSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModEntityTypes::registerWaveMembers);
    }

    private void dataSetup(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        boolean includeServer = event.includeServer();
        BkBlockTagsProvider blockTags = new BkBlockTagsProvider(generator, existingFileHelper);
        generator.addProvider( blockTags);
        generator.addProvider( new BkItemTagsProvider(generator,blockTags ,existingFileHelper));
        generator.addProvider( new BKEntityTypeTagsProvider(generator, existingFileHelper));
    }

    @OnlyIn(Dist.CLIENT)
    private void registerRenderers(FMLCommonSetupEvent event){
        EntityRenderers.register(ModEntityTypes.ILLAGER_SCAVENGER.get(), IllagerScavengerRenderer::new);
        EntityRenderers.register(RAKER.get(), RakerRenderer::new);
        EntityRenderers.register(ModEntityTypes.ILLAGER_MINER.get(), IllagerMinerRenderer::new);
        EntityRenderers.register(ModEntityTypes.ILLAGER_BEAST_TAMER.get(), IllagerBeastTamerRenderer::new);
        EntityRenderers.register(ModEntityTypes.ZOMBIFIED.get(), ZombifiedRenderer::new);
        EntityRenderers.register(ModEntityTypes.BLADE_KNIGHT.get(), BladeKnightRenderer::new);
        EntityRenderers.register(BULKWARK.get(), BulkWarkRenderer::new);
        EntityRenderers.register(FALLEN_KNIGHT.get(),FallenKnightRenderer::new);
        EntityRenderers.register(ACOLYTE.get(),AcolyteRenderer::new);
        EntityRenderers.register(ModEntityTypes.SOUL_PROJECTILE.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(SOUL_HUNTER.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(SUMMONED_SOUL.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(SOUL_SLASH.get(), SlashRender::new);
        EntityRenderers.register(SOUL_ENTITY.get(), ThrownItemRenderer<ThrowableItemProjectile>::new);
        EntityRenderers.register(ARROWBEAST.get(), ArrowBeastRender::new);
        EntityRenderers.register(FEATHER_PROJECTILE.get(), FeatherRender::new);
        EntityRenderers.register(MAULER.get(), MaulerRenderer::new);
        EntityRenderers.register(SCROUNGER.get(), ScroungerRenderer::new);
        EntityRenderers.register(WILD_RAVAGER.get(), WildRavagerRenderer::new);
        EntityRenderers.register(SOUL_BOMB.get(), NoopRenderer::new);
        EntityRenderers.register(SOUL_MISSILE.get(), NoopRenderer::new);
        EntityRenderers.register(AREA_FIRE_COLUMN.get(),NoopRenderer::new);
        EntityRenderers.register(SOUL_SAGE.get(), SoulSageRenderer::new);
        EntityRenderers.register(SOUL_EATER.get(), SoulEaterRenderer::new);
        MenuScreens.register(ModMenuTypes.RUNE_TABLE_MENU.get(), RuneTableScreen::new);
        event.enqueueWork(() -> {
            OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, IllagerRevolutionMod.MOD_ID + ":hearts", new HeartsEffect());
            OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HELMET_ELEMENT, "shield",new BKGui());
        });

    }

    private static DataSaver GAME_DATA_SAVER;
    public void onLoadingLevel(WorldEvent.Load event) {
        ServerLevel overworld = getOverworld( event.getWorld() );
        if( overworld == null )
            return;

        GAME_DATA_SAVER = overworld.getDataStorage()
                .computeIfAbsent(
                        nbt->new DataSaver( overworld, nbt ),
                        ()->new DataSaver( overworld ),
                        IllagerRevolutionMod.MOD_ID
                );
    }


    @Nullable
    private static ServerLevel getOverworld(LevelAccessor levelAccessor ) {
        ServerLevel overworld = levelAccessor.getServer() != null ? levelAccessor.getServer().getLevel( Level.OVERWORLD ) : null;
        return levelAccessor.equals( overworld ) ? overworld : null;
    }

    public static TheKnightOrders getTheOrders(ServerLevel level){
        return GAME_DATA_SAVER.getTheOrderAttack();
    }

    public static void setDirty(){
        if(GAME_DATA_SAVER!=null){
            GAME_DATA_SAVER.setDirty();
        }
    }

    public static void setupD() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(DeathEntityEvent::onLivintDeathEvent);
    }

}
