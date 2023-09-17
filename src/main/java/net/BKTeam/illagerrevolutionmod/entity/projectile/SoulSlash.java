package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.BKTeam.illagerrevolutionmod.enchantment.BKMobType;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SoulSlash extends ThrowableProjectile {

    private int life;
    public SoulSlash(EntityType<? extends SoulSlash> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
        this.life = 30;
    }

    public SoulSlash(LivingEntity thrower, Level level) {
        super(ModEntityTypes.SOUL_SLASH.get(),thrower,level);
        this.setNoGravity(true);
        this.life = 30;
    }

    @Override
    public void tick() {
        Vec3 deltaMovement = this.getDeltaMovement();
        super.tick();
        this.setDeltaMovement(deltaMovement);
        if (!this.level.isClientSide()) {
            HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (result.getType() == HitResult.Type.MISS && this.isAlive()) {
                List<Entity> intersecting = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(1.0F,3.0F,1.0F), this::canHitEntity);
                if (!intersecting.isEmpty())
                    this.onHit(new EntityHitResult(intersecting.get(0)));
            }
        }
        if(this.life<0){
            this.discard();
        }else {
            this.life--;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(pResult.getEntity() instanceof LivingEntity living){
            if(this.getOwner()!=null){
                if(!this.getOwner().isAlliedTo(living) &&
                        !(living instanceof IllagerBeastEntity beast && !beast.isTame())){
                    living.hurt(DamageSource.MAGIC,3);
                }
            }
        }
    }

    public ItemStack getItem(){
        return new ItemStack(ModItems.SOUL_SLASH.get());
    }

    @Override
    protected void defineSynchedData() {

    }
}
