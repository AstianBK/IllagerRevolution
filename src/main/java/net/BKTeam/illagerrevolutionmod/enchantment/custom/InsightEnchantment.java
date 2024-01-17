package net.BKTeam.illagerrevolutionmod.enchantment.custom;

import net.BKTeam.illagerrevolutionmod.item.custom.AnimatedItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InsightEnchantment extends Enchantment {
    public InsightEnchantment(EquipmentSlot... slots) {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, slots);
    }
    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof AnimatedItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof AnimatedItem;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}