package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Hunter;

public class Soul_HunterItem extends Item {

    public Soul_HunterItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide) {
            FallenKnight knight = new FallenKnight(ModEntityTypes.FALLEN_KNIGHT.get(),pLevel);
            knight.setIdOwner(pPlayer.getUUID());
            knight.setDispawnTimer(1200,pPlayer,false);
            knight.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(pLevel.random.nextFloat() < 0.5f ? Items.IRON_AXE : Items.IRON_SWORD));
            pLevel.addFreshEntity(knight);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
