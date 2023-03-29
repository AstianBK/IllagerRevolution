package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.item.ModItems;

public class ArrowBeast extends AbstractArrow {

    public ArrowBeast(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
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
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(pResult.getEntity() instanceof LivingEntity livingEntity){
            int ampliEffect=livingEntity.hasEffect(init_effect.BLEEDING.get()) ? livingEntity.getEffect(init_effect.BLEEDING.get()).getAmplifier() : 0;
            int ampliBleeding=0;
            if(livingEntity.hasEffect(init_effect.BLEEDING.get()) && ampliEffect==1){
                ampliBleeding=2;
            }else if(livingEntity.hasEffect(init_effect.BLEEDING.get()) && ampliEffect==0){
                ampliBleeding=1;
            }
            livingEntity.addEffect(new MobEffectInstance(init_effect.BLEEDING.get(),100,ampliBleeding));
            if(this.level.random.nextInt(1,9)==1){
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON,100,0));
            }
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
