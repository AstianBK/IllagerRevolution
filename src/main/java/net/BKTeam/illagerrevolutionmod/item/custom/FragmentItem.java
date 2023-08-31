package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FragmentItem extends Item {
    private final String name;
    public FragmentItem(Properties pProperties,String name) {
        super(pProperties);
        this.name=name;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod."+this.name));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}