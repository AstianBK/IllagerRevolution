package net.BKTeam.illagerrevolutionmod.deathentitysystem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;

@Mod.EventBusSubscriber(modid = "illagerrevolutionmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulTick {
    public static final Attribute SOUL= new RangedAttribute("soul",0.0d,-Double.MAX_VALUE, Double.MAX_VALUE);

    @SubscribeEvent
    public static void soulDeathEvent(LivingDeathEvent event){
        Entity assasin=event.getSource().getEntity();
        if(assasin instanceof Player player){
            if(player.getMainHandItem().is(ModItems.ILLAGIUM_RUNED_BLADE.get())){
                if(!(event.getEntity() instanceof ZombifiedEntity entity && entity.getOwner()==assasin)){
                    if(player.getAttribute(SoulTick.SOUL).getValue()<6){
                        player.getAttribute(SoulTick.SOUL).setBaseValue(player.getAttribute(SoulTick.SOUL).getValue()+1);
                        event.getEntity().playSound(ModSounds.SOUL_ABSORB.get(),2.0f,1.0f);
                        if(player.getAttribute(SoulTick.SOUL).getValue()==6){
                            event.getEntity().playSound(ModSounds.SOUL_LIMIT.get(),4.0f,1.0f);
                        }
                    }
                }
                if(event.getEntity() instanceof ZombifiedEntity){
                    if(player.getAttribute(SoulTick.SOUL).getValue()<6){
                        player.getAttribute(SoulTick.SOUL).setBaseValue(player.getAttribute(SoulTick.SOUL).getValue()+1);
                        event.getEntity().playSound(ModSounds.SOUL_ABSORB.get(),2.0f,1.0f);
                        if(player.getAttribute(SoulTick.SOUL).getValue()==6){
                            player.getKillCredit().playSound(ModSounds.SOUL_LIMIT.get(),5.0f,1.0f);
                        }
                    }
                }
            }
        }
    }
}
