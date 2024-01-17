package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.enchantment.InitEnchantment;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulMissile;
import net.BKTeam.illagerrevolutionmod.item.client.AnimatedItemRenderer;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class AnimatedItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller",
                0, this::predicate));
    }

    private <E extends GeoItem> PlayState predicate(AnimationState<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        CompoundTag nbt = pStack.getOrCreateTag();
        if (!nbt.isEmpty() && pIsSelected) {
            int i = nbt.getInt("casterTimer");
            this.castingTimer = i;
            if (i > 0) {
                i--;
                nbt.putInt("casterTimer", i);
            }
        }

        if (pEntity instanceof Player player) {
            int cc = (int) player.getAttribute(SoulTick.SOUL).getValue();
            this.souls = cc;
            nbt.putInt("CustomModelData", cc);
        }

        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public int getSouls() {
        return this.souls;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        boolean flag = pPlayer.isShiftKeyDown();
        CompoundTag nbt = pPlayer.getItemInHand(pUsedHand).getOrCreateTag();
        int cooldown = nbt.getInt("casterTimer");
        int cc = (int) pPlayer.getAttributeValue(SoulTick.SOUL);
        if (flag && cc > 0) {
            List<SoulBomb> soulBombs = pPlayer.level().getEntitiesOfClass(SoulBomb.class, pPlayer.getBoundingBox().inflate(3.0D),
                    e -> e.inOrbit() && e.getOwner() != null && e.getOwner() == pPlayer && !e.isDefender());
            int i1 = 1;
            int size = soulBombs.size();
            if (!pLevel.isClientSide) {
                nbt.putInt("casterTimer", 40);
                if (size < 3) {
                    for (SoulBomb soul : soulBombs) {
                        soul.setPositionSummon(i1);
                        i1++;
                    }
                    int i = soulBombs.isEmpty() ? 1 : 1 + size;
                    SoulBomb bomb = new SoulBomb(pPlayer, pLevel, i);
                    bomb.setPosition(pPlayer);
                    bomb.setPowerLevel(EnchantmentHelper.getItemEnchantmentLevel(InitEnchantment.INSIGHT.get(), pPlayer.getMainHandItem()));
                    pLevel.addFreshEntity(bomb);
                    size++;
                }
            }
            pLevel.playSound(null, pPlayer, SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.PLAYERS, 1.0F, 1.5F);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            if (!pPlayer.getAbilities().instabuild && size < 4) {
                pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc - 1);
            }
        } else {
            if (!pLevel.isClientSide) {
                List<SoulBomb> souls = pPlayer.level().getEntitiesOfClass(SoulBomb.class, pPlayer.getBoundingBox().inflate(3.0F),
                        e -> e.inOrbit() && e.getOwner() != null && e.getOwner() == pPlayer && !e.isDefender());
                if (!souls.isEmpty()) {
                    boolean flag1 = false;
                    int i = 0;
                    for (SoulBomb soulBomb : souls) {
                        if (!flag1) {
                            flag1 = true;
                            pLevel.playSound(null, pPlayer, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, -1.0F);
                            soulBomb.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.0F, 0.1F);
                        } else {
                            if (soulBomb.getPositionSummon() > 1) {
                                soulBomb.setPositionSummon(i + 1);
                            }
                        }
                        i++;
                    }
                } else if (cooldown <= 0) {
                    SoulMissile missile = new SoulMissile(pPlayer, pLevel);
                    missile.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 2.0F, 0.0F);
                    missile.setPowerLevel(EnchantmentHelper.getItemEnchantmentLevel(InitEnchantment.INSIGHT.get(), pPlayer.getMainHandItem()));
                    pLevel.addFreshEntity(missile);
                    pLevel.playSound(null, pPlayer, ModSounds.SOUL_SAGE_MISSILE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                    nbt.putInt("casterTimer", 40);
                }
            }

            if (!pPlayer.getAbilities().instabuild) {

            }
        }
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.ominous_grimoire.runemissile1"));

            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.ominous_grimoire.soulbomb1"));
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.ominous_grimoire.soulbomb2"));

            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.ominous_grimoire.runeshield1"));

        } else {
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.ominous_grimoire.tooltip1"));

        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
