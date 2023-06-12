package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.BKTeam.illagerrevolutionmod.deathentitysystem.SoulTick;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.custom.*;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulProjectile;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketEffectSwordRuned;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


@Mod.EventBusSubscriber
public class EventDeath {

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event != null && event.getEntity() != null) {
            upSouls(event.getEntity().level,event.getEntity(),event.getSource().getEntity());
            DamageSource pSource=event.getSource();
            LivingEntity entity=event.getEntity();

            if(entity instanceof  Player){
                ItemStack stack=entity.getMainHandItem().copy();
                if (entity.isDeadOrDying() && checkSword(pSource,entity)){
                    event.setCanceled(true);
                    sendRunedBladePacket(stack,entity);
                    giveUseStatAndCriterion(stack,(ServerPlayer) entity);
                }
            }

            if(entity instanceof AbstractIllager illager){
                List<ScroungerEntity> scroungerEntities = illager.level.getEntitiesOfClass(ScroungerEntity.class,illager.getBoundingBox().inflate(40.0D),e->e.getOwnerIllager()==entity);
                for (ScroungerEntity scrounger : scroungerEntities ){
                    scrounger.setOwnerIllager(null);
                }
            }
        }
    }
    public static void upSouls(LevelAccessor world, Entity entity,Entity assasin) {
        upSouls(null, world, entity,assasin);
    }

    private static void upSouls(@Nullable Event event, LevelAccessor world, Entity entity,Entity assasin) {
        LivingEntity souce=(LivingEntity) Util.Entity(entity, BladeKnightEntity.class);

        if (entity == null)
            return;
        if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(InitEffect.DEATH_MARK.get())) && !world
                .getEntitiesOfClass(BladeKnightEntity.class, AABB.ofSize(new Vec3((entity.getX()), (entity.getY()), (entity.getZ())), 100, 100, 100), e -> true)
                .isEmpty() && !(assasin instanceof Zombie)) {
            boolean flag=true;
            String name=entity.getName().getString();
            if(_livEnt instanceof ZombifiedEntity zombified_evokerEntity){
                name=zombified_evokerEntity.getIdSoul();
                flag=!(zombified_evokerEntity.getOwner() instanceof Player);
            }
            if(flag){
                SoulProjectile soul_projectile= new SoulProjectile((LivingEntity) entity,entity.level,souce);
                SoulEntity soul_entity = new SoulEntity(_livEnt,entity.level,name,souce,_livEnt.getY()+1.0D);
                entity.level.addFreshEntity(soul_projectile);
                entity.level.addFreshEntity(soul_entity);
                if(entity instanceof Player){
                    entity.level.getEntitiesOfClass(Monster.class,entity.getBoundingBox().inflate(50.0d),e-> e.getMobType()==MobType.UNDEAD).forEach(undead->{
                        boolean flag1=true;
                        if(undead instanceof ZombifiedEntity){
                            flag1=!(((ZombifiedEntity)undead).getOwner() instanceof Player);
                        }
                        if (flag1){
                            Random random = new Random();
                            undead.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,600,1));
                            undead.heal(undead.getMaxHealth()-undead.getHealth());
                            undead.level.addParticle(ParticleTypes.HEART,undead.getX(),undead.getY()+undead.getBbHeight()+0.5d,undead.getZ(),0.0d,0.1d,0.0d);
                            for(int j=0;j<5;j++){
                                double xp=undead.getX()+random.nextDouble(-1.0,1.0);
                                double yp=undead.getY()+random.nextDouble(0.5d,2.0d);
                                double zp=undead.getZ()+random.nextDouble(-1.0,1.0);
                                undead.level.addParticle(ParticleTypes.TOTEM_OF_UNDYING,undead.getX()+xp,undead.getY()+yp,undead.getZ()+zp,0.0d,0.2d,0.0d);
                            }
                        }
                    });
                }
            }

        }
    }
    public static boolean hasNameSoul(String soul){
        return ModConstants.LIST_NAME_ZOMBIFIED.contains(soul);
    }


    private static boolean checkSword(DamageSource damageSource,LivingEntity souce){
        if(damageSource.isBypassInvul()){
            return false;
        }else{
            ItemStack itemstack = null;
            ItemStack itemStack1=souce.getMainHandItem();
            if(itemStack1.is(ModItems.ILLAGIUM_RUNED_BLADE.get()) && souce instanceof Player player && player.getAttribute(SoulTick.SOUL).getValue()==6){
                itemstack=new ItemStack(ModItems.ILLAGIUM_RUNED_BLADE.get());
                int i=0;
                int cc=50;
                if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,itemStack1) !=0){
                    i=EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,itemStack1);
                    if(i==2){
                        cc=35;
                    }else if(i==3){
                        cc=25;
                    }
                }
                int damX=itemStack1.getMaxDamage()*(1+i);
                itemStack1.hurtAndBreak(damX*cc/100, (ServerPlayer) souce,e-> e.broadcastBreakEvent(souce.swingingArm));
            }

            if (itemstack != null) {
                ServerPlayer serverplayer = (ServerPlayer) souce;
                souce.setHealth(6.0F);
                serverplayer.getAttribute(SoulTick.SOUL).setBaseValue(0);
                souce.removeAllEffects();
                souce.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                souce.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 5));
                souce.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 3));
                //souce.level.broadcastEntityEvent(souce, (byte)35);
        }
            return itemstack != null ;
        }
    }
    public static void sendRunedBladePacket(ItemStack stack, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketEffectSwordRuned(stack, player), player);
        }
        PacketHandler.sendToAllTracking(new PacketEffectSwordRuned(stack, livingEntity), livingEntity);
    }
    public static void giveUseStatAndCriterion(ItemStack stack, ServerPlayer player) {
        if (!stack.isEmpty()) {
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
    }
}



