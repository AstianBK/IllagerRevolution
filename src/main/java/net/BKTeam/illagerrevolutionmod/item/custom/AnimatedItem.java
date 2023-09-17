package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.enchantment.InitEnchantment;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulMissile;
import net.BKTeam.illagerrevolutionmod.item.client.AnimatedItemRenderer;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Mod;

import java.util.List;
import java.util.function.Consumer;

public class AnimatedItem extends Item implements IAnimatable {
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public int souls = 0;
    public int castingTimer = 0;

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
        data.addAnimationController(new AnimationController<>(this, "controller",
                0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.grimoire.use_spell", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        CompoundTag nbt = pStack.getOrCreateTag();
        if(!nbt.isEmpty() && pIsSelected) {
            int i = nbt.getInt("casterTimer");
            this.castingTimer = i;
            if (i > 0) {
                i--;
                nbt.putInt("casterTimer", i);
            }
        }

        if(pEntity instanceof Player player){
            int cc = (int) player.getAttribute(SoulTick.SOUL).getValue();
            this.souls = cc;
            nbt.putInt("CustomModelData", cc);
        }

        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public int getSouls() {
        return this.souls;
    }

    public int getCastingTimer(){
        return this.castingTimer;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        boolean flag = pPlayer.isShiftKeyDown();
        CompoundTag nbt = pPlayer.getItemInHand(pUsedHand).getOrCreateTag();
        int cooldown = nbt.getInt("casterTimer");
        int cc = (int) pPlayer.getAttributeValue(SoulTick.SOUL);
        if(flag && cc>0){
            if (!pLevel.isClientSide) {
                List<SoulBomb> soulBombs = pPlayer.level.getEntitiesOfClass(SoulBomb.class,pPlayer.getBoundingBox().inflate(3.0D),
                        e-> e.inOrbit() && e.getOwner()!=null && e.getOwner()==pPlayer);
                int i1 = 1;
                int size = soulBombs.size();
                nbt.putInt("casterTimer",40);

                if(size <3){
                    for (SoulBomb soul : soulBombs){
                        soul.setPositionSummon(i1);
                        i1++;
                    }
                    int i = soulBombs.isEmpty() ? 1 : 1 + size;
                    SoulBomb bomb = new SoulBomb(pPlayer,pLevel,i);
                    bomb.setPosition(pPlayer);
                    bomb.setPowerLevel(pPlayer.getMainHandItem().getEnchantmentLevel(InitEnchantment.INSIGHT.get()));
                    pLevel.addFreshEntity(bomb);
                }
            }
            pLevel.playSound(null,pPlayer, SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.PLAYERS,1.0F,1.5F);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            if (!pPlayer.getAbilities().instabuild) {
                pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc-1);
            }
        }else {
            if(!pLevel.isClientSide){
                List<SoulBomb> souls = pPlayer.level.getEntitiesOfClass(SoulBomb.class,pPlayer.getBoundingBox().inflate(3.0F),
                        e-> e.inOrbit() && e.getOwner()!=null && e.getOwner()==pPlayer && !e.isDefender());
                if(!souls.isEmpty()){
                    boolean flag1 = false;
                    int i = 0;
                    for (SoulBomb soulBomb : souls){
                        if(!flag1){
                            flag1=true;
                            pLevel.playSound(null,pPlayer, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS,1.0F,-1.0F);
                            soulBomb.shootFromRotation(pPlayer,pPlayer.getXRot(),pPlayer.getYHeadRot(),0.0F,1.0F,0.1F);
                        }else {
                            if(soulBomb.getPositionSummon()>1){
                                soulBomb.setPositionSummon(i+1);
                            }
                        }
                        i++;
                    }
                }else if(cooldown<=0){
                    SoulMissile missile = new SoulMissile(pPlayer,pLevel);
                    missile.shootFromRotation(pPlayer,pPlayer.getXRot(),pPlayer.getYRot(),0.0F,2.0F,0.0F);
                    missile.setPowerLevel(pPlayer.getMainHandItem().getEnchantmentLevel(InitEnchantment.INSIGHT.get()));
                    pLevel.addFreshEntity(missile);
                    pLevel.playSound(null,pPlayer, ModSounds.SOUL_SAGE_MISSILE.get(), SoundSource.HOSTILE,1.0F,1.0F);
                    nbt.putInt("casterTimer",40);
                }
            }

            if(!pPlayer.getAbilities().instabuild){

            }
        }
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
