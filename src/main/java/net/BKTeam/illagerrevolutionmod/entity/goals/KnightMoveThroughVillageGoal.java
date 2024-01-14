package net.BKTeam.illagerrevolutionmod.entity.goals;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class KnightMoveThroughVillageGoal extends Goal {
      private final KnightEntity raider;
      private final double speedModifier;
      private BlockPos poiPos;
      private final List<BlockPos> visited = Lists.newArrayList();
      private final int distanceToPoi;
      private boolean stuck;

      public KnightMoveThroughVillageGoal(KnightEntity p_37936_, double p_37937_, int p_37938_) {
         this.raider = p_37936_;
         this.speedModifier = p_37937_;
         this.distanceToPoi = p_37938_;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean canUse() {
         this.updateVisited();
         return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
      }

      private boolean isValidRaid() {
         return this.raider.hasActiveRaidOfOrder() && !this.raider.getRaidOfOrder().isOver();
      }

      private boolean hasSuitablePoi() {
         ServerLevel serverlevel = (ServerLevel)this.raider.level;
         BlockPos blockpos = this.raider.blockPosition();
         Optional<BlockPos> optional = serverlevel.getPoiManager().getRandom((p_219843_) -> {
            return p_219843_ == PoiType.HOME;
         }, this::hasNotVisited, PoiManager.Occupancy.ANY, blockpos, 48, this.raider.getRandom());
         if (!optional.isPresent()) {
            return false;
         } else {
            this.poiPos = optional.get().immutable();
            return true;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean canContinueToUse() {
         if (this.raider.getNavigation().isDone()) {
            return false;
         } else {
            return this.raider.getTarget() == null && !this.poiPos.closerToCenterThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
         }
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void stop() {
         if (this.poiPos.closerToCenterThan(this.raider.position(), (double)this.distanceToPoi)) {
            this.visited.add(this.poiPos);
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void start() {
         super.start();
         this.raider.setNoActionTime(0);
         this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
         this.stuck = false;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.raider.getNavigation().isDone()) {
            Vec3 vec3 = Vec3.atBottomCenterOf(this.poiPos);
            Vec3 vec31 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, vec3, (double)((float)Math.PI / 10F));
            if (vec31 == null) {
               vec31 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, vec3, (double)((float)Math.PI / 2F));
            }

            if (vec31 == null) {
               this.stuck = true;
               return;
            }

            this.raider.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, this.speedModifier);
         }

      }

      private boolean hasNotVisited(BlockPos p_37943_) {
         for(BlockPos blockpos : this.visited) {
            if (Objects.equals(p_37943_, blockpos)) {
               return false;
            }
         }

         return true;
      }

      private void updateVisited() {
         if (this.visited.size() > 2) {
            this.visited.remove(0);
         }

      }
}