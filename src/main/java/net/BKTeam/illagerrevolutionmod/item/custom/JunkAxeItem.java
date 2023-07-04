package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSyncItemCapability;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
        CompoundTag nbt = pStack.getOrCreateTag();
        LivingEntity livingEntity= (LivingEntity) pEntity;
        if(pIsSelected && livingEntity instanceof Player){
            this.upgrade=nbt.getInt("upgrade");
            this.count_hit=nbt.getInt("countHit");
            nbt.putInt("CustomModelData",this.upgrade);
            if(!livingEntity.level.isClientSide){
                sendPacketAxe(pStack,nbt,livingEntity);
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        double cc=0;
        if (this.upgrade > 0 ){
            if (this.upgrade == 3 ){
                cc = 6.9D;
            } else if (this.upgrade == 2) {
                cc = 6.8D;
            } else if (this.upgrade == 1) {
                cc = 6.7D;
            }
        }
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage+(double)this.upgrade*7, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed-cc , AttributeModifier.Operation.ADDITION));
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
                if(this.upgrade<3){
                    pPlayer.level.playSound(null,pPlayer, SoundEvents.ALLAY_DEATH, SoundSource.HOSTILE,1.0F,1.0F);
                    CompoundTag nbt = pPlayer.getMainHandItem().getOrCreateTag();
                    this.upgrade++;
                    this.count_hit=5;
                    nbt.putInt("upgrade",this.upgrade);
                    nbt.putInt("countHit",this.count_hit);
                }
            }
            if(!pPlayer.getAbilities().instabuild && this.upgrade<3){
                itemStack.shrink(1);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if(pAttacker instanceof Player player){
            if(this.count_hit>0){
                CompoundTag nbt = pStack.getOrCreateTag();
                this.count_hit=this.count_hit-1;
                nbt.putInt("countHit",this.count_hit);
                if(this.count_hit==0){
                    player.level.playSound(null,player, SoundEvents.ALLAY_HURT, SoundSource.HOSTILE,1.0F,1.0F);
                    this.upgrade=this.upgrade-1;
                    nbt.putInt("upgrade",this.upgrade);
                    if(this.upgrade!=0){
                        this.count_hit=5;
                        nbt.putInt("countHit",this.count_hit);
                    }
                }
            }
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CompoundTag nbt = pStack.getOrCreateTag();
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.junk_axe"+this.count_hit));
            this.count_hit=nbt.getInt("countHit");
            this.upgrade=nbt.getInt("upgrade");
        }
    }

    public static void sendPacketAxe(ItemStack stack,CompoundTag tag,LivingEntity target) {
        if (target instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketSyncItemCapability(stack,tag), player);
        }
        PacketHandler.sendToAllTracking(new PacketSyncItemCapability(stack,tag),target);
    }
}
