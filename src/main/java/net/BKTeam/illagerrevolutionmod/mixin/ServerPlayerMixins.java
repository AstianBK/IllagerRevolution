package net.BKTeam.illagerrevolutionmod.mixin;

import com.mojang.authlib.GameProfile;
import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.api.IProxy;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.gui.MaulerInventoryMenu;
import net.BKTeam.illagerrevolutionmod.gui.RakerInventoryMenu;
import net.BKTeam.illagerrevolutionmod.gui.ScroungerInventoryMenu;
import net.BKTeam.illagerrevolutionmod.gui.WildRavagerInventoryMenu;
import net.BKTeam.illagerrevolutionmod.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixins extends Player implements IOpenBeatsContainer, INecromancerEntity {
    @Shadow
    @Final
    private ContainerListener containerListener;

    @Shadow
    private int containerCounter;

    private final List<FallenKnightEntity> entitiesLinked=new ArrayList<>();

    private final List<ZombifiedEntity> zombifieds=new ArrayList<>();


    public ServerPlayerMixins(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_, ProfilePublicKey key) {
        super(p_36114_, p_36115_, p_36116_, p_36117_,key);
    }

    @Override
    public void openRakerInventory(RakerEntity p_9059_, Container p_9060_) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        ClientRakerScreenOpenPacket message = new ClientRakerScreenOpenPacket(this.containerCounter, p_9059_.getId());
        PacketHandler.MOD_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> p_9059_), message);
        this.containerMenu = new RakerInventoryMenu(this.containerCounter, this.getInventory(), p_9060_, p_9059_);
        this.containerMenu.addSlotListener(this.containerListener);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
    }
    @Override
    public void openMaulerInventory(MaulerEntity p_9059_, Container p_9060_) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        ClientMaulerScreenOpenPacket message = new ClientMaulerScreenOpenPacket(this.containerCounter, p_9059_.getId());
        PacketHandler.MOD_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> p_9059_), message);
        this.containerMenu = new MaulerInventoryMenu(this.containerCounter, this.getInventory(), p_9060_, p_9059_);
        this.containerMenu.addSlotListener(this.containerListener);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
    }

    @Override
    public void openRavagerInventory(WildRavagerEntity p_9059_, Container p_9060_) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        ClientRavagerScreenOpenPacket message = new ClientRavagerScreenOpenPacket(this.containerCounter, p_9059_.getId());
        PacketHandler.MOD_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> p_9059_), message);
        this.containerMenu = new WildRavagerInventoryMenu(this.containerCounter, this.getInventory(), p_9060_, p_9059_);
        this.containerMenu.addSlotListener(this.containerListener);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));

    }

    @Override
    public void openScroungerInventory(ScroungerEntity p_9059_, Container p_9060_) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        ClientScroungerScreenOpenPacket message = new ClientScroungerScreenOpenPacket(this.containerCounter, p_9059_.getId());
        PacketHandler.MOD_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> p_9059_), message);
        this.containerMenu = new ScroungerInventoryMenu(this.containerCounter, this.getInventory(), p_9060_, p_9059_);
        this.containerMenu.addSlotListener(this.containerListener);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));

    }

    @Shadow
    private void nextContainerCounter() {

    }
    @Override
    public List<FallenKnightEntity> getBondedMinions(){
        return this.entitiesLinked;
    }

    @Override
    public List<ZombifiedEntity> getInvocations() {
        return this.zombifieds;
    }
}
