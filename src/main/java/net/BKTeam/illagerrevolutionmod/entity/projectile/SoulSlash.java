package net.BKTeam.illagerrevolutionmod.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class SoulSlash extends ThrowableProjectile {

    private int life;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;
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
        if (!this.level().isClientSide()) {
            HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (result.getType() == HitResult.Type.MISS && this.isAlive()) {
                List<Entity> intersecting = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(1.0F,3.0F,1.0F), this::canHitEntity);
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
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= 7) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(living.getId());
            if(this.getOwner()!=null){
                if(!this.getOwner().isAlliedTo(living) &&
                        !(living instanceof IllagerBeastEntity beast && !beast.isTame())){
                    if(living.hurt(damageSources().indirectMagic(this,(LivingEntity) this.getOwner()),3)){
                        if (!living.isAlive() && this.piercedAndKilledEntities != null) {
                            this.piercedAndKilledEntities.add(living);
                        }
                    }
                }
            }

        }
    }

    @Override
    protected boolean canHitEntity(Entity p_37250_) {
        return super.canHitEntity(p_37250_) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(p_37250_.getId()));
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }

    }

    public ItemStack getItem(){
        return new ItemStack(ModItems.SOUL_SLASH.get());
    }

    @Override
    protected void defineSynchedData() {

    }
}
