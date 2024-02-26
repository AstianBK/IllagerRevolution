package net.BKTeam.illagerrevolutionmod.procedures;

import net.BKTeam.illagerrevolutionmod.ModConstants;
import net.BKTeam.illagerrevolutionmod.api.IAbilityKnightCapability;
import net.BKTeam.illagerrevolutionmod.capability.CapabilityHandler;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Util {

    public static Entity entitydeterminar(Entity entity){
        if(entity instanceof Player){
            return entity;
        }
        if(entity instanceof AbstractVillager){
            return entity;
        }
        if(entity instanceof  AbstractIllager){
            if(entity instanceof BladeKnightEntity){
                return null;
            }else{
                return entity;
            }

        }
        return null;
    }
    public static boolean isItemRob(Item item){
        return item==Items.DIAMOND || item==Items.RAW_IRON || item==Items.RAW_GOLD || item==Items.EMERALD || item==ModItems.ILLAGIUM.get();
    }
    public static int mineralId(Item item){
        if(item==Items.DIAMOND){
            return 0;
        }if(item==Items.RAW_GOLD){
            return 1;
        }if(item==Items.RAW_IRON){
            return 2;
        }if(item==Items.EMERALD) {
            return 3;
        }
        return 4;

    }
    public static Item selectItem(int pId){
        if(pId==0){
            return Items.DIAMOND;
        }if(pId==1){
            return Items.RAW_GOLD;
        }if(pId==2){
            return Items.RAW_IRON;
        }if(pId==3) {
            return Items.EMERALD;
        }if(pId==4){
            return ModItems.ILLAGIUM.get();
        }
        return null;
    }
    public static boolean detectorTier(TieredItem tieredItem,int pMin){
        return tieredItem.getTier().getLevel()<pMin;
    }

    public static <T extends Entity> Entity Coord(LevelAccessor world, Class<T> lol,Entity entity) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        Entity maquina =(Entity) world.getEntitiesOfClass(lol, AABB.ofSize(new Vec3(x, y, z), 50, 50, 50), e -> true).stream()
                .sorted(new Object() {
                    Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                        return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                    }
                }.compareDistOf(x, y, z)).findFirst().orElse(null);
        if (maquina!=null &&!((LivingEntity)maquina).hasEffect(InitEffect.DEATH_MARK.get())){
            return maquina;
        }
        return null;
    }
    public static <T extends Entity> Entity Entity(Entity souce, Class<T> lol){
        Entity entity=null;

        int x=0;
        int z=0;
        int max=10;

        while((entity==null || entity==souce) && (x<=max || z<=max)) {
            x++;
            z++;
            if(x<=max) {
                entity = Util.Coord(souce.level(),lol,souce);
            }
            boolean flag=entity==null || entity==souce;
            if(flag && z<=max) {
                entity = Util.Coord(souce.level(),lol,souce);
            }
            if (flag && x<=max){
                entity = Util.Coord(souce.level(),lol,souce);
            }
            if(flag && z<=max) {
                entity = Util.Coord(souce.level(),lol,souce);
            }
        }
        return entity;

    }
    public static boolean checkCanLink(List<FallenKnightEntity> knights){
        int i=0;
        int j=0;
        while (i<knights.size()){
            if (knights.get(i).itIsLinked()){
                j++;
            }
            i++;
        }
        return j==knights.size() ;
    }
    public static boolean checkIsOneLinked(List<FallenKnightEntity> knights){
        int i=0;
        int j=0;
        if(knights!=null){
            while (i<knights.size()){
                if (knights.get(i).itIsLinked() && knights.get(i).isArmed()){
                    j++;
                }
                i++;
            }
        }
        return j>0 ;
    }
    public static int getNumberOfLinked(List<FallenKnightEntity> knights){
        int i=0;
        int j=0;
        while (i<knights.size()){
            if (knights.get(i).itIsLinked() && knights.get(i).isArmed()){
                j++;
            }
            i++;
        }
        return j;
    }

    public static <T extends LivingEntity>LivingEntity getEntityForUUID(List<T> list, UUID uuid){
        int i=0;
        LivingEntity entity=null;
        while (i<list.size() && entity==null){
            if (list.get(i).getUUID().equals(uuid)){
                entity=list.get(i);
            }
            i++;
        }
        return entity;
    }
    public static void spawFallenKnightBack(Level level, LivingEntity livingEntity,int number){
        List<BlockPos> pos=blockPosList(livingEntity,number);
        for(int i=0;i<number;i++){
            FallenKnightEntity knight=new FallenKnightEntity(ModEntityTypes.FALLEN_KNIGHT.get(),level);
            knight.spawnAnim();
            knight.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(level.random.nextFloat() < 0.5 ? ModItems.ILLAGIUM_AXE.get() : ModItems.ILLAGIUM_SWORD.get()));
            knight.setIdNecromancer(livingEntity.getUUID());
            knight.moveTo(pos.get(i),0.0f,0.0f);
            level.addFreshEntity(knight);
            ((BladeKnightEntity)livingEntity).getKnights().add(knight);
        }
    }
    public static void spawZombifiedBack(Level level, LivingEntity livingEntity,int number){
        List<BlockPos> pos=blockPosList(livingEntity,number);
        for(int i=0;i<number;i++){
            ZombifiedEntity zombie=new ZombifiedEntity(ModEntityTypes.ZOMBIFIED.get(),level);
            zombie.setIdSoul(ModConstants.LIST_NAME_ZOMBIFIED.get(zombie.level().random.nextInt(0,4)));
            zombie.spawnAnim();
            zombie.addEffect(new MobEffectInstance(InitEffect.DEATH_MARK.get(),999999,0));
            zombie.moveTo(pos.get(i),0.0f,0.0f);
            level.addFreshEntity(zombie);
        }
    }
    public static List<BlockPos> blockPosList(LivingEntity livingEntity,int number){
        List<BlockPos> list=new ArrayList<>();
        int i=0;
        int j=1;
        double k=0.5d;
        float f = livingEntity.yBodyRot * ((float) Math.PI / 180F) + Mth.cos((float) livingEntity.tickCount * 0.6662F) * 0.25F;
        float f1 = Mth.cos(f);
        float f2 = Mth.sin(f);
        while (i<number){
            list.add(new BlockPos((int) (livingEntity.getX()+j+(f1*k)), (int) livingEntity.getY(), (int) (livingEntity.getZ()-j+(f2*k))));
            i++;
            j*=-1;
            if(j==1){
                k+=0.3d;
            }
        }
        return list;
    }
    public static int getNumberOfInvocations(List<ZombifiedEntity> zombies){
        return zombies.size();
    }
    public static IAbilityKnightCapability getCapability (LivingEntity living){
        IAbilityKnightCapability knightCapability = CapabilityHandler.getEntityCapability(living,CapabilityHandler.ABILITY_KNIGHT_CAPABILITY);
        return knightCapability;
    }
}



