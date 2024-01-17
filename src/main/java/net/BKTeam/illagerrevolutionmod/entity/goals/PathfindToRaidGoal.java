package net.BKTeam.illagerrevolutionmod.entity.goals;

import com.google.common.collect.Sets;
import net.BKTeam.illagerrevolutionmod.orderoftheknight.TheKnightOrder;
import net.BKTeam.illagerrevolutionmod.orderoftheknight.TheKnightOrders;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PathfindToRaidGoal<T extends KnightEntity> extends Goal {
   private final T mob;
   private int recruitmentTick;

   public PathfindToRaidGoal(T pMob) {
      this.mob = pMob;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean canUse() {
      return this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasActiveRaidOfOrder() && !this.mob.getRaidOfOrder().isOver();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean canContinueToUse() {
      return this.mob.hasActiveRaidOfOrder() && !this.mob.getRaidOfOrder().isOver() && this.mob.level() instanceof ServerLevel;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.mob.hasActiveRaidOfOrder()) {
         TheKnightOrder raid = this.mob.getRaidOfOrder();
         if (this.mob.tickCount > this.recruitmentTick) {
            this.recruitmentTick = this.mob.tickCount + 20;
            this.recruitNearby(raid);
         }

         if (!this.mob.isPathFinding()) {
            Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf(raid.getCenter()), (double)((float)Math.PI / 2F));
            if (vec3 != null) {
               this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
            }
         }
      }

   }

   private void recruitNearby(TheKnightOrder pRaid) {
      if (pRaid.isActive()) {
         Set<KnightEntity> set = Sets.newHashSet();
         List<KnightEntity> list = this.mob.level().getEntitiesOfClass(KnightEntity.class, this.mob.getBoundingBox().inflate(16.0D), (p_25712_) -> {
            return !p_25712_.hasActiveRaidOfOrder() && TheKnightOrders.canJoinRaid(p_25712_, pRaid);
         });
         set.addAll(list);

         for(KnightEntity raider : set) {
            pRaid.joinRaid(pRaid.getGroupsSpawned(), raider, (BlockPos)null, true);
         }
      }

   }
}