package net.BKTeam.illagerrevolutionmod.enchantment.custom;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorGogglesItem;
import net.BKTeam.illagerrevolutionmod.item.custom.RunedSword;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class SoulSlashEnchantment extends Enchantment {
    public SoulSlashEnchantment(EquipmentSlot... slots) {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, slots);
    }
    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof RunedSword;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != Enchantments.SWEEPING_EDGE;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof RunedSword;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}