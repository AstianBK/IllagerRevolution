package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;


public class IllagerMinerEntity extends IllagerMinerBadlandsEntity implements IAnimatable, InventoryCarrier {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final SimpleContainer inventory = new SimpleContainer(5);
    public boolean fistUseInvi;

    public IllagerMinerEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
        this.fistUseInvi = false;
    }

    public boolean isAlliedTo(@NotNull Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof LivingEntity && ((LivingEntity) pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }


    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.30f).build();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        if(this.level.getRandom().nextInt(0,5)==0 ){
            this.spawnAtLocation(ModItems.HELMET_MINER.get());
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (event.isMoving() && this.isHasItems()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.walk3", ILoopType.EDefaultLoopTypes.LOOP));

        }else if (event.isMoving() && !this.isAggressive() && !this.isAttacking()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.walk", ILoopType.EDefaultLoopTypes.LOOP));
        }
        else if (this.isAggressive() && event.isMoving() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.walk2", ILoopType.EDefaultLoopTypes.LOOP));

        }
        else if (this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));

        }
        else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.illagerminer.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;

    }

    @Override
    public void setHasItem(boolean pBoolean) {
        super.setHasItem(pBoolean);
        this.fistUseInvi=pBoolean;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_34080_) {
        ItemStack itemstack = new ItemStack(Items.IRON_AXE);
        Raid raid = this.getCurrentRaid();
        int i = 1;
        if (pWave > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 2;
        }


        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
        this.playSound(SoundEvents.STONE_STEP, 0.15F, 1.5F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.PILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }


    @Override
    public @NotNull Container getInventory() {
        return this.inventory;
    }
}
