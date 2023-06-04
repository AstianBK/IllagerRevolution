package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class IllagerBeastEntity extends TamableAnimal {


    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_PAINT_COLOR = SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PAINTED =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    IllagerBeastEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_PAINT_COLOR,-1);
        this.entityData.define(PAINTED,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isSitting", this.isSitting());
        compound.putBoolean("isPainted",this.isPainted());
        compound.putInt("color",this.getColor().getId());

    }
    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_PAINT_COLOR));
    }

    public void setColor(DyeColor pcolor) {
        this.entityData.set(DATA_PAINT_COLOR, pcolor.getId());
    }

    public boolean isPainted(){
        return this.entityData.get(PAINTED);
    }

    public void setPainted(boolean pBoolean){
        this.entityData.set(PAINTED,pBoolean);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSitting(compound.getBoolean("isSitting"));
        this.setPainted(compound.getBoolean("isPainted"));
        if (compound.contains("color", 99)) {
            this.setColor(DyeColor.byId(compound.getInt("color")));
        }
    }

    protected void updateContainerEquipment() {
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }
}
