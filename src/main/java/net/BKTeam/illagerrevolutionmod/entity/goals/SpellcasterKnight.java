package net.BKTeam.illagerrevolutionmod.entity.goals;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class SpellcasterKnight extends AbstractIllager {
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterKnight.class, EntityDataSerializers.BYTE);
    protected int spellCastingTickCount;
    private SpellcasterKnight.IllagerSpell currentSpell = SpellcasterKnight.IllagerSpell.NONE;

    protected SpellcasterKnight(EntityType<? extends SpellcasterKnight> p_33724_, Level p_33725_) {
        super(p_33724_, p_33725_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.spellCastingTickCount = pCompound.getInt("SpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SpellTicks", this.spellCastingTickCount);
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
        }
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setIsCastingSpell(SpellcasterKnight.IllagerSpell pSpellType) {
        this.currentSpell = pSpellType;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte)pSpellType.id);
    }

    protected SpellcasterKnight.IllagerSpell getCurrentSpell() {
        return !this.level.isClientSide ? this.currentSpell : SpellcasterKnight.IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }

    }
    public void tick() {
        super.tick();
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    protected static enum IllagerSpell {
        NONE(0, 0.0D, 0.0D, 0.0D),
        SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
        FANGS(2, 0.4D, 0.3D, 0.35D),
        WOLOLO(3, 0.7D, 0.5D, 0.2D),
        DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
        BLINDNESS(5, 0.1D, 0.1D, 0.2D);

        final int id;
        final double[] spellColor;

        private IllagerSpell(int p_33754_, double p_33755_, double p_33756_, double p_33757_) {
            this.id = p_33754_;
            this.spellColor = new double[]{p_33755_, p_33756_, p_33757_};
        }

        public static SpellcasterKnight.IllagerSpell byId(int pId) {
            for(SpellcasterKnight.IllagerSpell spellcasterillager$illagerspell : values()) {
                if (pId == spellcasterillager$illagerspell.id) {
                    return spellcasterillager$illagerspell;
                }
            }

            return NONE;
        }
    }

    protected class SpellcasterCastingSpellGoal extends Goal {
        public SpellcasterCastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }


        public boolean canUse() {
            return SpellcasterKnight.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            SpellcasterKnight.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            SpellcasterKnight.this.setIsCastingSpell(SpellcasterKnight.IllagerSpell.NONE);
        }

        public void tick() {
            if (SpellcasterKnight.this.getTarget() != null) {
                SpellcasterKnight.this.getLookControl().setLookAt(SpellcasterKnight.this.getTarget(), (float) SpellcasterKnight.this.getMaxHeadYRot(), (float) SpellcasterKnight.this.getMaxHeadXRot());
            }

        }
    }

    protected abstract class SpellcasterUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = SpellcasterKnight.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (SpellcasterKnight.this.isCastingSpell()) {
                    return false;
                } else {
                    return SpellcasterKnight.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SpellcasterKnight.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SpellcasterKnight.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = SpellcasterKnight.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                SpellcasterKnight.this.playSound(soundevent, 1.0F, 1.0F);
            }

            SpellcasterKnight.this.setIsCastingSpell(this.getSpell());
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                SpellcasterKnight.this.playSound(SpellcasterKnight.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract SpellcasterKnight.IllagerSpell getSpell();
    }
}

