package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.Patreon;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SummonedSoul;
import net.BKTeam.illagerrevolutionmod.Events;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;


import javax.annotation.Nullable;
import java.util.List;

public class SwordRuneBladeItem extends RunedSword {
    private final float attackDamage;
    private final float attackSpeed;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public SwordRuneBladeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        this.attackDamage=pAttackDamageModifier+pTier.getAttackDamageBonus();
        this.attackSpeed=pAttackSpeedModifier;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)pAttackSpeedModifier, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers=builder.build();

    }
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        CompoundTag nbt = null;
        if(entity instanceof Player player){
            int cc = (int) player.getAttribute(SoulTick.SOUL).getValue();
            int cc1 = this.isFrostRune(player,itemStack) ? 7 : 0;
            if (cc <= 6) {
                nbt = itemStack.getOrCreateTag();
                this.souls = cc;
            }
            if(nbt!=null){
                nbt.putInt("CustomModelData", this.souls + cc1);
            }
        } else if (entity instanceof BladeKnightEntity) {
            this.souls=0;
        }
        super.inventoryTick(itemStack,level,entity,p_41407_,p_41408_);
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage+(double)this.souls, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
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
            List<SoulEntity> listSoul=pPlayer.level.getEntitiesOfClass(SoulEntity.class,pPlayer.getBoundingBox().inflate(50.0d));
            List<ZombifiedEntity> listZombi=pPlayer.level.getEntitiesOfClass(ZombifiedEntity.class,pPlayer.getBoundingBox().inflate(50.0d),e->e.getOwner()==pPlayer);
            int k = Util.getNumberOfInvocations(listZombi);
            boolean flag1= pPlayer.isShiftKeyDown();
            boolean flag2= cc>=0 && cc<=6;
            boolean canSummon= Events.checkOwnerSoul(listSoul,pPlayer);
            if(!flag1 ){
                if(!pLevel.isClientSide && flag2 && cc>0){
                    SummonedSoul soul_wither= new SummonedSoul(pPlayer,pLevel);
                    pPlayer.level.playSound(null,pPlayer.blockPosition(),ModSounds.SOUL_RELEASE.get(),SoundSource.AMBIENT,3.0f,1.0f);
                    soul_wither.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
                    pLevel.addFreshEntity(soul_wither);
                }
                if (!pPlayer.getAbilities().instabuild && cc>0){
                    pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc-1);
                }
            }else {
                if(!pLevel.isClientSide){
                    if (flag2) {
                        int i = 0;
                        int j = 0;
                        Entity entity;
                        if (canSummon) {
                            while (i < listSoul.size() && j < 6 ) {
                                entity = listSoul.get(i);
                                if (entity instanceof SoulEntity entity1 && entity1.getOwner() == pPlayer) {
                                    entity1.spawUndead((ServerLevel) pPlayer.level, pPlayer, entity,this.isFrostRune(pPlayer,itemStack));
                                    j++;
                                    k++;
                                    if(k==12){
                                        pPlayer.level.playSound(pPlayer,pPlayer,SoundEvents.DISPENSER_FAIL,SoundSource.AMBIENT,2.0f,-3.0f);
                                    }
                                }
                                i++;
                            }
                        }else{
                            if (!listZombi.isEmpty() && cc<6 ) {
                                while (i<listZombi.size()){
                                    ZombifiedEntity zombie=listZombi.get(i);
                                    if(zombie.isAlive() && cc<6){
                                        zombie.setInvulnerable(false);
                                        zombie.hurt(DamageSource.playerAttack(pPlayer).bypassMagic().bypassArmor(),zombie.getMaxHealth());
                                        cc++;
                                        pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc);
                                        pPlayer.playSound(ModSounds.SOUL_ABSORB.get(),2.0f,1.0f);
                                        if(cc==6){
                                            pPlayer.playSound(ModSounds.SOUL_LIMIT.get(),5.0f,1.0f);
                                        }
                                    }
                                    i++;
                                }
                            }
                            pPlayer.level.playSound(pPlayer,pPlayer,SoundEvents.DISPENSER_FAIL,SoundSource.AMBIENT,2.0f,-3.0f);
                        }
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
            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.soulsiphon1"));
            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.soulsiphon2"));

            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.soulrelease1"));
            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.soulrelease2"));

            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.soulward"));

        } else {
            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.fleshtooltip1"));
            pTooltipComponents.add(new TranslatableComponent("tooltip.illagerrevolutionmod.illagium_runed_blade.fleshtooltip2"));

        }
    }
    
    
}
