package net.BKTeam.illagerrevolutionmod.potion;

import net.BKTeam.illagerrevolutionmod.network.PacketBleedingEffect;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketProcBleedingEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import org.jetbrains.annotations.NotNull;


@Mod.EventBusSubscriber
public class EffectBleeding extends MobEffect {
    public EffectBleeding(){
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect.illagerrevolutionmod.deep_wound";
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(amplifier < 2){
            if ((entity.isSprinting() || (!(entity instanceof Player || entity instanceof AbstractSkeleton || entity instanceof AbstractGolem || entity instanceof Blaze || entity instanceof Vex || entity instanceof WitherBoss))) ) {
                entity.hurt(DamageSource.GENERIC, 1);
                if(!entity.level.isClientSide){
                    sendBleeding(entity);
                }
            }else if(!(entity instanceof Player)){
                entity.removeEffect(this);
            }
        }else {
            if(!entity.level.isClientSide){
                sendProcBleeding(entity);
            }
            entity.level.playSound(null,entity.blockPosition(),SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.AMBIENT,8.0f,-2.0f);
            entity.level.playSound(null,entity.blockPosition(),ModSounds.BLEEDING_PROC.get(), SoundSource.AMBIENT,2.0f,1.0f);
            entity.hurt(DamageSource.GENERIC,7);
            entity.removeEffect(InitEffect.DEEP_WOUND.get());
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event){
        LivingEntity entity=event.getEntity();
        if(entity.hasEffect(InitEffect.DEEP_WOUND.get())){
            int ampli=entity.getEffect(InitEffect.DEEP_WOUND.get()).getAmplifier();
            if(ampli==1){
                event.setCanceled(true);
            }
        }
    }
    public static void sendBleeding(LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketBleedingEffect(player), player);
        }
        PacketHandler.sendToAllTracking(new PacketBleedingEffect(livingEntity),livingEntity);

    }

    public static void sendProcBleeding(LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketProcBleedingEffect(player), player);
        }
        PacketHandler.sendToAllTracking(new PacketProcBleedingEffect(livingEntity),livingEntity);

    }

}