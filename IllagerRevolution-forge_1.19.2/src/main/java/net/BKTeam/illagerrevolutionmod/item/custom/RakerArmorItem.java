package net.BKTeam.illagerrevolutionmod.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IRakerArmorItem;

public class RakerArmorItem extends Item implements IRakerArmorItem {

    private final int armorValue;
    private final ResourceLocation tex;
    private final ArmorMaterial armorMaterial;
    private final double damage;
    private final int extraTime;
    private final EquipmentSlot slot;

    public RakerArmorItem(Properties pProperties, int armorValue, String tierarmor, ArmorMaterial armorMaterial, double damage,int extraTime, EquipmentSlot pSlot) {
        this(armorValue, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/raker/raker_equip/"+tierarmor+"_raker_"+pSlot.getName()+".png"), pProperties, armorMaterial,damage,extraTime,pSlot);
    }
    public RakerArmorItem(int armorValue, ResourceLocation texture, Properties builder, ArmorMaterial armorMaterial, double pDamage, int extraTime,EquipmentSlot slot) {
        super(builder);
        this.armorMaterial = armorMaterial;
        this.armorValue = armorValue;
        this.tex = texture;
        this.damage=pDamage;
        this.extraTime=extraTime;
        this.slot=slot;
    }
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getArmorTexture() {
        return tex;
    }

    @Override
    public ArmorMaterial getArmorMaterial() {
        return this.armorMaterial;
    }
    @Override
    public int getArmorValue() {
        return this.armorValue;
    }

    @Override
    public double getDamageValue() {
        return this.damage;
    }

    @Override
    public int getAddBleeding() {
        return  this.extraTime;
    }

    @Override
    public EquipmentSlot getEquipmetSlot() {
        return this.slot;
    }

}
