package net.BKTeam.illagerrevolutionmod.potion;

import ca.weblite.objc.Message;
import net.BKTeam.illagerrevolutionmod.network.PacketBleedingEffect;
import net.BKTeam.illagerrevolutionmod.network.PacketEffectSwordRuned;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.setup.Messages;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import org.jetbrains.annotations.NotNull;


@Mod.EventBusSubscriber
public class Effect_bleeding extends MobEffect {
    public Effect_bleeding(){
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
                double xp=entity.getX()+entity.level.random.nextDouble(-0.4d,0.4d);
                double yp=entity.getY()+entity.level.random.nextDouble(0.0d,2.0d);
                double zp=entity.getZ()+entity.level.random.nextDouble(-0.4d,0.4d);
                entity.level.addParticle(ModParticles.BLOOD_PARTICLES.get(), xp, yp ,zp,  0.0f, -0.3f,0.0f);

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
            entity.removeEffect(init_effect.DEEP_WOUND.get());
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event){
        LivingEntity entity=event.getEntityLiving();
        if(entity.hasEffect(init_effect.DEEP_WOUND.get())){
            int ampli=entity.getEffect(init_effect.DEEP_WOUND.get()).getAmplifier();
            if(ampli==1){
                event.setCanceled(true);
            }
        }
    }

    public static void sendProcBleeding(LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            Messages.sendToPlayer(new PacketBleedingEffect(player), player);
        }
        Messages.sendToAllTracking(new PacketBleedingEffect(livingEntity),livingEntity);

    }

}