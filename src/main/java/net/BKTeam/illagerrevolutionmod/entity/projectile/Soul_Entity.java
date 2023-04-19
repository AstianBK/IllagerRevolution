package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.api.INecromancerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.procedures.Event_Death;


public class Soul_Entity extends ThrowableItemProjectile {

    int life;
    String soul;

    private boolean canUp;
    private double PosYMax;

    private double PosYMin;


    public Soul_Entity(EntityType<? extends Soul_Entity> type, Level world) {
        super(type,world);
        this.setNoGravity(true);
        this.life=0;
    }

    public Soul_Entity(LivingEntity thrower, Level level,String soul,LivingEntity owner,double ori) {
        super(ModEntityTypes.SOUL_ENTITY.get(),thrower,level);
        this.setOwner(owner);
        this.setNoGravity(true);
        this.PosYMax=ori+0.2d;
        this.PosYMin=ori-0.2d;
        this.canUp=false;
        this.soul=soul;
        this.life=0;

    }
    public Soul_Entity(Level world, double x, double y, double z) {
        super(ModEntityTypes.SOUL_ENTITY.get(), x, y, z, world);
        this.setNoGravity(true);
        this.life=0;
    }

    public void setSoul(String soul) {
        this.soul = soul;
    }

    public String getSoul() {
        return this.soul;
    }

    public void setRangeMoveY(double posYMax,double posYMin) {
        this.PosYMin=posYMin;
        this.PosYMax=posYMax;
    }
    public boolean checkOwnerAlive(LivingEntity owner){
        if(owner!=null){
            double y=owner.getY();
            double d1=this.distanceTo(owner);
            return owner.isAlive() || d1<20 ||this.getY()-y<10;
        }
        return false;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SOUL_WITHER.get();
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    @Override
    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("soul",this.soul);
        pCompound.putDouble("posYmax",this.PosYMax);
        pCompound.putDouble("posYmin",this.PosYMin);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setSoul(pCompound.getString("soul"));
        setRangeMoveY(pCompound.getDouble("posYmax"),pCompound.getDouble("posYmin"));
    }


    @Override
    public void tick() {
        if (this.level.isClientSide) {
            if(this.level.getRandom().nextInt(0,4)==0){
                this.level.addParticle(ModParticles.BKSOULS_PARTICLES.get(), this.getX(), this.getY()+0.15D, this.getZ(), 0.0D, 0.1D, 0.0D);
            }
        }
        if(!this.level.isClientSide){
            if(!(this.getY()>=this.PosYMin && this.getY()<=this.PosYMax)){
                if(this.getY()>= this.PosYMax){
                    this.canUp=false;
                }else if(this.getY()<=this.PosYMin){
                    this.canUp=true;
                }
            }
            if(this.canUp){
                this.yo+=0.02D;
            }else{
                this.yo-=0.02D;
            }
            BlockPos blockPos=this.getOnPos();
            BlockState state=this.getBlockStateOn();
            while (this.isColliding(blockPos,state)){
                this.moveTo(this.getX()+this.level.getRandom().nextInt(-1,1),this.getY(),this.getZ()+(this.level.getRandom().nextInt(-1,1)));
            }

        }
        this.moveTo(this.xo,this.yo,this.zo);
        if(!checkOwnerAlive((LivingEntity) this.getOwner())){
            discard();
        }

    }
    @Override
    protected float getGravity() {
        return 0.0F;
    }


    public void spawUndead(ServerLevel world, LivingEntity pSummoner, Entity Source){
        String undead=this.getSoul();
        boolean flag1= Event_Death.hasNameSoul(undead);
        if(flag1){
            ZombifiedEntity entity=new ZombifiedEntity(ModEntityTypes.ZOMBIFIED.get(),world);
            BlockPos blockpos = Source.blockPosition();
            entity.setIdSoul(undead);
            entity.setIdOwner(pSummoner.getUUID());
            entity.moveTo(blockpos,0.0F,0.0F);
            entity.finalizeSpawn(world, world.getCurrentDifficultyAt(pSummoner.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
            pSummoner.level.addFreshEntity(entity);
            if(pSummoner instanceof Player){
                entity.addEntityOfList();
            }else {
                entity.addEffect(new MobEffectInstance(init_effect.DEATH_MARK.get(),99999,0));
            }
        }else if(undead.equals("villager") || undead.equals("zombie_villager")){
            ZombieVillager entity=new ZombieVillager(EntityType.ZOMBIE_VILLAGER,world);
            BlockPos blockpos = Source.blockPosition();
            entity.moveTo(blockpos,0.0F,0.0F);
            entity.addEffect(new MobEffectInstance(init_effect.DEATH_MARK.get(),99999,0));
            entity.finalizeSpawn(world, world.getCurrentDifficultyAt(pSummoner.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
            pSummoner.level.addFreshEntity(entity);
        }else {
            Zombie entity=new Zombie(EntityType.ZOMBIE,world);
            BlockPos blockpos = Source.blockPosition();
            entity.moveTo(blockpos,0.0F,0.0F);
            entity.addEffect(new MobEffectInstance(init_effect.DEATH_MARK.get(),99999,0));
            entity.finalizeSpawn(world, world.getCurrentDifficultyAt(pSummoner.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
            pSummoner.level.addFreshEntity(entity);
        }
        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED,0.35F, -1.5F);
        discard();
    }
}
