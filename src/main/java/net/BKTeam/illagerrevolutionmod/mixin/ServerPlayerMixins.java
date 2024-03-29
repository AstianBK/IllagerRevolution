package net.BKTeam.illagerrevolutionmod.mixin;

import com.mojang.authlib.GameProfile;
import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.gui.BeastInventoryMenu;
import net.BKTeam.illagerrevolutionmod.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    public int containerCounter;

    private final List<FallenKnightEntity> entitiesLinked=new ArrayList<>();

    private final List<ZombifiedEntity> zombifieds=new ArrayList<>();


    public ServerPlayerMixins(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_) {
        super(p_36114_, p_36115_, p_36116_, p_36117_);
    }

    @Override
    public void openRakerInventory(IllagerBeastEntity p_9059_, Container p_9060_) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        if(p_9059_.getOwner()!=null){
            ServerPlayer serverPlayer = (ServerPlayer) p_9059_.getOwner();
            this.nextContainerCounter();
            ClientBeastScreenOpenPacket message = new ClientBeastScreenOpenPacket(this.containerCounter, p_9059_.getId());
            PacketHandler.MOD_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
            this.containerMenu = new BeastInventoryMenu(this.containerCounter, this.getInventory(), p_9060_, p_9059_);
            this.containerMenu.addSlotListener(this.containerListener);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
        }

    }

    @Shadow
    public void nextContainerCounter() {

    }

    @Shadow public abstract boolean startRiding(Entity pEntity, boolean pForce);

    @Override
    public List<FallenKnightEntity> getBondedMinions(){
        return this.entitiesLinked;
    }

    @Override
    public List<ZombifiedEntity> getInvocations() {
        return this.zombifieds;
    }
}
