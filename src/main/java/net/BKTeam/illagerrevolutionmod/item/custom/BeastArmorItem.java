package net.BKTeam.illagerrevolutionmod.item.custom;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IBeastArmorItem;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeastArmorItem extends Item implements IBeastArmorItem {

    private final int armorValue;
    private final ResourceLocation tex;
    private final ArmorMaterial armorMaterial;
    private final double damage;
    private final int extraTime;
    private final EquipmentSlot slot;

    private final Beast beast;

    public BeastArmorItem(Properties pProperties, int armorValue, ArmorMaterial armorMaterial, double damage, int extraTime, EquipmentSlot pSlot, Beast beast) {
        this(armorValue, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/"+beast.getBeastName()+"/armor/"+beast.getBeastName()+"_armor_"+pSlot.getName()+"_"+armorMaterial.getName()+".png"), pProperties, armorMaterial,damage,extraTime,pSlot,beast);
    }
    public BeastArmorItem(int armorValue, ResourceLocation texture, Properties builder, ArmorMaterial armorMaterial, double pDamage, int extraTime, EquipmentSlot slot,Beast beastName) {
        super(builder);
        this.armorMaterial = armorMaterial;
        this.armorValue = armorValue;
        this.tex = texture;
        this.damage=pDamage;
        this.extraTime=extraTime;
        this.slot=slot;
        this.beast=beastName;
    }
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getArmorTexture(ItemStack stack) {
        if(this.isAmethystFreak(stack)){
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/"+beast.getBeastName()+"/armor/"+beast.getBeastName()+"_armor_"+slot.getName()+"_amethyst.png");
        }else if (this.isCopperFreak(stack)){
            return new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/"+beast.getBeastName()+"/armor/"+beast.getBeastName()+"_armor_"+slot.getName()+"_copper.png");
        }
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
    public Beast getBeast() {
        return this.beast;
    }

    public boolean isAmethystFreak(ItemStack stack){
        return stack.getHoverName().getString().equals("AmethystFreak") && !stack.is(ModItems.SCROUNGER_POUCH.get());
    }

    public boolean isCopperFreak(ItemStack stack){
        return stack.getHoverName().getString().equals("CopperFreak") && !stack.is(ModItems.SCROUNGER_POUCH.get());
    }

}
