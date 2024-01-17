package net.BKTeam.illagerrevolutionmod.api;

import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;

public interface IBeastArmorItem {

    ArmorMaterial getArmorMaterial();
    int getArmorValue();
    double getDamageValue();
    int getAddBleeding();
    EquipmentSlot getEquipmetSlot();

    Beast getBeast();
}
