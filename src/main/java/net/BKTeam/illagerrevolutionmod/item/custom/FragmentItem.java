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
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide){
            List<SoulBomb> souls = pPlayer.level.getEntitiesOfClass(SoulBomb.class,pPlayer.getBoundingBox().inflate(3.0F),
                    e-> e.inOrbit() && e.getOwner()!=null && e.getOwner()==pPlayer);
            if(!souls.isEmpty()){
                boolean flag1 = false;
                int i = 0;
                for (SoulBomb soulBomb : souls){
                    if(!flag1){
                        flag1=true;
                        soulBomb.setDefender(true);
                    }else {
                        if(soulBomb.getPositionSummon()>1){
                            soulBomb.setPositionSummon(i+1);
                        }
                    }
                    i++;
                }
            }
        }

        if(!pPlayer.getAbilities().instabuild){
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod."+this.name));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}