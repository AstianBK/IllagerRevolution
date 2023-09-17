package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.Patreon;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.enchantment.InitEnchantment;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulSlash;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class RunedSword extends SwordItem {

    public int souls = 0;

    public RunedSword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(pEntity instanceof Player){
            this.souls = (int) ((Player)pEntity).getAttribute(SoulTick.SOUL).getValue();
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public boolean isFrostRune(Player player, ItemStack stack){
        return Patreon.isPatreon(player, IllagerRevolutionMod.KNIGHTS_SKIN_UUID) && stack.getHoverName().getString().equals("FrostRune");
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        int i = pStack.getEnchantmentLevel(InitEnchantment.SOUL_SLASH.get());
        if(i!=0){
            if(!pAttacker.level.isClientSide){
                if(pAttacker.level.random.nextFloat()<0.1F*i){
                    if(pAttacker instanceof  Player player){
                        int cc = (int) player.getAttribute(SoulTick.SOUL).getValue();
                        if(cc>5){
                            SoulSlash slash = new SoulSlash(pAttacker,pAttacker.level);
                            slash.shootFromRotation(pAttacker,pAttacker.getXRot(),pAttacker.getYRot(),0.0F,0.4F,0.1F);
                            pAttacker.level.addFreshEntity(slash);
                            pAttacker.level.playSound(null,pAttacker, ModSounds.SOUL_SLASH.get(), SoundSource.PLAYERS,5.0F,1.0F);
                            pStack.hurtAndBreak(5,pAttacker,e->e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                            player.getAttribute(SoulTick.SOUL).setBaseValue(cc-1);
                        }
                    }
                }
            }
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.UNCOMMON;
    }
}
