package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class VariantRuneBladeItem extends RunedSword{
    private double defense;

    private double knockbackres;
    private final float attackDamage;
    private final float attackSpeed;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private static final UUID ARMOR_RUNE_BLADE_ITEM=UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    public VariantRuneBladeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        this.defense=0;
        this.knockbackres=0;
        this.attackDamage=pAttackDamageModifier+pTier.getAttackDamageBonus();
        this.attackSpeed=pAttackSpeedModifier;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)pAttackSpeedModifier, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers=builder.build();
    }
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(itemStack,level,entity,p_41407_,p_41408_);
        CompoundTag nbt = null;
        if(entity instanceof Player player){
            this.defense=(double)this.souls*2.5d;
            this.knockbackres=(double)this.souls*0.025d;
            int cc1 = this.isFrostRune(player,itemStack) ? 7 : 0;
            if (this.souls <= 6) {
                nbt = itemStack.getOrCreateTag();
            }
            if(nbt!=null){
                nbt.putInt("CustomModelData", this.souls+cc1);
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed , AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_RUNE_BLADE_ITEM, "armor modifier", this.defense, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ARMOR_RUNE_BLADE_ITEM, "knockres modifier", this.knockbackres, AttributeModifier.Operation.ADDITION));
        if(slot == EquipmentSlot.MAINHAND) {
            return builder.build();
        }
        else{
            return super.getAttributeModifiers(slot, stack);
        }
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        int cc = (int) pPlayer.getAttribute(SoulTick.SOUL).getValue();
        if(pUsedHand==InteractionHand.MAIN_HAND){
            boolean flag1= pPlayer.isShiftKeyDown();
            boolean flag2= cc>=0 && cc<=6;
            if(!flag1 ){
                if(!pLevel.isClientSide && flag2 && cc>2 ){
                    BlockPos pos=pPlayer.getOnPos();
                    BlockPos pos1=new BlockPos(pos.getX()+pLevel.getRandom().nextInt(-1,1),pos.getY()+2.0d,pos.getZ()+pLevel.getRandom().nextInt(-1,1));
                    FallenKnightEntity fallenKnight=new FallenKnightEntity(ModEntityTypes.FALLEN_KNIGHT.get(),pLevel);
                    pPlayer.level.playSound(null,pPlayer.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.AMBIENT,25.0f,1.0f);
                    fallenKnight.setIdOwner(pPlayer.getUUID());
                    fallenKnight.setIsFrozen(this.isFrostRune(pPlayer,itemStack));
                    fallenKnight.finalizeSpawn((ServerLevelAccessor) pLevel,pLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED,null,null);
                    fallenKnight.setDispawnTimer(2500,pPlayer,false);
                    fallenKnight.moveTo(pos1,0.0f,0.0f);
                    pLevel.addFreshEntity(fallenKnight);
                    fallenKnight.addEntityOfList();
                    pPlayer.playSound(ModSounds.SOUL_LIMIT.get(),4.0f,1.0f);
                }
                if (!pPlayer.getAbilities().instabuild && cc>2){
                    pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc-3);
                }
            }else {
                if (!pLevel.isClientSide && flag2) {
                    List<FallenKnightEntity> knights=((INecromancerEntity)pPlayer).getBondedMinions();
                    boolean flag= Util.checkCanLink(knights);
                    if(!knights.isEmpty()) {
                        knights.forEach(knight ->{
                            if(!flag){
                                if(!knight.itIsLinked()){
                                    knight.setLink(true);
                                }
                            }else {
                                knight.setLink(false);
                            }
                        });
                    }
                }
                if (!pPlayer.getAbilities().instabuild){
                    //pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemStack,pLevel.isClientSide());
        }
        return super.use(pLevel,pPlayer,pUsedHand);
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.soulnourish1"));
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.soulnourish2"));

            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.bonesummon1"));
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.bonesummon2"));

            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.boneshield1"));
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.boneshield2"));

        } else {
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.bonetooltip1"));
            pTooltipComponents.add(Component.translatable("tooltip.illagerrevolutionmod.illagium_variant_runed_blade.bonetooltip2"));

        }
    }
}
