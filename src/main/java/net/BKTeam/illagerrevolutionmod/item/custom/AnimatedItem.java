package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.item.client.AnimatedItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class AnimatedItem extends Item implements IAnimatable {
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public AnimatedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new AnimatedItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.grimoire.idle", ILoopType.EDefaultLoopTypes.LOOP));

        return PlayState.CONTINUE;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        boolean flag = pPlayer.isShiftKeyDown();
        if(!flag){
            if (!pLevel.isClientSide) {
                List<SoulBomb> soulBombs = pPlayer.level.getEntitiesOfClass(SoulBomb.class,pPlayer.getBoundingBox().inflate(3.0D),
                        e-> e.inOrbit() && e.getOwner()!=null && e.getOwner()==pPlayer);
                int i1 = 1;
                for (SoulBomb soul : soulBombs){
                    soul.setPositionSummon(i1);
                    i1++;
                }
                if(soulBombs.size()<6){
                    int i = soulBombs.isEmpty() ? 1 : 1 + soulBombs.size();
                    SoulBomb bomb = new SoulBomb(pPlayer,pLevel,i);
                    bomb.setPosition(pPlayer);
                    pLevel.addFreshEntity(bomb);
                }

            }

            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            if (!pPlayer.getAbilities().instabuild) {

            }
        }else {
            if(!pLevel.isClientSide){
                List<SoulBomb> souls = pPlayer.level.getEntitiesOfClass(SoulBomb.class,pPlayer.getBoundingBox().inflate(3.0F),
                        e-> e.inOrbit() && e.getOwner()!=null && e.getOwner()==pPlayer);
                if(!souls.isEmpty()){
                    boolean flag1 = false;
                    int i = 0;
                    for (SoulBomb soulBomb : souls){
                        if(!flag1){
                            flag1=true;
                            soulBomb.shootFromRotation(pPlayer,pPlayer.getXRot(),pPlayer.getYHeadRot(),0.0F,1.0F,0.1F);
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

        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
