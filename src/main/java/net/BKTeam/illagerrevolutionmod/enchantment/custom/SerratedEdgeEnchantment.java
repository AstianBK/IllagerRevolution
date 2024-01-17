package net.BKTeam.illagerrevolutionmod.enchantment.custom;

import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class SerratedEdgeEnchantment extends Enchantment {
    public SerratedEdgeEnchantment(EquipmentSlot... slots) {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return ench == Enchantments.MOB_LOOTING || ench== Enchantments.SMITE || ench == Enchantments.MENDING || ench == Enchantments.UNBREAKING || ench == Enchantments.SWEEPING_EDGE || ench == Enchantments.FIRE_ASPECT;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem;
    }
    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        if(pTarget instanceof LivingEntity livingEntity){
            int ampliEffect=livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) ? livingEntity.getEffect(InitEffect.DEEP_WOUND.get()).getAmplifier() : 0;
            int ampliBleeding=0;
            if(livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) && ampliEffect==1){
                ampliBleeding=2;
            }else if(livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) && ampliEffect==0){
                ampliBleeding=1;
            }
            livingEntity.addEffect(new MobEffectInstance(InitEffect.DEEP_WOUND.get(),160,ampliBleeding));
        }


    }
}
