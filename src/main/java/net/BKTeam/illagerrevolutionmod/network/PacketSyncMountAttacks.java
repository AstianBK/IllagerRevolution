package net.BKTeam.illagerrevolutionmod.network;

import net.BKTeam.illagerrevolutionmod.entity.custom.MountEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.command.ModIdArgument;

import java.util.List;
import java.util.function.Supplier;


public class PacketSyncMountAttacks {
    private final int key;
    private final byte pId;

    public PacketSyncMountAttacks(FriendlyByteBuf buf) {
        this.key = buf.readInt();
        this.pId = buf.readByte();
    }

    public PacketSyncMountAttacks(int pKey, byte pId){
        this.key = pKey;
        this.pId = pId;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(key);
        buf.writeByte(pId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->{
            Player player=context.get().getSender();
            assert player!=null;
            LivingEntity vehicle = (LivingEntity) player.getVehicle();
            ItemStack book=player.getItemBySlot(EquipmentSlot.MAINHAND);
            handleEnchantmet(book,player);
            handlePlayActivateAnimation(vehicle);
        });
        context.get().setPacketHandled(true);
    }
    private void handlePlayActivateAnimation(LivingEntity vehicle) {
        if(vehicle instanceof MountEntity mount){
            mount.handledEventKey(this.pId);
        }
    }

    private void handleEnchantmet(ItemStack pStack,Player pPlayer) {
        if(pStack.is(ModItems.OMINOUS_GRIMOIRE.get()) && this.pId==2){
            List<SoulBomb> souls = pPlayer.level.getEntitiesOfClass(SoulBomb.class,pPlayer.getBoundingBox().inflate(3.0F),
                    e-> e.inOrbit() && e.getOwner()!=null && e.getOwner()==pPlayer);
            if(!souls.isEmpty()){
                boolean flag1 = false;
                int i = 0;
                for (SoulBomb soulBomb : souls){
                    if(!flag1){
                        flag1=true;
                        soulBomb.setDefender(true);
                        pPlayer.level.playSound(pPlayer,pPlayer, ModSounds.SOUL_RELEASE.get(), SoundSource.PLAYERS,2.0F,2.0f);
                    }else {
                        if(soulBomb.getPositionSummon()>1){
                            soulBomb.setPositionSummon(i+1);
                        }
                    }
                    i++;
                }
            }
        }

    }
}
