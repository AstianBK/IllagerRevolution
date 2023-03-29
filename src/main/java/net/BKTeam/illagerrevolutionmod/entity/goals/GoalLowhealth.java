package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.BKTeam.illagerrevolutionmod.entity.custom.Blade_KnightEntity;

import java.util.EnumSet;

    public class GoalLowhealth extends Goal {
        private int durationGoal;
        private int countTick;
        private boolean firstUse;

        Blade_KnightEntity owner;

        public GoalLowhealth (Blade_KnightEntity entity, int pDuration){
            this.setFlags(EnumSet.of(Flag.MOVE,Flag.JUMP,Flag.LOOK));
            this.firstUse=false;
            this.setDurationGoal(pDuration);
            this.owner=entity;
        }
        @Override
        public boolean canUse() {
            return this.owner.isLowLife() && !this.firstUse;
        }


        public int getCountTick(){
            return this.countTick;
        }


        public void setFirstUse(boolean firstUse) {
            this.firstUse = firstUse;
        }

        @Override
        public void start() {
            this.owner.getNavigation().stop();
            this.countTick=this.durationGoal;
            this.owner.setStartAnimationLowHealth(true);
        }

        @Override
        public void stop() {
            this.owner.setInvulnerable(false);
            this.setFirstUse(true);
        }

        @Override
        public void tick() {
            this.countTick--;
            if(this.getCountTick()==0) {
                this.owner.getNavigation().stop();
                this.stop();
            }else {
                this.owner.getNavigation().stop();
                this.owner.setInvulnerable(true);
                if(this.owner.isFase2()){
                    this.stop();
                }
            }
        }
        public void setDurationGoal(int pDuration){
            this.durationGoal=pDuration;
        }
    }
