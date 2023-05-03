package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMap;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSmoke;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.BKTeam.illagerrevolutionmod.item.ModArmorMaterials;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Map;

public class IllagiumArmorItem extends GeoArmorItem implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public IllagiumArmorItem(ModArmorMaterials material, EquipmentSlot slot, Properties settings) {
        super(material, slot, settings);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<IllagiumArmorItem>(this, "controller",
                20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        //event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
        return PlayState.CONTINUE;
    }




    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.RightClickItem event){
        LivingEntity livingEntity = event.getEntity();
        if(livingEntity instanceof ServerPlayer player){
            ItemStack helmet=player.getItemBySlot(EquipmentSlot.HEAD);
            if(checkHelmetMiner(helmet.getItem())){
                if(!player.hasEffect(MobEffects.LUCK)){
                    if(event.getItemStack().getItem()== Items.EMERALD){
                        ItemStack itemStack=event.getItemStack();
                        itemStack.shrink(1);
                        hurtHelmet(helmet,player);
                        player.addEffect(new MobEffectInstance(MobEffects.LUCK,150,0));
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,140,1));
                        SoundEvent Sound=player.level.getRandom().nextInt(0,2)==1 ? SoundEvents.AMETHYST_CLUSTER_BREAK:SoundEvents.AMETHYST_CLUSTER_HIT;
                        event.getEntity().level.playSound(null,event.getEntity().blockPosition(),Sound, SoundSource.AMBIENT,1.0f,-1.0f);
                    }
                }
                if(!player.hasEffect(MobEffects.INVISIBILITY)){
                    if(event.getItemStack().getItem()== Items.AMETHYST_SHARD){
                        if(!player.level.isClientSide){
                            PacketHandler.sendToPlayer(new PacketSmoke(player),player);
                        }
                        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,150,0));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,140,1));
                        ItemStack itemStack=event.getItemStack();
                        itemStack.shrink(1);
                        hurtHelmet(helmet,player);
                        SoundEvent Sound=player.level.getRandom().nextInt(0,2)==1 ? SoundEvents.AMETHYST_CLUSTER_BREAK:SoundEvents.AMETHYST_CLUSTER_HIT;
                        event.getEntity().level.playSound(null,event.getEntity().blockPosition(),Sound, SoundSource.AMBIENT,1.0f,-1.0f);
                    }
                }
            }
        }
    }

    private static boolean checkHelmetMiner(Item item){
        return item instanceof IllagiumArmorItem;
    }

    private static void hurtHelmet(ItemStack itemStack, Player player){
        float i=0;
        int j=((IllagiumArmorItem)itemStack.getItem()).getMaterial()== ModArmorMaterials.ILLAGIUM ? 2 : 10;

        if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,itemStack) !=0){
            i=percentDamageForEnchantmentLevel(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,itemStack));
        }
        int maxHurt= (int) (itemStack.getMaxDamage()+(itemStack.getMaxDamage()*(i)));
        itemStack.hurtAndBreak(maxHurt*j/100,player,e -> e.broadcastBreakEvent(EquipmentSlot.HEAD));
    }

    private static float percentDamageForEnchantmentLevel(int pLevel){
        float i;
        if(pLevel==1){
            i=0.25f;
        }
        else if(pLevel==2) {
            i = 0.364f;
        }else {
            i=0.429f;
        }
        return  i;
    }
}