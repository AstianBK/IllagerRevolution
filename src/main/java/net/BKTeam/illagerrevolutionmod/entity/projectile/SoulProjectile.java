package net.BKTeam.illagerrevolutionmod.entity.projectile;

import com.google.common.collect.Lists;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class SoulProjectile extends ThrowableItemProjectile {
    int life;
    @Nullable
    private Entity finalTarget;

    private Entity owner;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetId;

    public SoulProjectile(EntityType<? extends SoulProjectile> type, Level world) {
        super(type,world);
        this.setNoGravity(true);
        this.life=0;
    }

    public SoulProjectile(LivingEntity thrower, Level level, LivingEntity target) {
        super(ModEntityTypes.SOUL_PROJECTILE.get(),thrower,level);
        this.setNoGravity(true);
        this.setOwner(thrower);
        BlockPos blockpos = Objects.requireNonNull(this.getOwner()).blockPosition();
        double d0 = (double)blockpos.getX() + 0.5D;
        double d1 = (double)blockpos.getY() + 0.5D;
        double d2 = (double)blockpos.getZ() + 0.5D;
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());
        this.owner=thrower;
        this.finalTarget = target;
        this.currentMoveDirection = Direction.UP;
        if(target!=null){
            this.selectNextMoveDirection(target.getDirection().getAxis());
        }
        this.life=0;
    }
    public SoulProjectile(Level world, double x, double y, double z) {
        super(ModEntityTypes.SOUL_PROJECTILE.get(), x, y, z, world);
        this.setNoGravity(true);
        this.life = 0;
    }

    private void setMoveDirection(@Nullable Direction pDirection) {
        this.currentMoveDirection = pDirection;
    }
    private void selectNextMoveDirection(@Nullable Direction.Axis p_37349_) {
        double d0 = 0.5D;
        BlockPos blockpos;
        if (this.finalTarget == null) {
            blockpos = this.blockPosition().below();
        } else {
            d0 = (double)this.finalTarget.getBbHeight() * 0.5D;
            blockpos = new BlockPos(this.finalTarget.getX(), this.finalTarget.getY() + d0, this.finalTarget.getZ());
        }

        double d1 = (double)blockpos.getX() + 0.5D;
        double d2 = (double)blockpos.getY() + d0;
        double d3 = (double)blockpos.getZ() + 0.5D;
        Direction direction = null;
        if (!blockpos.closerToCenterThan(this.position(), 2.0D)) {
            BlockPos blockpos1 = this.blockPosition();
            List<Direction> list = Lists.newArrayList();
            if (p_37349_ != Direction.Axis.X) {
                if (blockpos1.getX() < blockpos.getX() && this.level.isEmptyBlock(blockpos1.east())) {
                    list.add(Direction.EAST);
                } else if (blockpos1.getX() > blockpos.getX() && this.level.isEmptyBlock(blockpos1.west())) {
                    list.add(Direction.WEST);
                }
            }

            if (p_37349_ != Direction.Axis.Y) {
                if (blockpos1.getY() < blockpos.getY() && this.level.isEmptyBlock(blockpos1.above())) {
                    list.add(Direction.UP);
                } else if (blockpos1.getY() > blockpos.getY() && this.level.isEmptyBlock(blockpos1.below())) {
                    list.add(Direction.DOWN);
                }
            }

            if (p_37349_ != Direction.Axis.Z) {
                if (blockpos1.getZ() < blockpos.getZ() && this.level.isEmptyBlock(blockpos1.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockpos1.getZ() > blockpos.getZ() && this.level.isEmptyBlock(blockpos1.north())) {
                    list.add(Direction.NORTH);
                }
            }

            direction = Direction.getRandom(this.random);
            if (list.isEmpty()) {
                for(int i = 5; !this.level.isEmptyBlock(blockpos1.relative(direction)) && i > 0; --i) {
                    direction = Direction.getRandom(this.random);
                }
            } else {
                direction = list.get(this.random.nextInt(list.size()));
            }
            d1 = this.getX() + (double)direction.getStepX();
            d2 = this.getY() + (double)direction.getStepY();
            d3 = this.getZ() + (double)direction.getStepZ();
        }

        this.setMoveDirection(direction);
        double d6 = d1 - this.getX();
        double d7 = d2 - this.getY();
        double d4 = d3 - this.getZ();
        double d5 = Math.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
        if (d5 == 0.0D) {
            this.targetDeltaX = 0.0D;
            this.targetDeltaY = 0.0D;
            this.targetDeltaZ = 0.0D;
        } else {
            this.targetDeltaX = d6 / d5 * 0.15D;
            this.targetDeltaY = d7 / d5 * 0.15D;
            this.targetDeltaZ = d4 / d5 * 0.15D;
        }

        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if(result.getEntity() instanceof LivingEntity){
            Entity entity = result.getEntity();
            LivingEntity livingEntity = (LivingEntity) entity;
            boolean flag1= livingEntity.isInvulnerable() && livingEntity==this.getOwner();
            if ( flag1 || this.finalTarget==livingEntity) {
                if(livingEntity instanceof BladeKnightEntity bkEntity){
                    bkEntity.absorbedSouls++;
                    bkEntity.updateDamage();
                }
                livingEntity.heal(2);
                livingEntity.playSound(ModSounds.SOUL_RELEASE.get(), 2.0F, 1.0F);
                discard();
            }else {
                livingEntity.hurt(DamageSource.MAGIC,5);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 1));
                livingEntity.playSound(ModSounds.SOUL_RELEASE.get(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SOUL_PROJECTILE.get();
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }


    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                this.finalTarget = ((ServerLevel)this.level).getEntity(this.targetId);
                if (this.finalTarget == null) {
                    this.targetId = null;
                }
            }

            if (this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && this.finalTarget.isSpectator()) {
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
                }
                this.discard();
            } else {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
                Vec3 vec3 = this.getDeltaMovement();
                this.setDeltaMovement(vec3.add((this.targetDeltaX - vec3.x) * 0.2D, (this.targetDeltaY - vec3.y) * 0.2D, (this.targetDeltaZ - vec3.z) * 0.2D));
            }

            HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }
        }

        this.checkInsideBlocks();
        Vec3 vec31 = this.getDeltaMovement();
        this.setPos(this.getX() + vec31.x, this.getY() + vec31.y, this.getZ() + vec31.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        if (this.level.isClientSide) {
            this.level.addParticle(ModParticles.BKSOULS_PARTICLES.get(), this.getX() - vec31.x, this.getY() - vec31.y + 0.15D, this.getZ() - vec31.z, 0.0D, 0.0D, 0.0D);
        } else if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
                }
            }

            if (this.currentMoveDirection != null) {
                BlockPos blockpos = this.blockPosition();
                Direction.Axis direction$axis = this.currentMoveDirection.getAxis();
                if (this.level.loadedAndEntityCanStandOn(blockpos.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection(direction$axis);
                } else {
                    BlockPos blockpos1 = this.finalTarget.blockPosition();
                    if (direction$axis == Direction.Axis.X && blockpos.getX() == blockpos1.getX() || direction$axis == Direction.Axis.Z && blockpos.getZ() == blockpos1.getZ() || direction$axis == Direction.Axis.Y && blockpos.getY() == blockpos1.getY()) {
                        this.selectNextMoveDirection(direction$axis);
                    }
                }
            }
        }
        if (this.life>=500){
            discard();
        }
        ++this.life;
    }
    @Override
    protected float getGravity() {
        return 0.0F;
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        double d0 = pPacket.getXa();
        double d1 = pPacket.getYa();
        double d2 = pPacket.getZa();
        this.setDeltaMovement(d0, d1, d2);
    }

}
