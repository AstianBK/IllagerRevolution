package net.BKTeam.illagerrevolutionmod.procedures;

import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.entity.custom.Blade_KnightEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;

import java.util.Comparator;

@Mod.EventBusSubscriber
public class Util {

    public static Entity entitydeterminar(Entity entity){
        if(entity instanceof Player){
            return entity;
        }
        if(entity instanceof AbstractVillager){
            return entity;
        }
        if(entity instanceof  AbstractIllager){
            if(entity instanceof Blade_KnightEntity){
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
        if (maquina!=null &&!((LivingEntity)maquina).hasEffect(init_effect.DEATH_MARK.get())){
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
                entity = Util.Coord(souce.level,lol,souce);
            }
            boolean flag=entity==null || entity==souce;
            if(flag && z<=max) {
                entity = Util.Coord(souce.level,lol,souce);
            }
            if (flag && x<=max){
                entity = Util.Coord(souce.level,lol,souce);
            }
            if(flag && z<=max) {
                entity = Util.Coord(souce.level,lol,souce);
            }
        }
        return entity;

    }
}



