package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.BKTeam.illagerrevolutionmod.ModConstants;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;

import java.util.List;


public class SummonedSoul extends ThrowableItemProjectile {

    int life;

    public SummonedSoul(EntityType<? extends SummonedSoul> type, Level world) {
        super(type,world);
        this.setNoGravity(true);
        this.life=0;
    }

    public SummonedSoul(LivingEntity thrower, Level level) {
        super(ModEntityTypes.SUMMONED_SOUL.get(),thrower,level);
        this.setNoGravity(true);
        this.life=0;
    }
    public SummonedSoul(Level world, double x, double y, double z) {
        super(ModEntityTypes.SUMMONED_SOUL.get(), x, y, z, world);
        this.setNoGravity(true);
        this.life=0;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {

        if(!(result.getEntity() instanceof LivingEntity)){
            Entity entity = result.getEntity();
            Entity owner = this.getOwner();
            LivingEntity livingEntity = (LivingEntity) entity;

            if(owner!=null){
                BlockPos pos=livingEntity.getOnPos();
                SoulEntity soul=new SoulEntity((LivingEntity) owner,owner.level, ModConstants.LIST_NAME_ZOMBIFIED.get(this.level.getRandom().nextInt(0,4)), (LivingEntity) owner,pos.getY());
                soul.setOwner(owner);
                soul.moveTo(pos,0.0f,0.0f);
                owner.level.addFreshEntity(soul);
                livingEntity.hurt(DamageSource.WITHER,4);
                this.discard();
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    @Override
    public void tick() {
        Vec3 deltaMovement = this.getDeltaMovement();
        super.tick();
        this.setDeltaMovement(deltaMovement);
        if(this.level.isClientSide){
            SimpleParticleType particleType = ParticleTypes.LARGE_SMOKE;
            this.level.addParticle(particleType, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
        if (!this.level.isClientSide()) {
            HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (result.getType() == HitResult.Type.MISS && this.isAlive()) {
                List<Entity> intersecting = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox(), this::canHitEntity);
                if (!intersecting.isEmpty())
                    this.onHit(new EntityHitResult(intersecting.get(0)));
            }
        }
        if (this.life>=90){
            this.discard();
        }
        ++this.life;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos pos=result.getBlockPos();
        BlockPos posX= new BlockPos(pos.getX(),pos.getY()+2.0D,pos.getZ());
        if(this.getOwner()!=null){
            SoulEntity soul=new SoulEntity((LivingEntity) this.getOwner(),this.getOwner().level, ModConstants.LIST_NAME_ZOMBIFIED.get(this.level.getRandom().nextInt(0,4)), (LivingEntity) this.getOwner(),posX.getY());
            soul.moveTo(posX,0.0f,0.0f);
            soul.setOwner(this.getOwner());
            this.getOwner().level.addFreshEntity(soul);
            this.discard();
        }
    }
    @Override
    protected float getGravity() {
        return 0.0F;
    }


}
