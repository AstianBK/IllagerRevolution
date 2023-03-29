package net.BKTeam.illagerrevolutionmod.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorGogglesItem;

public class WaryLensesEnchantment extends Enchantment {
    protected WaryLensesEnchantment(EquipmentSlot... slots) {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, slots);
    }
    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ArmorGogglesItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ArmorGogglesItem;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
