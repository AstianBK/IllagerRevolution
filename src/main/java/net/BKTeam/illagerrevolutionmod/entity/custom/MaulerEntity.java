package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.effect.InitEffect;
import net.BKTeam.illagerrevolutionmod.enchantment.BKMobType;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class MaulerEntity extends MountEntity implements IAnimatable {
    private static final UUID MAULER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private static final UUID MAULER_ATTACK_DAMAGE_UUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");


    public final AnimationFactory factory= GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> MAULED =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> SAVAGER =
            SynchedEntityData.defineId(MaulerEntity.class, EntityDataSerializers.BOOLEAN);

    public int attackTimer;

    public boolean isLeftAttack;

    public int mauledTimer;

    private int mauledAttackTimer;

    private int catchedTimer;

    private int prepareTimer;

    private int savagerTimer;

    private int savagerCooldown;

    private int stunnedTimer;

    public MaulerEntity(EntityType<? extends MountEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.attackTimer=0;
        this.mauledTimer=0;
        this.mauledAttackTimer=0;
        this.catchedTimer=0;
        this.prepareTimer = 0;
        this.savagerTimer = 0;
        this.isLeftAttack=false;
        this.stunnedTimer = 0;
        this.savagerCooldown = 0;
    }
    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D)
                .add(Attributes.FOLLOW_RANGE, 30.D)
                .add(Attributes.MOVEMENT_SPEED, 0.29f)
                .add(Attributes.JUMP_STRENGTH,0.60d)
                .build();
    }

    @Override
    public float getStepHeight() {
        return 1.0f;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1,new TemptGoal(this,1.5d,Ingredient.of(Items.ROTTEN_FLESH),false){
            @Override
            public boolean canUse() {
                return super.canUse() && ((TamableAnimal)this.mob).isTame();
            }

        });
        this.targetSelector.addGoal(1,new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2,new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1,new MaulerMauled(this));
        this.goalSelector.addGoal(2,new MaulerAttackGoal(this,1.2d,true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, true));
        super.registerGoals();
    }
    private   <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isAggressive() && !this.isSitting() && !this.isPrepare() && !this.isStunned()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.walk"+(!this.isVehicle() ? "1" : "2"), ILoopType.EDefaultLoopTypes.LOOP));
        }else if(event.isMoving() && this.isAggressive() && !this.isSitting() && !this.isPrepare() && !this.isStunned()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.walk2", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isSitting() && this.isTame() && !this.isPrepare() && !this.isStunned()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.sit", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isStunned()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.stun", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isPrepare()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.rage", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    private  <E extends IAnimatable> PlayState predicateHead(AnimationEvent<E> event) {
        if (this.isMauled() && !this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.attack3", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else if(this.isAttacking() && !this.isMauled()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mauler.attack"+(this.isLeftAttack ? "1" : "2"), ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else {
            event.getController().clearAnimationCache();
        }
        return PlayState.CONTINUE;
    }

    public boolean isStunned(){
        return this.stunnedTimer>0;
    }
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.ROTTEN_FLESH);
    }

    @Override
    public double getPassengersRidingOffset() {
        return ((double)this.getBbHeight());
    }

    @Override
    public Beast getTypeBeast() {
        return Beast.MAULER;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.savagerCooldown==0 && !this.isSavager() && !this.isVehicle() && this.prepareTimer==0){
            float healt = 1.0F-(this.getHealth()/this.getMaxHealth());
            boolean flag = this.random.nextFloat()<healt;
            if(flag){
                this.prepareTimer=25;
                this.level.playSound(null,this,SoundEvents.RAVAGER_ROAR,SoundSource.HOSTILE,3.0F,1.0F);
                this.level.broadcastEntityEvent(this,(byte) 62);
                return false;
            }
        }

        if(this.isVehicle()){
            for (Entity entity : this.getPassengers()){
                if(entity==pSource.getEntity()){
                    return false;
                }
            }
        }

        if(this.isSavager()){
            this.savagerTimer=200;
            pAmount = Math.max(1.0F,pAmount*0.10F);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        if(this.level.random.nextFloat() > 0.85F){
            ItemStack itemStack=new ItemStack(ModItems.MAULER_PELT.get());
            itemStack.setCount(1);
            this.spawnAtLocation(itemStack);
        }
        if(this.hasArmor() || this.isSaddled()){
            for (int i = 0 ; i < this.getInventorySize() ; i++){
                ItemStack stack = this.inventory.getItem(i);
                if(!stack.isEmpty()){
                    this.spawnAtLocation(stack);
                    this.inventory.setItem(i,ItemStack.EMPTY);
                }
            }
        }
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    public boolean hasArmor(){
        return !this.inventory.getItem(1).isEmpty() && this.isArmor(this.inventory.getItem(1));
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isPrepare() || this.stunnedTimer>0;
    }

    public boolean isPrepare(){
        return this.prepareTimer>0;
    }

    @Override
    public void attackG() {
        if(!this.isMauled() && !this.isAttacking()){
            if(this.getPassengers().size()<2){
                if(!this.level.isClientSide){
                    this.level.broadcastEntityEvent(this, (byte) 8);
                }
                boolean flag=false;
                this.setAttacking(true);
                this.level.playSound(null,this.getOnPos(),ModSounds.MAULER_BARK.get(), SoundSource.HOSTILE,1.0f,1.0f);
                float f = this.yBodyRot * ((float)Math.PI / 180F);
                float f1 = Mth.sin(f);
                float f2 = Mth.cos(f);
                float f3 = 0.5f;
                BlockPos pos = new BlockPos(this.getX()-(f3*f1),this.getY()+1.5d,this.getZ()+(f3*f2));
                List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class,new AABB(pos).inflate(3,3,3), e -> e != this && e!=this.getOwner() && distanceTo(e) <= 3 + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
                for(LivingEntity living : targets){
                    float entityHitAngle = (float) ((Math.atan2(living.getZ() - this.getZ(), living.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
                    float entityAttackingAngle = this.yBodyRot % 360;
                    float arc = 180.0F;
                    if (entityHitAngle < 0) {
                        entityHitAngle += 360;
                    }
                    if (entityAttackingAngle < 0) {
                        entityAttackingAngle += 360;
                    }
                    float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                    float entityHitDistance = (float) Math.sqrt((living.getZ() - this.getZ()) * (living.getZ() - this.getZ()) + (living.getX() - this.getX()) * (living.getX() - this.getX())) - living.getBbWidth() / 2f;
                    if (entityHitDistance <= 3 - 0.3 && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2) && !flag ) {
                        living.hurt(DamageSource.mobAttack(this), 3.0F);
                        this.catchedTarget(living);
                        flag = true;
                    }else if(flag){
                        break;
                    }
                }
            }else {
                this.setAttacking(true);
                if(!this.level.isClientSide){
                    this.level.broadcastEntityEvent(this, (byte) 8);
                }
                this.level.playSound(null,this.getOnPos(), ModSounds.MAULER_BARK.get(), SoundSource.HOSTILE,1.0f,1.0f);
                if(this.getCatchedEntity()!=null){
                    this.getCatchedEntity().addEffect(new MobEffectInstance(InitEffect.MAULED.get(),100,0));
                    this.releaseTarget(this.getCatchedEntity());
                }
            }
            if(this.isSavager()){
                this.savagerTimer=200;
            }
        }
        super.attackG();
    }

    @Override
    public double getSpeedBase() {
        return this.isSavager() ? 0.29D : 0.45D;
    }

    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    public void attackV() {
        if(!this.isMauled() && !this.isAttacking() && this.hasCatched()){
            this.setIsMauled(true);
            if(!this.level.isClientSide){
                this.level.broadcastEntityEvent(this, (byte) 4);
            }
        }
        super.attackV();
    }

    public LivingEntity getCatchedEntity(){
        for(Entity entity:this.getPassengers()){
            if(entity!=this.getOwner() && entity instanceof LivingEntity){
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId==4){
            this.setIsMauled(true);
        }else if(pId == 8){
            this.setAttacking(true);
        } else if (pId == 60) {
            this.setSavager(true);
        } else if (pId == 61) {
            this.stunnedTimer=100;
            this.savagerCooldown=500;
        } else if (pId == 62) {
            this.prepareTimer=25;
        }else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot){
        switch (pSlot.getType()){
            case ARMOR :
                return this.inventory.getItem(pSlot.getIndex());
            default:
                return super.getItemBySlot(pSlot);
        }
    }

    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack saddled = this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack legs = this.getItemBySlot(EquipmentSlot.LEGS);
        this.updateContainerEquipment();
        ItemStack saddled1 = this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack legs1= this.getItemBySlot(EquipmentSlot.LEGS);
        if(this.tickCount > 20){
            if (this.isArmor(legs1) && legs!=legs1){
                this.playSound(SoundEvents.ARMOR_EQUIP_IRON);
            }
            if(saddled!=saddled1){
                this.playSound(SoundEvents.HORSE_SADDLE);
            }
        }
    }

    @Override
    protected void blockedByShield(LivingEntity pEntity) {
        if(this.stunnedTimer==0 && this.isSavager()){
            this.stunnedTimer=100;
            this.savagerCooldown=500;
            if(!this.level.isClientSide){
                this.level.broadcastEntityEvent(this,(byte) 61);
            }
            if(this.hasCatched()){
                this.releaseTarget(this.getCatchedEntity());
            }
        }
        super.blockedByShield(pEntity);
    }

    @Override
    protected void updateContainerEquipment(){
        if (!this.level.isClientSide) {
            ItemStack stack = this.getItemBySlot(EquipmentSlot.LEGS);
            boolean flag = !stack.isEmpty();
            this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(MAULER_ATTACK_DAMAGE_UUID);
            this.getAttribute(Attributes.ARMOR).removeModifier(MAULER_ARMOR_UUID);
            if(flag) {
                double i = ((BeastArmorItem) stack.getItem()).getDamageValue();
                int d = (((BeastArmorItem) stack.getItem()).getArmorValue());
                if (i != 0) {
                    this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(MAULER_ATTACK_DAMAGE_UUID, "mauler attack bonus", i, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                if(d!=0){
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(MAULER_ARMOR_UUID,"mauler armor bonus",d, AttributeModifier.Operation.ADDITION));
                }
            }
            this.setIsSaddled(!this.getContainer().getItem(0).isEmpty());
        }
        super.updateContainerEquipment();
    }

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack itemStack) {
        switch (slotIn.getType()){
            case ARMOR :
                this.inventory.setItem(slotIn.getIndex(),itemStack);
            default:
                super.setItemSlot(slotIn,itemStack);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.isBaby()) {
            if (this.isTame() && pPlayer.isSecondaryUseActive() && this.isOwnedBy(pPlayer)) {
                this.setSitting(!this.isSitting());
                return InteractionResult.SUCCESS;
            }
            if (this.isVehicle() && !this.canAddPassenger(pPlayer)) {
                return super.mobInteract(pPlayer, pHand);
            }
        }
        if (!itemstack.isEmpty()) {
            if(itemstack.getItem() instanceof DyeItem dyeItem){
                this.setPainted(true);
                if (dyeItem.getDyeColor()!=this.getColor()){
                    this.setColor(dyeItem.getDyeColor());
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    playSound(SoundEvents.INK_SAC_USE, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            } else if(itemstack.is(Items.WATER_BUCKET) && this.isPainted()){
                this.setPainted(false);
                this.setColor(DyeColor.WHITE);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    itemstack=new ItemStack(Items.BUCKET);
                    pPlayer.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
                    pPlayer.setItemSlot(EquipmentSlot.MAINHAND,itemstack);
                }
                playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
            if(itemstack.is(ModItems.BEAST_STAFF.get()) && this.isOwnedBy(pPlayer)){
                if(pPlayer instanceof IOpenBeatsContainer){
                    this.openInventory(pPlayer);
                    this.gameEvent(GameEvent.ENTITY_INTERACT, pPlayer);
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (this.isFood(itemstack)) {
                if(!this.isTame()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if(this.level.random.nextFloat()>0.90f){
                        if (!ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                            if (!this.level.isClientSide) {
                                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, -1.0F);
                                super.tame(pPlayer);
                                this.navigation.recomputePath();
                                this.setTarget(null);
                                this.level.broadcastEntityEvent(this, (byte)7);
                                this.setSitting(true);
                                for(Entity entity : this.getPassengers()){
                                    entity.stopRiding();
                                }
                            }
                        }
                    }else {
                        if(!this.level.isClientSide){
                            this.level.broadcastEntityEvent(this, (byte)6);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }else{
                    if(!pPlayer.getAbilities().instabuild){
                        itemstack.shrink(1);
                    }
                    if(!this.level.isClientSide && this.getMaxHealth()!=this.getHealth()){
                        this.heal(this.getMaxHealth()*0.10f);
                    }
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, -1.0F);
                }
                return InteractionResult.CONSUME;
            }
            boolean flag = !this.isSaddled() && itemstack.is(Items.SADDLE);
            if ((this.isArmor(itemstack) || flag) && this.isTame() && this.isOwnedBy(pPlayer))  {
                ItemStack stack = itemstack.copy();
                if(stack.getItem() instanceof BeastArmorItem armorItem){
                    if(!this.getItemBySlot(armorItem.getEquipmetSlot()).isEmpty()){
                        this.spawnAtLocation(this.getItemBySlot(armorItem.getEquipmetSlot()));
                        this.setItemSlot(armorItem.getEquipmetSlot(),ItemStack.EMPTY);
                    }
                    this.setItemSlot(armorItem.getEquipmetSlot(),stack);
                    this.playSound(SoundEvents.ARMOR_EQUIP_IRON);
                }else {
                    this.playSound(SoundEvents.HORSE_SADDLE);
                    this.setIsSaddled(true);
                    this.setItemSlot(EquipmentSlot.FEET,itemstack);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
            return super.mobInteract(pPlayer,pHand);
        }

        if (this.isBaby() || !this.isTame()) {
            return super.mobInteract(pPlayer, pHand);
        } else{
            this.doPlayerRide(pPlayer);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
    }


    public void catchedTarget(LivingEntity entity){
        if(entity!=null && !entity.isPassenger() && this.canAddPassenger(entity) && entity.getBbWidth()<1.4D && entity.getBbHeight()<2.7d && !(entity instanceof WitherBoss)){
            if (!this.level.isClientSide) {
                entity.startRiding(this);
            }
        }
    }


    public void releaseTarget(LivingEntity entity){
        if(entity!=null && !this.canAddPassenger(entity)){
            if (!this.level.isClientSide) {
                entity.stopRiding();
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        LivingEntity target = this.getCatchedEntity();

        if(this.savagerCooldown>0){
            this.savagerCooldown--;
        }
        if(this.prepareTimer>0){
            this.prepareTimer--;
            if(this.prepareTimer==0){
                this.setSavager(true);
                if(!this.level.isClientSide){
                    this.level.broadcastEntityEvent(this,(byte) 60);
                }

            }
        }

        if(this.stunnedTimer>0){
            this.stunnedTimer--;
            this.stunEffect();
        }

        if(this.isSavager()){
            this.savagerTimer--;
        }

        if(this.savagerTimer<0){
            this.stunnedTimer=100;
            this.setSavager(false);
            this.savagerCooldown=500;
            if(this.hasCatched()){
                this.releaseTarget(this.getCatchedEntity());
            }
            if(!this.level.isClientSide){
                this.level.broadcastEntityEvent(this,(byte)61);
            }
        }

        if (this.isAttacking()) {
            this.attackTimer--;
        }
        if(this.attackTimer<0){
            this.setAttacking(false);
        }
        if(this.catchedTimer>0){
            this.catchedTimer--;
        }
        if(this.isMauled()){
            this.mauledTimer--;
            if(this.mauledTimer!=0){
                if(this.mauledAttackTimer==0){
                    this.mauledAttackTimer=10;
                    if(target!=null && target.isAlive()){
                        target.hurt(DamageSource.mobAttack(this),1.0f);
                        target.addEffect(new MobEffectInstance(InitEffect.MAULED.get(),350,0));
                        if(this.isSavager()){
                            this.savagerTimer=200;
                        }
                    }
                }
                this.mauledAttackTimer--;
            }else {
                this.setIsMauled(false);
            }
        }

        if(this.isSavager()){
            float f = this.yBodyRot * ((float) Math.PI / 180F);
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level.addParticle(ParticleTypes.SMOKE, this.getX() - f2 + f1 * 0.4d, this.getY()+this.getBbHeight()+0.1D, this.getZ() + f1 + f2 * 0.4d, 0.0F, 0.0F, 0.0F);
            this.level.addParticle(ParticleTypes.SMOKE, this.getX() - f2  - f1 * 0.4d, this.getY()+this.getBbHeight()+0.1D, this.getZ() + f1  - f2 * 0.4d, 0.0F, 0.0F, 0.0F);
        }

    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.isImmobile() && !this.isSavager()){
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29D);
        }else if(this.isImmobile() && !this.isSavager()){
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        }
    }

    @Override
    public void attackC() {
        if(this.savagerTimer<=0 && this.getCatchedEntity()==null && this.savagerCooldown==0){
            if(!this.level.isClientSide){
                this.prepareTimer=25;
                this.level.broadcastEntityEvent(this,(byte) 62);
            }
            this.level.playSound(null,this,SoundEvents.RAVAGER_ROAR,SoundSource.HOSTILE,3.0F,1.0F);
            super.attackC();
        }else {
            this.level.broadcastEntityEvent(this,(byte) 63);
            this.level.playSound(null,this,SoundEvents.VILLAGER_NO,SoundSource.HOSTILE,1.0F,1.0F);
        }
    }

    public void setAttacking(boolean pBoolean) {
        this.entityData.set(ATTACKING,pBoolean);
        this.attackTimer=pBoolean ? 10 : 0;
        this.isLeftAttack = this.level.random.nextBoolean();
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }


    public void setSavager(boolean pBoolean) {
        this.entityData.set(SAVAGER,pBoolean);
        this.savagerTimer=pBoolean ? 200 : 0;
        if(pBoolean){
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8.00F);;
        }else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0F);;
        }
    }

    public boolean isSavager() {
        return this.entityData.get(SAVAGER);
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    public void travel(Vec3 pTravelVector) {
        LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
        if (this.isAlive() ) {
            if (this.isVehicle() && livingentity!=null && this.isTame() && !this.isSitting() && this.isSaddled()) {
                this.setYRot(livingentity.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(livingentity.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float f = livingentity.xxa * 0.5F;
                float f1 = livingentity.zza;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                if (this.onGround && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
                    f = 0.0F;
                    f1 = 0.0F;
                }

                if (this.playerJumpPendingScale > 0.0F && !this.isJumping() && this.onGround) {
                    double d0 = this.getCustomJump() * (double) this.playerJumpPendingScale * (double) this.getBlockJumpFactor();
                    double d1 = d0 + this.getJumpBoostPower();
                    Vec3 vec3 = this.getDeltaMovement();
                    this.setDeltaMovement(vec3.x, d1, vec3.z);
                    this.setIsJumping(true);
                    this.hasImpulse = true;
                    net.minecraftforge.common.ForgeHooks.onLivingJump(this);
                    if (f1 > 0.0F) {
                        float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                        float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                        this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * this.playerJumpPendingScale), 0.0D, (double) (0.4F * f3 * this.playerJumpPendingScale)));
                    }

                    this.playerJumpPendingScale = 0.0F;
                }

                this.flyingSpeed = this.getSpeed() * 0.1F;
                if (this.isControlledByLocalInstance()) {
                    this.setSpeed(!this.hasCatched() ? (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)/2 : (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)/3);
                    super.travel(new Vec3((double) f, pTravelVector.y, (double) f1));
                } else if (livingentity instanceof Player) {
                    this.setDeltaMovement(Vec3.ZERO);
                }

                if (this.onGround) {
                    this.playerJumpPendingScale = 0.0F;
                    this.setIsJumping(false);
                }
                this.calculateEntityAnimation(this, false);
                this.tryCheckInsideBlocks();
            } else {
                this.flyingSpeed = 0.02F;
                super.travel(pTravelVector);
            }
        }
    }


    public boolean hasCatched(){
        return this.getCatchedEntity()!=null;
    }

    public void positionRider(Entity pPassenger) {
        super.positionRider(pPassenger);
        if(pPassenger==this.getOwner() || this.getControllingPassenger()==pPassenger){
            if (pPassenger instanceof Mob mob) {
                this.yBodyRot = mob.yBodyRot;
            }
            float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            float f1 = 0.1F;
            float f2 = 0.15F;
            pPassenger.setPos(this.getX() + (double)(f1 * f3), this.getY()+ this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset() - (double)f2, this.getZ() - (double)(f1 * f));
            ((LivingEntity)pPassenger).yBodyRot = this.yBodyRot;

        }else {
            float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            float f1 = 1.7F;
            pPassenger.setPos(this.getX() - (f3*f1), this.getY()+ this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset()-1.0d, this.getZ() + (f*f1));
            if (pPassenger instanceof LivingEntity) {
                ((LivingEntity)pPassenger).yBodyRot = this.yBodyRot;
            }
        }
    }

    public boolean canBeLeashed(@NotNull Player player) {
        return super.canBeLeashed(player);
    }

    @Override
    public void setTame(boolean tamed) {
        super.setTame(tamed);
        if (tamed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29f);
        }
    }
    @Override
    public boolean canEquipOnFeet(ItemStack p_39690_) {
        return p_39690_.is(Items.SADDLE);
    }

    @Override
    public boolean canEquipOnLegs(ItemStack p_39690_) {
        return super.canEquipOnLegs(p_39690_);
    }

    public boolean isArmor(ItemStack stack){
        return stack.getItem() instanceof BeastArmorItem armorItem && armorItem.getBeast() == Beast.MAULER;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if(pEntity instanceof  LivingEntity living){
            if(this.level.random.nextFloat() > 0.90F && this.catchedTimer<=0){
                this.catchedTarget(living);
                this.catchedTimer=400;
            }
            if(this.level.random.nextFloat() > 0.99F){
                living.addEffect(new MobEffectInstance(InitEffect.MAULED.get(),1200,0));
            }
        }

        return super.doHurtTarget(pEntity);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Savager",this.isSavager());
        pCompound.putBoolean("Attacking",this.isAttacking());
        pCompound.putBoolean("isMauled",this.isMauled());
        ItemStack saddle = this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack itemStackHead = this.getItemBySlot(EquipmentSlot.LEGS);
        if(!itemStackHead.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            itemStackHead.save(headCompoundNBT);
            pCompound.put("ChestMaulerArmor", headCompoundNBT);
        }
        if(!saddle.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            saddle.save(headCompoundNBT);
            pCompound.put("saddleItem", headCompoundNBT);
        }
    }

    public boolean isMauled() {
        return this.entityData.get(MAULED);
    }

    public void setIsMauled(boolean pBoolean){
        this.entityData.set(MAULED,pBoolean);
        this.mauledTimer= pBoolean ? 40 : 0 ;
        this.mauledAttackTimer = 0;
        if(pBoolean){
            this.level.playSound(null,this,ModSounds.MAULER_SNARL.get(),SoundSource.HOSTILE,1.0F,1.0F);
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setSavager(pCompound.getBoolean("Savager"));
        this.setAttacking(pCompound.getBoolean("Attacking"));
        this.setIsMauled(pCompound.getBoolean("isMauled"));
        CompoundTag compoundNBT1 = pCompound.getCompound("saddleItem");
        CompoundTag compoundNBT = pCompound.getCompound("ChestMaulerArmor");
        if(!compoundNBT.isEmpty()) {
            if(this.isArmor(ItemStack.of(compoundNBT))){
                ItemStack stack=ItemStack.of(compoundNBT);
                this.setItemSlot(EquipmentSlot.LEGS,stack);
            }
        }
        if(!compoundNBT1.isEmpty()) {
            if(ItemStack.of(compoundNBT1).is(Items.SADDLE)){
                ItemStack stack=ItemStack.of(compoundNBT1);
                this.setItemSlot(EquipmentSlot.FEET,stack);
            }
        }
        this.updateContainerEquipment();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING,false);
        this.entityData.define(MAULED,false);
        this.entityData.define(SAVAGER,false);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<MaulerEntity>(this, "controller_mauled",
                0, this::predicateHead));
        data.addAnimationController(new AnimationController<MaulerEntity>(this, "controller_body",
                10, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (p_21748_ != null) {
            this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.MAULER_HURT.get();
    }

    @javax.annotation.Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    static class MaulerAttackGoal extends MeleeAttackGoal {
        private final MaulerEntity goalOwner;

        public MaulerAttackGoal(MaulerEntity entity, double speedModifier, boolean followWithoutLineOfSight) {
            super(entity, speedModifier, followWithoutLineOfSight);
            this.goalOwner = entity;
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity entity, double distance) {
            double d0 = this.getAttackReachSqr(entity);
            if (distance <= d0 && this.goalOwner.attackTimer <= 0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.goalOwner.playSound(ModSounds.MAULER_BARK.get(), 1.0F, 1.0F);
                this.goalOwner.doHurtTarget(entity);
            }
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !(this.goalOwner.isTame() && this.goalOwner.isVehicle()) && !this.goalOwner.hasCatched();
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            this.goalOwner.setAttacking(true);
            this.goalOwner.level.broadcastEntityEvent(this.goalOwner,(byte) 8);
        }

    }

    static class MaulerMauled extends Goal {
        private final MaulerEntity mauler;

        MaulerMauled (MaulerEntity mauler){
            this.mauler=mauler;
        }

        @Override
        public void start() {
            this.mauler.setIsMauled(true);
            this.mauler.level.broadcastEntityEvent(this.mauler, (byte) 4);
            super.start();
        }

        @Override
        public boolean canUse() {
            return this.mauler.getCatchedEntity()!=null && this.mauler.level.random.nextFloat() > 0.90F && !this.mauler.isMauled() && this.mauler.getPassengers().size()!=2;
        }
    }
}
