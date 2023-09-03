package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.api.IMauledCapability;
import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.BKTeam.illagerrevolutionmod.capability.MauledCapability;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulEntity;
import net.BKTeam.illagerrevolutionmod.item.ModArmorMaterials;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorIllusionerRobeItem;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorPillagerVestItem;
import net.BKTeam.illagerrevolutionmod.item.custom.ArmorVindicatorJacketItem;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSmoke;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class Events {
    @SubscribeEvent
    public static void initEffectPotion(MobEffectEvent.Added event){
        if(event.getEntity()!=null && event.getEffectInstance().getEffect()== InitEffect.MAULED.get()){
            if(!event.getEntity().level.isClientSide){
                LivingEntity entity=event.getEntity();
                IMauledCapability capability=CapabilityHandler.getEntityCapability(entity,CapabilityHandler.MAULED_CAPABILITY);
                if(capability!=null){
                    capability.setArmorTotal(capability.getArmorTotal());
                }
            }
        }
    }
    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (event != null && event.getEntity() != null) {
            Arrow bulletEntity=event.getSource().getDirectEntity() instanceof Arrow ? (Arrow) event.getSource().getDirectEntity() :null;
            LivingEntity attacker=event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
            LivingEntity entity=event.getEntity();
            if(attacker!=null){
                List<ScroungerEntity> listBirds=attacker.level.getEntitiesOfClass(ScroungerEntity.class,attacker.getBoundingBox().inflate(20.0D),e->e.getOwner()==attacker || e.getOwnerIllager()==attacker);
                if(!listBirds.isEmpty()){
                    for(ScroungerEntity scrounger:listBirds){
                        if(scrounger.nextAttack<=0){
                            scrounger.ordenAttack(entity);
                        }
                    }
                }
                if(entity!=null){
                    List<ScroungerEntity> listBirds1=attacker.level.getEntitiesOfClass(ScroungerEntity.class,attacker.getBoundingBox().inflate(20.0D),e->e.getOwner()==entity || e.getOwnerIllager()==entity);
                    for (ScroungerEntity scrounger : listBirds1){
                        if(scrounger.nextAttack<=0){
                            scrounger.ordenAttack(attacker);
                        }
                    }
                    List<SoulBomb> bombList =entity.level.getEntitiesOfClass(SoulBomb.class,entity.getBoundingBox().inflate(3.0d),e->e.getOwnerID()==entity.getId() && e.isDefender());
                    for(SoulBomb bomb : bombList){
                        bomb.expander();
                        event.setCanceled(true);
                    }
                }
            }
            if(attacker instanceof Player player){
                if (bulletEntity!=null){
                    if(player.getMainHandItem().is(Items.CROSSBOW)){
                        if(hasSetFullArmorPillager(player)){
                            event.setCanceled(true);
                            effectFullAmorPillager(player,entity,bulletEntity,event.getAmount());
                        }
                    }else if(player.getMainHandItem().is(Items.BOW)){
                        if(player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.ILLUSIONER_ROBE_ARMOR.get())){
                            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,200,0));
                            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,100,0));
                        }
                    }
                }else if(hasSetFullArmorVindicator(player)){
                    if(player.getMainHandItem().getItem() instanceof AxeItem){
                        event.setCanceled(true);
                        entity.hurt(DamageSource.GENERIC, event.getAmount()+2.0f);
                    }
                }else if(player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.EVOKER_ROBE_ARMOR.get())){
                    if(player.level.random.nextFloat() < 0.2f){
                        for(int i=0;i<9;i++){
                            float f = (float)Mth.atan2(entity.getZ() - player.getZ(), entity.getX() - player.getX());
                            double d2 = 1.25D * (double)(i + 1);
                            double f1 = Mth.cos(f)*d2;
                            double f2 = Mth.sin(f)*d2;
                            EvokerFangs fangs=new EvokerFangs(player.level,player.getOnPos().getX()+f1,player.getOnPos().getY()+1.0d,player.getOnPos().getZ()+f2,0f,10,player);
                            player.level.addFreshEntity(fangs);
                        }
                        player.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(1,player,e->e.broadcastBreakEvent(EquipmentSlot.CHEST));

                    }
                }
            }
            if(entity instanceof Player player){
                if(player.getMainHandItem().is(ModItems.ILLAGIUM_ALT_RUNED_BLADE.get())){
                    List <FallenKnightEntity> knights= player.level.getEntitiesOfClass(FallenKnightEntity.class,player.getBoundingBox().inflate(50.0d), e -> e.getOwner()==player);
                    if(!knights.isEmpty()){
                        if(Util.checkIsOneLinked(knights)){
                            player.getMainHandItem().hurtAndBreak((int) event.getAmount()*Util.getNumberOfLinked(knights),player, e->e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            for(FallenKnightEntity knight : knights){
                                if(knight.itIsLinked()){
                                    knight.hurt(event.getSource(),event.getAmount()*1/Util.getNumberOfLinked(knights));
                                }
                            }
                            event.setCanceled(true);
                        }
                    }
                }
                if(player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.EVOKER_ROBE_ARMOR.get())){
                    if(player.level.random.nextFloat() < 0.3f){
                        if(attacker!=null){
                            player.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(25,player,e->e.broadcastBreakEvent(EquipmentSlot.CHEST));
                            for(int i=0;i<3;i++){
                                for(int j=0;j<10;j++){
                                    float f4 = Mth.cos(2*j*10)*(0.5f*i);
                                    float f5 = Mth.sin(2*j*10)*(0.5f*i);
                                    EvokerFangs fangs=new EvokerFangs(player.level,attacker.getOnPos().getX()+(double)f4,attacker.getOnPos().getY()+1.0d,attacker.getOnPos().getZ()+(double)f5,0f,5,player);
                                    player.level.addFreshEntity(fangs);
                                }
                            }
                        }
                    }
                }
            }
            if(entity.hasEffect(InitEffect.DEATH_MARK.get() ) && entity.level.random.nextInt(0,7)==1){
                Random random=new Random();
                for (int i=0;i<3;i++){
                    double xp=entity.getX()+random.nextDouble(-1.0,1.0);
                    double yp=entity.getY()+random.nextDouble(0.0d,2.0d);
                    double zp=entity.getZ()+random.nextDouble(-1.0,1.0);
                    entity.level.addParticle(ModParticles.RUNE_SOUL_PARTICLES.get(),xp,yp,zp,random.nextFloat(-0.1f,0.1f),-0.1F,random.nextFloat(-0.1f,0.1f));
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderEvent(RenderLivingEvent.Pre<?,?> event){
        if(event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorIllusionerRobeItem){
            if(event.getEntity().getHealth()<event.getEntity().getMaxHealth()*20/100){
                event.setCanceled(true);

            }
        }
    }

    public static boolean hasSetFullArmorPillager(Player player){
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorPillagerVestItem &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof  ArmorPillagerVestItem &&
                player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof  ArmorPillagerVestItem;
    }

    public static boolean hasSetFullArmorVindicator(Player player){
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorVindicatorJacketItem &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof  ArmorVindicatorJacketItem;
    }

    public static void effectFullAmorPillager(Player player,LivingEntity living,Arrow arrow,float amount){
        living.hurt(DamageSource.arrow(arrow,null),amount+4.0f);
        arrow.discard();
    }

    public static boolean checkOwnerSoul(List<SoulEntity> list, LivingEntity owner){
        if(!list.isEmpty()){
            Entity entity=null;
            int i=0;
            while (entity==null && i<list.size()){
                if(list.get(i).getOwner()==owner){
                    entity=list.get(i);
                }
                i++;
            }
            if(owner instanceof Player player){
                if(player instanceof INecromancerEntity necromancer){
                    if(!necromancer.getInvocations().isEmpty()){
                        return entity!=null && necromancer.getInvocations().size()<12;
                    }
                }
            }
            return entity!=null;
        }
        return false;
    }
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event){
        LivingEntity livingEntity = event.getEntity();
        if(livingEntity instanceof ServerPlayer player){
            ItemStack helmet=player.getItemBySlot(EquipmentSlot.HEAD);
            ItemStack itemStack=event.getItemStack();
            if(checkHelmetMiner(helmet.getItem())){
                if(!player.hasEffect(MobEffects.LUCK)){
                    if(itemStack.getItem() == Items.EMERALD){
                        itemStack.shrink(1);
                        hurtHelmet(helmet,player);
                        player.addEffect(new MobEffectInstance(MobEffects.LUCK,150,0));
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,140,1));
                        SoundEvent Sound=player.level.getRandom().nextInt(0,2)==1 ? SoundEvents.AMETHYST_CLUSTER_BREAK:SoundEvents.AMETHYST_CLUSTER_HIT;
                        event.getEntity().level.playSound(null,event.getEntity().blockPosition(),Sound, SoundSource.AMBIENT,1.0f,-1.0f);
                    }
                }
                if(!player.hasEffect(MobEffects.INVISIBILITY)){
                    if(itemStack.getItem() == Items.AMETHYST_SHARD){
                        if(!player.level.isClientSide){
                            sendSmoke(player);
                        }
                        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,150,0));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,140,1));
                        itemStack.shrink(1);
                        hurtHelmet(helmet,player);
                        SoundEvent Sound=player.level.getRandom().nextInt(0,2)==1 ? SoundEvents.AMETHYST_CLUSTER_BREAK:SoundEvents.AMETHYST_CLUSTER_HIT;
                        event.getEntity().level.playSound(null,event.getEntity().blockPosition(),Sound, SoundSource.AMBIENT,1.0f,-1.0f);
                    }
                }
            }
        }
    }
    public static void sendSmoke(LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketSmoke(player), player);
        }
        PacketHandler.sendToAllTracking(new PacketSmoke(livingEntity),livingEntity);
    }

    private static boolean checkHelmetMiner(Item item){
        return item== ModItems.HELMET_MINER.get() || item==ModItems.HELMET_MINER_REINFORCED.get();
    }

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof LivingEntity){
            event.addCapability(new ResourceLocation(IllagerRevolutionMod.MOD_ID,"mauled"),new MauledCapability.AplastarProvider());
        }
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
    private static float percentDamageForEnchantmentLevel(int pLevel) {
        float i;
        if (pLevel == 1) {
            i = 0.25f;
        } else if (pLevel == 2) {
            i = 0.364f;
        } else {
            i = 0.429f;
        }
        return i;
    }
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level world = entity.level;
        if(entity instanceof ScroungerEntity scrounger){
            scrounger.level.getBlockEntity(scrounger.getOnPos(), BlockEntityType.JUKEBOX);
        }
        if (entity.hasEffect(InitEffect.MAULED.get())) {
            IMauledCapability capability= CapabilityHandler.getEntityCapability(entity,CapabilityHandler.MAULED_CAPABILITY);
            if(capability!=null){
                capability.onTick(entity,entity.getEffect(InitEffect.MAULED.get()));
            }
        }
        if (!(entity.hasEffect(InitEffect.DEATH_MARK.get())) && Util.entitydeterminar(entity) != null) {
            if (!world.getEntitiesOfClass(BladeKnightEntity.class, AABB.ofSize(new Vec3(entity.getX(), entity.getY(), entity.getZ()), 50, 50, 50), e -> true).isEmpty()) {
                entity.addEffect(new MobEffectInstance(InitEffect.DEATH_MARK.get(), 3000, 0));
                if (entity instanceof Player) {
                    entity.level.playSound(null, entity.blockPosition(), ModSounds.DEATH_MARK_SOUND.get(), SoundSource.AMBIENT, 2.0f, 1.0f);
                }
            }
        }
    }
}