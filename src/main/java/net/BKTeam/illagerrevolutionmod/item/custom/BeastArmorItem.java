package net.BKTeam.illagerrevolutionmod.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IBeastArmorItem;

public class BeastArmorItem extends Item implements IBeastArmorItem {

    private final int armorValue;
    private final ResourceLocation tex;
    private final ArmorMaterial armorMaterial;
    private final double damage;
    private final int extraTime;
    private final EquipmentSlot slot;

    private final String beastName;

    public BeastArmorItem(Properties pProperties, int armorValue, String tierarmor, ArmorMaterial armorMaterial, double damage, int extraTime, EquipmentSlot pSlot, String nameBeast) {
        this(armorValue, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/"+nameBeast+"/armor/"+nameBeast+"_armor_"+pSlot.getName()+"_"+tierarmor+".png"), pProperties, armorMaterial,damage,extraTime,pSlot,nameBeast);
    }
    public BeastArmorItem(int armorValue, ResourceLocation texture, Properties builder, ArmorMaterial armorMaterial, double pDamage, int extraTime, EquipmentSlot slot,String beastName) {
        super(builder);
        this.armorMaterial = armorMaterial;
        this.armorValue = armorValue;
        this.tex = texture;
        this.damage=pDamage;
        this.extraTime=extraTime;
        this.slot=slot;
        this.beastName=beastName;
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

    @Override
    public String getName() {
        return this.beastName;
    }

}
