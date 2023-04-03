package net.BKTeam.illagerrevolutionmod.procedures;

import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.entity.custom.Blade_KnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.Soul_Entity;
import net.BKTeam.illagerrevolutionmod.item.ModArmorMaterials;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;


import java.util.List;

@Mod.EventBusSubscriber
public class Events {
    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (event != null && event.getEntity() != null) {
            LivingEntity entity=event.getEntityLiving();
            if(entity instanceof ServerPlayer player && player.getMainHandItem().is(ModItems.ILLAGIUM_ALT_RUNED_BLADE.get())){
                if(player instanceof INecromancerEntity){
                    if(((INecromancerEntity)player).getBondedMinions()!=null){
                        if(!((INecromancerEntity)player).getBondedMinions().isEmpty()){
                            if(Util.checkIsOneLinked(((INecromancerEntity)player).getBondedMinions())){
                                player.getMainHandItem().hurtAndBreak(50,player,e->e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                                ((INecromancerEntity)player).getBondedMinions().forEach(knight->{
                                    if(knight.itIsLinked()){
                                        knight.hurt(event.getSource(),event.getAmount()*1/Util.getNumberOfLinked(((INecromancerEntity)knight.getOwner()).getBondedMinions()));
                                    }
                                });
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
            if(entity.hasEffect(init_effect.DEATH_MARK.get() ) && entity.level.random.nextInt(0,7)==1){
                for (int i=0;i<3;i++){
                    double xp=entity.getX()+entity.getRandom().nextDouble(-1.0,1.0);
                    double yp=entity.getY()+entity.getRandom().nextDouble(0.0d,2.0d);
                    double zp=entity.getZ()+entity.getRandom().nextDouble(-1.0,1.0);
                    entity.level.addParticle(ModParticles.RUNE_SOUL_PARTICLES.get(),xp,yp,zp,entity.getRandom().nextFloat(-0.1f,0.1f),-0.1F,entity.getRandom().nextFloat(-0.1f,0.1f));
                }
            }
        }
    }
    public static boolean checkOwnerSoul(List<Soul_Entity> list,LivingEntity owner){
        if(!list.isEmpty()){
            Entity entity=null;
            int i=0;
            while (entity==null && i<list.size()){
                if(list.get(i).getOwner()==owner){
                    entity=list.get(i);
                }
                i++;
            }
            return entity!=null;
        }
        return false;
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.RightClickItem event){
        LivingEntity livingEntity = event.getEntityLiving();
        if(livingEntity instanceof ServerPlayer player){
            ItemStack helmet=player.getItemBySlot(EquipmentSlot.HEAD);
            if(checkHelmetMiner(helmet.getItem()) && !player.hasEffect(MobEffects.LUCK)){
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
        }
    }

    private static boolean checkHelmetMiner(Item item){
        return item== ModItems.HELMET_MINER.get() || item==ModItems.HELMET_MINER_REINFORCED.get();
    }

    private static void hurtHelmet(ItemStack itemStack, Player player){
        float i=0;
        int j=((IllagiumArmorItem)itemStack.getItem()).getMaterial()== ModArmorMaterials.ILLAGIUM ? 2 : 10;

        if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,itemStack) !=0){
            i=percentDamageForEnchantmentLevel(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,itemStack));
        }
        int maxHurt= (int) (itemStack.getMaxDamage()+(itemStack.getMaxDamage()*(i)));
        itemStack.hurtAndBreak(maxHurt*j/100,player,e-> e.broadcastBreakEvent(EquipmentSlot.HEAD));
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
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity=event.getEntityLiving();
        Level world = entity.level;
        if (!(entity.hasEffect(init_effect.DEATH_MARK.get())) && Util.entitydeterminar(entity)!=null) {
            if (!world.getEntitiesOfClass(Blade_KnightEntity.class, AABB.ofSize(new Vec3(entity.getX(),entity.getY(),entity.getZ()), 50, 50, 50), e -> true).isEmpty()) {
                entity.addEffect(new MobEffectInstance(init_effect.DEATH_MARK.get(), 3000, 0));
                if(entity instanceof Player){
                    entity.level.playSound(null,entity.blockPosition(),ModSounds.DEATH_MARK_SOUND.get(), SoundSource.AMBIENT,2.0f,1.0f);
                }
            }
        }
    }
}