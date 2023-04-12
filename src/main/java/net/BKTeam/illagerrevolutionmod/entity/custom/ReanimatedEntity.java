package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.BKTeam.illagerrevolutionmod.entity.goals.FollowOwnerGoalReanimate;
import net.BKTeam.illagerrevolutionmod.entity.goals.Owner_Attacking;
import net.BKTeam.illagerrevolutionmod.entity.goals.Owner_Defend;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ReanimatedEntity extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> ID_OWNER =
            SynchedEntityData.defineId(ReanimatedEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> ID_NECROMANCER =
            SynchedEntityData.defineId(ReanimatedEntity.class,EntityDataSerializers.OPTIONAL_UUID);

    protected ReanimatedEntity(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    public boolean isAlliedTo(@NotNull Entity pEntity) {
        if (this.getOwner()==pEntity) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof LivingEntity && ((LivingEntity) pEntity).getMobType() == MobType.ILLAGER  && this.getOwner()==null) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        }else if(pEntity instanceof Villager && getOwner()!=null){
            return true;
        }
        if(getOwner()!=null){
            if(pEntity instanceof ReanimatedEntity reanimated){
                return reanimated.getOwner()==this.getOwner();
            }
            return this.getOwner().isAlliedTo(pEntity);
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public LivingEntity getNecromancer(){
        List<Blade_KnightEntity> necromancers=this.level.getEntitiesOfClass(Blade_KnightEntity.class,this.getBoundingBox().inflate(100.0d));
        if(!necromancers.isEmpty()){
            return this.getIdNecromancer()!=null ? Util.getEntityForUUID(necromancers,this.getIdNecromancer()) : null ;
        }
        return null;
    }

    public LivingEntity getOwner(){
        if(this.getIdOwner()!=null){
            return this.level.getPlayerByUUID(getIdOwner());
        }
        return null;
    }

    public UUID getIdOwner() {
        return this.entityData.get(ID_OWNER).orElse((UUID)null);
    }

    public void setIdOwner(UUID idOwner){
        this.entityData.set(ID_OWNER, Optional.ofNullable(idOwner));
    }

    @Override
    protected void dropExperience() {
        if(this.getIdOwner()==null){
            super.dropExperience();
        }
    }

    public UUID getIdNecromancer() {
        return this.entityData.get(ID_NECROMANCER).orElse(null);
    }

    public void setIdNecromancer(UUID idOwner){
        this.entityData.set(ID_NECROMANCER,Optional.ofNullable(idOwner));
    }


    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getIdOwner() != null) {
            pCompound.putUUID("Owner", this.getIdOwner());
        }
        if(this.getIdNecromancer()!=null){
            pCompound.putUUID("IdNecromancer",this.getIdNecromancer());
        }
    }



    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        UUID uuid;
        UUID uuid2;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(Objects.requireNonNull(this.getServer()), s);
        }
        if (uuid != null) {
            this.setIdOwner(uuid);
        }
        if (pCompound.hasUUID("IdNecromancer")) {
            uuid2 = pCompound.getUUID("IdNecromancer");
        } else {
            String s = pCompound.getString("IdNecromancer");
            uuid2 = OldUsersConverter.convertMobOwnerIfNecessary(Objects.requireNonNull(this.getServer()), s);
        }
        if (uuid2 != null) {
            this.setIdNecromancer(pCompound.getUUID("IdNecromancer"));
        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    @Override
    public Team getTeam() {
        if(this.getOwner()!=null){
            return this.getOwner().getTeam();
        }
        return super.getTeam();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_OWNER, Optional.empty());
        this.entityData.define(ID_NECROMANCER,Optional.empty());
    }
}
