package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArrowBeast extends AbstractArrow {

    public ArrowBeast(EntityType<? extends ArrowBeast> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public ArrowBeast(Level pLevel, LivingEntity pShooter) {
        super(ModEntityTypes.ARROWBEAST.get(),pShooter,pLevel);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.ARROW_BEAST.get());
    }

    @Override
    protected void doPostHurtEffects(LivingEntity livingEntity) {
        super.doPostHurtEffects(livingEntity);
        if(!this.level().isClientSide){
            int ampliEffect=livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) ? livingEntity.getEffect(InitEffect.DEEP_WOUND.get()).getAmplifier() : 0;
            int ampliBleeding=0;
            if(livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) && ampliEffect==1){
                ampliBleeding=2;
            }else if(livingEntity.hasEffect(InitEffect.DEEP_WOUND.get()) && ampliEffect==0){
                ampliBleeding=1;
            }
            livingEntity.addEffect(new MobEffectInstance(InitEffect.DEEP_WOUND.get(),100,ampliBleeding));
        }
    }
}
