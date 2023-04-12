package net.BKTeam.illagerrevolutionmod.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.item.ModItems;

public class SerratedEdgeEnchantment extends Enchantment {
    public SerratedEdgeEnchantment(EquipmentSlot... slots) {
        super(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModItems.ILLAGIUM_CLEAVER.get();
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return ench== Enchantments.SMITE || ench == Enchantments.MENDING || ench == Enchantments.UNBREAKING || ench == Enchantments.SWEEPING_EDGE || ench == Enchantments.FIRE_ASPECT;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModItems.ILLAGIUM_CLEAVER.get();
    }
    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        LivingEntity livingEntity= (LivingEntity) pTarget;
        int ampliEffect=livingEntity.hasEffect(init_effect.DEEP_WOUND.get()) ? livingEntity.getEffect(init_effect.DEEP_WOUND.get()).getAmplifier() : 0;
        int ampliBleeding=0;
        if(livingEntity.hasEffect(init_effect.DEEP_WOUND.get()) && ampliEffect==1){
            ampliBleeding=2;
        }else if(livingEntity.hasEffect(init_effect.DEEP_WOUND.get()) && ampliEffect==0){
            ampliBleeding=1;
        }
        livingEntity.addEffect(new MobEffectInstance(init_effect.DEEP_WOUND.get(),160,ampliBleeding));

    }
}
