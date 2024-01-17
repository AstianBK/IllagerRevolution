package net.BKTeam.illagerrevolutionmod.enchantment.custom;

import net.BKTeam.illagerrevolutionmod.enchantment.BKMobType;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class BeastSlayerEnchantment extends Enchantment {
    public BeastSlayerEnchantment(EquipmentSlot... slots) {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return ench == Enchantments.MOB_LOOTING || ench == Enchantments.MENDING
                || ench == Enchantments.UNBREAKING || ench == Enchantments.SWEEPING_EDGE
                || ench == Enchantments.FIRE_ASPECT;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem;
    }
    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
       if(pTarget instanceof IllagerBeastEntity beast){
           beast.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,100,0));
           pAttacker.getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak(1,pAttacker,e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
       }
    }

    @Override
    public float getDamageBonus(int pLevel, MobType pType) {
        if(pType== BKMobType.BEAST_ILLAGER){
            return 0.15F*pLevel;
        }
        return super.getDamageBonus(pLevel, pType);
    }



}
