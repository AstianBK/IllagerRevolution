package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Entity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Summoned_Soul;
import net.BKTeam.illagerrevolutionmod.procedures.Events;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;


import java.util.List;

public class SwordRuneBladeItem extends SwordItem {
    public int tier1=0;
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
        int cc = (int) ((Player)entity).getAttribute(SoulTick.SOUL).getValue();
        if (cc <= 6) {
            nbt = itemStack.getOrCreateTag();
            this.tier1 = cc;
        }
        if(nbt!=null){
            nbt.putInt("CustomModelData", this.tier1);
        }
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage+(double)this.tier1, AttributeModifier.Operation.ADDITION));
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
            List<Soul_Entity> listSoul=pPlayer.level.getEntitiesOfClass(Soul_Entity.class,pPlayer.getBoundingBox().inflate(50.0d));
            List<ZombifiedEntity> listZombi=pPlayer.level.getEntitiesOfClass(ZombifiedEntity.class,pPlayer.getBoundingBox().inflate(50.0d));
            boolean flag1= pPlayer.isShiftKeyDown();
            boolean flag2= cc>=0 && cc<=6;
            boolean canSummon= Events.checkOwnerSoul(listSoul,pPlayer);
            if(!flag1 ){
                if(!pLevel.isClientSide && flag2 && cc>0 ){
                    Summoned_Soul soul_wither= new Summoned_Soul(pPlayer,pLevel);
                    pPlayer.level.playSound(null,pPlayer.blockPosition(),ModSounds.SOUL_RELEASE.get(),SoundSource.AMBIENT,3.0f,1.0f);
                    soul_wither.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
                    pLevel.addFreshEntity(soul_wither);

                }
                if (!pPlayer.getAbilities().instabuild && cc>0){
                    pPlayer.getAttribute(SoulTick.SOUL).setBaseValue(cc-1);
                }
            }else {
                if (!pLevel.isClientSide && flag2) {
                    int i = 0;
                    int j = 0;
                    Entity entity;
                    if (canSummon) {
                        while (i < listSoul.size() && j <= 5) {
                            entity = listSoul.get(i);
                            if (entity instanceof Soul_Entity entity1 && entity1.getOwner() == pPlayer) {
                                entity1.spawUndead((ServerLevel) pPlayer.level, pPlayer, entity);
                                j++;
                            }
                            i++;
                        }
                    }
                }
                if (!listZombi.isEmpty() && flag2 && !canSummon) {
                    int i=0;
                    while (i < listZombi.size()) {
                        if (listZombi.get(i).getOwner() == pPlayer) {
                            if (listZombi.get(i).isAlive()) {
                                listZombi.get(i).die(DamageSource.playerAttack(pPlayer));
                            }
                        }
                        i++;
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
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }
}
