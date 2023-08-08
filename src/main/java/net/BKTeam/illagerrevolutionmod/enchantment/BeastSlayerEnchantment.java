package net.BKTeam.illagerrevolutionmod.enchantment;

import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.enchantment.BKMobType;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        return ench == Enchantments.MOB_LOOTING || ench == Enchantments.MENDING || ench == Enchantments.UNBREAKING || ench == Enchantments.SWEEPING_EDGE || ench == Enchantments.FIRE_ASPECT;
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
    public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        if(mobType== BKMobType.BEAST_ILLAGER)  {
            if(enchantedItem.getItem() instanceof SwordItem item)
            return item.getDamage()*(0.15F*level);
        }
        return super.getDamageBonus(level, mobType, enchantedItem);
    }
}
