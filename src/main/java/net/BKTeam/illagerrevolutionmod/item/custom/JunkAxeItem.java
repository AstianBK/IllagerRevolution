package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.BKTeam.illagerrevolutionmod.api.IItemCapability;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class JunkAxeItem extends AxeItem {

    public int upgrade = 0;
    private int count_hit = 0;

    private final float attackDamage;
    private final float attackSpeed;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public JunkAxeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        this.attackDamage=pAttackDamageModifier+pTier.getAttackDamageBonus();
        this.attackSpeed=pAttackSpeedModifier;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)pAttackSpeedModifier, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers=builder.build();
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        CompoundTag nbt = null;
        LivingEntity livingEntity= (LivingEntity) pEntity;
        if(pIsSelected && !Screen.hasShiftDown()){
            IItemCapability capability= CapabilityHandler.getItemCapability(pStack,CapabilityHandler.SWORD_CAPABILITY);
            if(capability!=null){
                this.upgrade = capability.getTier();
                this.count_hit = capability.getCountHit();
                nbt=pStack.getOrCreateTag();
            }
            if (nbt != null){
                nbt.putInt("CustomModelData",this.upgrade);
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier"+stack.getItem().getName(stack), (double)this.attackDamage+(double)this.upgrade*7, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed-2.3f*this.upgrade , AttributeModifier.Operation.ADDITION));
        if(slot == EquipmentSlot.MAINHAND) {
            return builder.build();
        }
        else{
            return super.getAttributeModifiers(slot, stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pPlayer.getOffhandItem().is(Items.IRON_INGOT) || pPlayer.getOffhandItem().is(ModItems.ILLAGIUM.get())){
            ItemStack itemStack = pPlayer.getOffhandItem();
            if(!pPlayer.level.isClientSide){
                IItemCapability capability = CapabilityHandler.getItemCapability(pPlayer.getMainHandItem(),CapabilityHandler.SWORD_CAPABILITY);
                if(capability!=null){
                    if(capability.getTier()<3){
                        pPlayer.level.playSound(null,pPlayer, SoundEvents.SMITHING_TABLE_USE, SoundSource.AMBIENT,1.0f,1.0f);
                        capability.setTier(capability.getTier()+1);
                        capability.setCountHit(15);
                    }
                }
            }
            if(!pPlayer.getAbilities().instabuild){
                itemStack.shrink(1);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if(!pAttacker.level.isClientSide){
            if(pAttacker instanceof Player player){
                IItemCapability capability = CapabilityHandler.getItemCapability(pStack,CapabilityHandler.SWORD_CAPABILITY);
                if(capability!=null){
                    if(capability.getTier()>0){
                        if(this.count_hit>0){
                            capability.setCountHit(this.count_hit-1);
                            this.count_hit=capability.getCountHit();
                            if(this.count_hit==0){
                                capability.setTier(capability.getTier()-1);
                                capability.setCountHit(capability.getTier()==0 ? 0 : 15);
                                player.level.playSound(null,player ,SoundEvents.LADDER_BREAK,SoundSource.AMBIENT,1.0f,1.0f);
                            }
                        }
                    }
                }
            }

        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.translatable("Remaining Hits:"+this.count_hit));
            CompoundTag nbt = null;
            IItemCapability capability= CapabilityHandler.getItemCapability(pStack,CapabilityHandler.SWORD_CAPABILITY);
            if(capability!=null){
                this.upgrade = capability.getTier();
                this.count_hit = capability.getCountHit();
                nbt=pStack.getOrCreateTag();
            }
            if(nbt!=null){
                nbt.putInt("CustomModelData",this.upgrade);
            }
        }else {
            CompoundTag nbt = null;
            IItemCapability capability= CapabilityHandler.getItemCapability(pStack,CapabilityHandler.SWORD_CAPABILITY);
            if(capability!=null){
                this.upgrade = capability.getTier();
                nbt=pStack.getOrCreateTag();
            }
            if(nbt!=null){
                nbt.putInt("CustomModelData",this.upgrade);
            }
            pTooltipComponents.add(Component.translatable("Press SHIFT"));
        }

    }
}
