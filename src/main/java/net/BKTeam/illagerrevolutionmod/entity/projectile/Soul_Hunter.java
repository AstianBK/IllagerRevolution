package net.BKTeam.illagerrevolutionmod.entity.projectile;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.Blade_KnightEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;


public class Soul_Hunter extends ThrowableItemProjectile {

    int life;

    public Soul_Hunter(EntityType<? extends Soul_Hunter> type, Level world) {
        super(type,world);
        this.setNoGravity(true);
        this.life=0;
    }

    public Soul_Hunter(LivingEntity thrower, Level level) {
        super(ModEntityTypes.SOUL_HUNTER.get(),thrower,level);
        this.setNoGravity(true);
        this.life=0;
    }
    public Soul_Hunter(Level world, double x, double y, double z) {
        super(ModEntityTypes.SOUL_HUNTER.get(), x, y, z, world);
        this.setNoGravity(true);
        this.life=0;
    }

    @Override
    protected void onHit(HitResult pResult) {
        if(pResult instanceof EntityHitResult){
            this.onHitEntity((EntityHitResult) pResult);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if(result.getEntity() instanceof LivingEntity entity){
            LivingEntity owner = (LivingEntity) this.getOwner();
            if (owner instanceof Blade_KnightEntity && entity instanceof Player) {
                float f = owner.yBodyRotO * ((float) Math.PI / 180F) + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
                float f1 = Mth.cos(f);
                float f2 = Mth.sin(f);
                entity.teleportTo(owner.getX()+f1*1.0D,owner.getY(),owner.getZ()+f2*1.0D);
                playSound(SoundEvents.ENDERMAN_TELEPORT,1.0f,1.0f);
                owner.playSound(ModSounds.BLADE_KNIGHT_LAUGH.get(),6.5f,1.0f);
                discard();
            }else if (!entity.getMobType().equals(MobType.UNDEAD)){
                playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 1.0F);
                discard();
            }
        }
    }


    @Override
    protected Item getDefaultItem() {
        return ModItems.SOUL_HUNTER.get();
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    @Override
    public void tick() {
        Vec3 deltaMovement = this.getDeltaMovement();
        super.tick();
        float f = this.yRotO * ((float) Math.PI / 180F) + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
        float f1 = Mth.cos(f);
        float f2 = Mth.sin(f);
        this.setDeltaMovement(deltaMovement); //Undoes tampering by superclass
        SimpleParticleType particleType = (SimpleParticleType) ModParticles.SMOKE_BK_PARTICLES.get();
        this.level.addParticle(particleType, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);++this.life;
        if (this.life>=150 || this.checkDistandOwner((LivingEntity) this.getOwner())){
            this.discard();
        }
    }

    private boolean checkDistandOwner(LivingEntity owner){
        if(owner!=null){
            double dx=this.distanceTo(owner);
            return dx>50.0d;
        }
        return false;
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }
}
