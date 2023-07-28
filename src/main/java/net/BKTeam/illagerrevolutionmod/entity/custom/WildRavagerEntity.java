package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.block.custom.DrumBlock;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSand;
import net.BKTeam.illagerrevolutionmod.network.PacketStopSound;
import net.BKTeam.illagerrevolutionmod.particle.ModParticles;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class WildRavagerEntity extends MountEntity {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (p_33346_) -> {
        return p_33346_.isAlive() && !(p_33346_ instanceof WildRavagerEntity);
    };
    private static final EntityDataAccessor<Boolean> SADDLED =
            SynchedEntityData.defineId(WildRavagerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> CHARGED =
            SynchedEntityData.defineId(WildRavagerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> HAS_DRUM =
            SynchedEntityData.defineId(WildRavagerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final UUID WILD_RAVAGER_ARMOR_UUID= UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private int attackTick;
    private int stunnedTick;
    private int roarTick;
    private int drumTick;
    private float roarPower;
    private int reAcvivateEffectTick;
    public int prepareTimer;
    private int nextAssaultTimer;

    private int chargedTick;

    public WildRavagerEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.roarPower = 0.0F;
        this.drumTick = 0;
        this.reAcvivateEffectTick = 0;
        this.prepareTimer = 0;
        this.nextAssaultTimer = 0;
        this.chargedTick = 100;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1,new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2,new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4,new RavagerMeleeAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(1,new TemptGoal(this,1.5d,Ingredient.of(Items.HAY_BLOCK),false){
            @Override
            public boolean canUse() {
                return super.canUse() && ((TamableAnimal)this.mob).isTame() && !((TamableAnimal)this.mob).isOrderedToSit();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof WildRavagerEntity ravager && !ravager.isSitting() && !ravager.isVehicle();
            }
        });
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof WildRavagerEntity ravager && !ravager.isSitting() && !ravager.isVehicle();
            }
        });
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true){
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof WildRavagerEntity ravager && !ravager.isTame() && !ravager.isVehicle();
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_) -> {
            return !p_199899_.isBaby();
        }){
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof WildRavagerEntity ravager && !ravager.isTame() && !ravager.isVehicle();
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true){
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof WildRavagerEntity ravager && !ravager.isTame() && !ravager.isVehicle();
            }
        });
    }

    @Override
    public float getStepHeight() {
        return 1.0f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.JUMP_STRENGTH,1.0d);
    }

    @Override
    protected void dropEquipment() {
        if(this.hasArmor() || this.isSaddled()){
            ItemStack stack=this.inventory.getItem(0);
            this.spawnAtLocation(stack);
            this.inventory.setItem(0,ItemStack.EMPTY);
        }
        if(this.hasDrum()){
            ItemStack stack=this.inventory.getItem(1);
            this.spawnAtLocation(stack);
            this.inventory.setItem(1,ItemStack.EMPTY);
        }
        super.dropEquipment();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED,false);
        this.entityData.define(HAS_DRUM,false);
        this.entityData.define(CHARGED,0);
    }

    public void setIsChargedState(int pId){
        this.entityData.set(CHARGED,pId);
        switch (pId){
            case 1:{
                this.prepareTimer=20;
                if(!this.level.isClientSide){
                    this.level.broadcastEntityEvent(this,(byte) 59);
                }
            }
            case 2: {
                this.chargedTick=0;
            }
            case 3: {
                this.nextAssaultTimer = 20;
            }
        }
    }

    public ChargedStates getChargedState(){
        return ChargedStates.byId(this.getChargedId() & 255);
    }

    public int getChargedId(){
        return this.entityData.get(CHARGED);
    }

    public boolean hasDrum() {
        return this.entityData.get(HAS_DRUM);
    }

    public void setHasDrum(boolean pBoolean){
        this.entityData.set(HAS_DRUM,pBoolean);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_146754_) {
        super.onSyncedDataUpdated(p_146754_);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ChargedState",this.getChargedId());
        pCompound.putBoolean("hasDrum", this.hasDrum());
        pCompound.putInt("AttackTick", this.attackTick);
        pCompound.putInt("StunTick", this.stunnedTick);
        pCompound.putInt("RoarTick", this.roarTick);
        pCompound.putInt("activateEffectTick",this.reAcvivateEffectTick);
        ItemStack itemStackHead = this.inventory.getItem(0);

        if(this.hasDrum()){
            ItemStack drumItem= this.inventory.getItem(1);
            CompoundTag nbt=new CompoundTag();
            drumItem.save(nbt);
            pCompound.put("drumItem",nbt);
        }
        if(!itemStackHead.isEmpty()){
            CompoundTag headCompoundNBT = new CompoundTag();
            itemStackHead.save(headCompoundNBT);
            pCompound.put("ChestRavagerArmor", headCompoundNBT);
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setIsChargedState(pCompound.getInt("ChargedState"));
        this.setHasDrum(pCompound.getBoolean("hasDrum"));
        this.attackTick = pCompound.getInt("AttackTick");
        this.stunnedTick = pCompound.getInt("StunTick");
        this.roarTick = pCompound.getInt("RoarTick");
        this.reAcvivateEffectTick = pCompound.getInt("activateEffectTick");
        if(this.hasDrum()){
            CompoundTag compoundTag = pCompound.getCompound("drumItem");
            if(!compoundTag.isEmpty()){
                ItemStack stack= ItemStack.of(compoundTag);
                this.setItemSlot(EquipmentSlot.LEGS,stack);
            }
        }
        CompoundTag compoundNBT = pCompound.getCompound("ChestRavagerArmor");
        if(!compoundNBT.isEmpty()) {
            if(this.isArmor(ItemStack.of(pCompound.getCompound("ChestRavagerArmor")))){
                ItemStack stack = ItemStack.of(pCompound.getCompound("ChestRavagerArmor"));
                this.getContainer().setItem(0,stack);
            }
        }
        this.updateContainerEquipment();
    }

    protected PathNavigation createNavigation(Level pLevel) {
        return new RavagerNavigation(this, pLevel);
    }

    @Override
    public Beast getTypeBeast() {
        return Beast.WILD_RAVAGER;
    }

    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public void equipSaddle(@org.jetbrains.annotations.Nullable SoundSource p_21748_) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (p_21748_ != null) {
            this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }
    }

    public double getPassengersRidingOffset() {
        return ((double)this.getBbHeight());
    }

    public boolean isCharged(){
        return this.getChargedState()==ChargedStates.PREPARE || this.getChargedState()==ChargedStates.CHARGED;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
            if (this.isVehicle() && livingentity!=null && livingentity==this.getOwner() && !this.isSitting() && this.isSaddled() && !this.isCharged()) {
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
                this.flyingSpeed = this.getSpeed() * 0.1F;
                if (this.isControlledByLocalInstance()) {
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)/2);
                    super.travel(new Vec3((double) f, pTravelVector.y, (double) f1));
                } else if (livingentity instanceof Player) {
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.calculateEntityAnimation(this, false);
                this.tryCheckInsideBlocks();
            } else {
                if(this.chargedTick<100){
                    this.charged(pTravelVector);
                }else {
                    this.flyingSpeed = 0.02F;
                    super.travel(pTravelVector);
                }
            }
        }
    }

    public void positionRider(@NotNull Entity pPassenger) {
        super.positionRider(pPassenger);
        if (pPassenger instanceof Mob mob) {
            this.yBodyRot = mob.yBodyRot;
        }

        float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
        float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
        float f1 = 0.15F;
        float f2 = 0.1F;
        float f4 = this.isCharged() ? Mth.cos(((float) 20-this.prepareTimer/20.0F)/10) : 0.0F;
        pPassenger.setPos(this.getX() + (double)(f1 * f3), this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset()- f2 + f4, this.getZ() - (double)(f1 * f));
        ((LivingEntity)pPassenger).yBodyRot = this.yBodyRot;
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

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack stack=pPlayer.getMainHandItem();
        if(!stack.isEmpty()){
            if(stack.getItem() instanceof DyeItem dyeItem){
                this.setPainted(true);
                if (dyeItem.getDyeColor()!=this.getColor()){
                    this.setColor(dyeItem.getDyeColor());
                    if (!pPlayer.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    playSound(SoundEvents.INK_SAC_USE, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            } else if(stack.is(Items.WATER_BUCKET) && this.isPainted()){
                this.setPainted(false);
                this.setColor(DyeColor.WHITE);
                if (!pPlayer.getAbilities().instabuild) {
                    stack.shrink(1);
                    stack=new ItemStack(Items.BUCKET);
                    pPlayer.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
                    pPlayer.setItemSlot(EquipmentSlot.MAINHAND,stack);
                }
                playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }if(stack.is(Items.APPLE)){
                if(!this.level.isClientSide){
                    this.setIsChargedState(1);

                }
                if(!pPlayer.getAbilities().instabuild){
                    stack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
            if(Block.byItem(stack.getItem()) instanceof DrumBlock){
                this.setHasDrum(true);
                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER);
                this.inventory.setItem(1,stack.copy());
                if(!pPlayer.getAbilities().instabuild){
                    stack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
            if((stack.is(Items.SADDLE) || this.isArmor(stack)) && this.isTame() && this.isOwnedBy(pPlayer)){
                ItemStack stack1 = stack.copy();
                boolean flag = stack1.is(Items.SADDLE);
                EquipmentSlot slot = EquipmentSlot.FEET;
                if(!this.getItemBySlot(slot).isEmpty()){
                    this.spawnAtLocation(this.getItemBySlot(slot));
                    this.setItemSlot(slot,ItemStack.EMPTY);
                }
                this.playSound(flag ? SoundEvents.HORSE_SADDLE : SoundEvents.ARMOR_EQUIP_DIAMOND);
                this.setItemSlot(EquipmentSlot.FEET,stack1.copy());
                this.setIsSaddled(true);
                if (!pPlayer.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.CONSUME;
            }else if(stack.is(ModItems.BEAST_STAFF.get()) && this.isOwnedBy(pPlayer)){
                if(pPlayer instanceof IOpenBeatsContainer){
                    this.openInventory(pPlayer);
                    this.gameEvent(GameEvent.ENTITY_INTERACT,pPlayer);
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }
            }else if(stack.is(Items.HAY_BLOCK)){
                if(!this.isTame()){
                    if(this.stunnedTick>0){
                        if (!pPlayer.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        if(this.level.random.nextFloat()<0.25F){
                            if (!ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                                if (!this.level.isClientSide) {
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
                            this.level.broadcastEntityEvent(this, (byte)6);
                        }
                        playSound(SoundEvents.HORSE_EAT, 1.0F, -1.5F);
                        return InteractionResult.SUCCESS;
                    }
                }else if(this.getMaxHealth()!=this.getHealth()){
                    if(!this.level.isClientSide){
                        this.heal(this.getMaxHealth()*20/100);
                        playSound(SoundEvents.HORSE_EAT, 1.0F, -1.5F);
                    }
                    if(!pPlayer.getAbilities().instabuild){
                        stack.shrink(1);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            return super.mobInteract(pPlayer, pHand);
        }else{
            if(pPlayer.isShiftKeyDown() && this.isOwnedBy(pPlayer)){
                this.setSitting(!this.isSitting());
                return InteractionResult.SUCCESS;
            }
        }
        if(!this.isBaby() && this.isTame() && this.isOwnedBy(pPlayer)){
            this.doPlayerRide(pPlayer);
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void attackV() {
        if(!this.level.isClientSide){
            if(this.hasDrum()){
                if(this.drumTick<=0){
                    if(this.reAcvivateEffectTick==0){
                        this.activeEffectAura();
                        this.reAcvivateEffectTick=100;
                    }
                    this.level.playSound(null,this, ModSounds.DRUM_SOUND.get(),SoundSource.HOSTILE,1.5f,1.0f);
                    this.level.broadcastEntityEvent(this,(byte) 64);
                    this.drumTick=600;
                }else {
                    this.stopDrumSound();
                }
            }
        }
        super.attackV();
    }

    public void charged(Vec3 p_20857_){
        Entity entity = this.getControllingPassenger();
        if (this.isVehicle() && entity instanceof Player) {
            this.setYRot(entity.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(entity.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            this.flyingSpeed = this.getSpeed() * 0.1F;

            if (this.isControlledByLocalInstance()) {
                this.setSpeed((float) this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                this.travelWithInput(new Vec3(0.0D, 0.0D, 1.0D));
                this.lerpSteps = 0;
            } else {
                this.calculateEntityAnimation(this, false);
                this.setDeltaMovement(Vec3.ZERO);
            }
            this.tryCheckInsideBlocks();
        } else {
            this.flyingSpeed = 0.02F;
            this.travelWithInput(p_20857_);
        }
    }



    public void travelWithInput(Vec3 pTravel){
        super.travel(pTravel);
    }

    @Override
    public void onPlayerJump(int pJumpPower) {
        super.onPlayerJump(pJumpPower);
    }

    @Override
    public void handleStopJump() {
        super.handleStopJump();
    }

    @Override
    public void handleStartJump(int pJumpPower) {
        super.handleStartJump(pJumpPower);
        if (this.isSaddled()) {
            if (pJumpPower < 0) {
                pJumpPower = 0;
            }
            if (pJumpPower >= 90) {
                this.roarPower = 1.0F;
            } else {
                this.roarPower = 0.4F + 0.4F * (float)pJumpPower / 90.0F;
            }
            if(!this.level.isClientSide){
                if(!this.isImmobile()){
                    this.roarTick=20;
                    this.level.broadcastEntityEvent(this, (byte)65);
                    this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
                }
            }
        }
    }

    public void activeEffectAura() {
        List<IllagerBeastEntity> beasts = this.level.getEntitiesOfClass(IllagerBeastEntity.class,this.getBoundingBox().inflate(15.0d),e -> e.getOwner()==this.getOwner());
        MobEffectInstance effect=this.getDrumEffect().getEffect();
        for (IllagerBeastEntity beast : beasts){
            beast.setIsExcited(true);
        }
        if(this.getOwner()!=null){
            for(Player player : this.level.getEntitiesOfClass(Player.class,this.getBoundingBox().inflate(15.0D))){
                player.addEffect(new MobEffectInstance(effect.getEffect(),effect.getDuration(),effect.getAmplifier(),effect.isAmbient(),effect.isVisible()));
            }
        }
    }

    protected void stopDrumSound(){
        if(!this.level.isClientSide){
            PacketHandler.sendToAllTracking(new PacketStopSound(ModSounds.DRUM_SOUND.getId(),SoundSource.HOSTILE),this);
        }
        this.drumTick=0;
    }

    public DrumBlock.Drum getDrumEffect(){
        return this.hasDrum() ? ((DrumBlock)Block.byItem(this.inventory.getItem(1).getItem())).getDrum() : null;
    }

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    @Override
    public void attackC() {
        if(this.getChargedState() == ChargedStates.CAN_CHARGED && !this.isImmobile()){
            this.setIsChargedState(1);
        }
        super.attackC();
    }

    @Override
    public void attackG() {
        if(!this.level.isClientSide){
            if(!this.isImmobile()){
                boolean flag=false;
                this.attackTick=10;
                this.level.broadcastEntityEvent(this, (byte)4);
                float f = this.yBodyRot * ((float)Math.PI / 180F);
                float f1 = Mth.sin(f);
                float f2 = Mth.cos(f);
                float f3 = 0.5f;
                BlockPos pos = new BlockPos(this.getX()-(f3*f1),this.getY()+1.5d,this.getZ()+(f3*f2));
                for(LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class,new AABB(pos).inflate(2.5d))){
                    if(living!=this && living!=this.getOwner() && !flag){
                        flag=true;
                        living.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                    }else if(flag){
                        break;
                    }
                }
                this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
            }
        }
        super.attackG();
    }

    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack saddle = this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack drum = this.getItemBySlot(EquipmentSlot.LEGS);
        this.updateContainerEquipment();
        ItemStack saddle1 = this.getItemBySlot(EquipmentSlot.FEET);
        ItemStack drum1 = this.getItemBySlot(EquipmentSlot.LEGS);
        if(this.tickCount > 20){
            if(this.isArmor(saddle1) && saddle!=saddle1){
                this.playSound(SoundEvents.ARMOR_EQUIP_DIAMOND);
            }else if(saddle1.getItem() instanceof SaddleItem && saddle!=saddle1){
                this.playSound(SoundEvents.HORSE_SADDLE);
            }
            if(drum1!=drum){
                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER);
            }
        }
        super.containerChanged(pInvBasic);
    }
    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            ItemStack stack = this.getContainer().getItem(0);
            ItemStack stack1 = this.getContainer().getItem(1);
            boolean flag = !stack.isEmpty();
            this.getAttribute(Attributes.ARMOR).removeModifier(WILD_RAVAGER_ARMOR_UUID);
            if(flag && stack.getItem() instanceof BeastArmorItem){
                int i = ((BeastArmorItem)stack.getItem()).getArmorValue();
                if (i != 0) {
                    this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(WILD_RAVAGER_ARMOR_UUID, "Ravager armor bonus", i, AttributeModifier.Operation.ADDITION));
                }
            }
            this.setHasDrum(!stack1.isEmpty());
            this.setIsSaddled(flag);
        }
        super.updateContainerEquipment();
    }

    public boolean isArmor(ItemStack chest1) {
        return chest1.getItem() instanceof BeastArmorItem armorItem && armorItem.getBeast() == Beast.WILD_RAVAGER || chest1.is(Items.SADDLE);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            for(Entity entity:this.getPassengers()){
                if(entity==this.getOwner()){
                    return (LivingEntity) entity;
                }
            }
        }else {
            return this.getFirstPassenger() instanceof LivingEntity ? (LivingEntity) this.getFirstPassenger() : null ;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshDimensions();
    }

    public void aiStep() {
        super.aiStep();
        if(this.getChargedState() == ChargedStates.PREPARE){
            this.prepareTimer--;
            if(this.prepareTimer==0){
                this.setIsChargedState(2);
                this.chargedTick=0;
            }
        }

        if(this.getChargedState() == ChargedStates.CHARGED){
            this.chargedTick++;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.40D);
            if(this.chargedTick>100){
                this.setIsChargedState(3);
                this.stunnedTick = 40;
                this.level.broadcastEntityEvent(this,(byte) 39);
            }
        }

        if(this.getChargedState() == ChargedStates.FINISH){
            this.nextAssaultTimer--;
            this.setIsChargedState(0);
        }

        if(this.hasDrum()){
            if(this.drumTick>0){
                this.drumTick--;
                if(this.drumTick==0){
                    this.stopDrumSound();
                    this.level.playSound(null,this, ModSounds.DRUM_SOUND.get(),SoundSource.HOSTILE,1.5f,1.0f);
                    this.level.broadcastEntityEvent(this,(byte) 64);
                    this.drumTick=600;
                }
                this.reAcvivateEffectTick--;
                if(this.reAcvivateEffectTick<0){
                    this.activeEffectAura();
                    this.reAcvivateEffectTick=200;
                }
                if(!this.hasPassenger(e->e==this.getOwner())){
                    this.stopDrumSound();
                }
            }
        }
        if (this.isAlive()) {
            if (this.isImmobile()) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
            } else {
                double d0 = this.getTarget() != null ? 0.30D : 0.29D;
                double d1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1D, d1, d0));
            }

            if (this.horizontalCollision && ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                boolean flag = false;
                AABB aabb = this.getBoundingBox().inflate(0.2D);

                for(BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (block instanceof LeavesBlock) {
                        flag = this.level.destroyBlock(blockpos, true, this) || flag;
                    }
                }
            }
            if (this.isAlive() && this.chargedTick<100) {
                for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                    if (!(this.isTame() && isAlliedTo(entity)) && !(!this.isTame() && entity instanceof WildRavagerEntity) && entity != this) {
                        entity.hurt(DamageSource.mobAttack(this), 8.0F + random.nextFloat() * 8.0F);
                        this.strongKnockback(entity);
                    }
                }
                this.maxUpStep = 2F;
            }else{
                this.maxUpStep = 1.1F;
            }


            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunnedTick > 0) {
                --this.stunnedTick;
                this.stunEffect();
                if (this.stunnedTick == 0) {
                    this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }
        }
    }

    public int getPrepareTimer() {
        return this.prepareTimer;
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

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0 || this.prepareTimer > 0;
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 && super.hasLineOfSight(p_149755_);
    }

    protected void blockedByShield(LivingEntity pEntity) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.stunnedTick = 40;
                this.roarPower=1.0F;
                this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
                this.level.broadcastEntityEvent(this, (byte)39);
                pEntity.push(this);
            } else {
                this.roarPower=1.0F;
                this.strongKnockback(pEntity);
            }

            pEntity.hurtMarked = true;
        }

    }

    private void roar() {
        if (this.isAlive()) {
            List<Entity> livingEntityList = this.isTame() ? this.level.getEntitiesOfClass(Entity.class,this.getBoundingBox().inflate(4.0d)) : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE);
            for(Entity livingentity : livingEntityList) {
                if(livingentity instanceof LivingEntity && livingentity!=this && livingentity!=this.getOwner()){
                    livingentity.hurt(DamageSource.mobAttack(this), 5.0F);
                    ((LivingEntity) livingentity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,120,0));
                    this.strongKnockback(livingentity);
                }else if(livingentity instanceof Projectile projectile){
                    projectile.shoot(projectile.getX()-this.getX(),projectile.getY()-this.getY(),projectile.getZ()-this.getZ(),1f,0.1f);
                }

            }
            Vec3 vec3 = this.getBoundingBox().getCenter();

            for(int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;
                this.level.addParticle(this.isCute()  ? ModParticles.HEART_BK_PARTICLES.get() : ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0, d1, d2);
            }
            this.gameEvent(GameEvent.ENTITY_ROAR);
        }

    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        double d3= this.getKnockbackPower();
        p_33340_.push(d0 / d2 * d3, 0.2D, d1 / d2 * d3);
    }

    private double getKnockbackPower(){
        return this.roarPower > 0 ? 4.0D*this.roarPower : 4.0D;
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        } else if (pId == 39) {
            this.stunnedTick = 40;
        } else if (pId == 65) {
            this.roarTick = 20;
        }else if (pId == 64){
            this.level.playSound((Player) null,this,ModSounds.DRUM_SOUND.get(),SoundSource.HOSTILE,1.5f,1.0f);
        }else if (pId == 59){
            Random random1 = new Random();
            this.prepareTimer=20;
            float f = this.yBodyRot * ((float) Math.PI / 180F);
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            double dx0 = this.getX() - (this.getX() - f2 + f1 * 0.4d);
            double dz0 = this.getZ() - (this.getZ() + f1 + f2 * 0.4d);
            double dx1 = this.getX() - (this.getX() - f2  - f1 * 0.4d);
            double dz1 = this.getZ() - (this.getZ() + f1  - f2 * 0.4d);
            double d0 = Math.max(dx0*dx0 + dz0*dz0,0.001D);
            double d1 = Math.max(dx1*dx1 + dz1*dz1,0.001D);
            for(int i = 0; i<10;i++){
                double r = random1.nextFloat(0.01F,0.2F);
                this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - f2 + f1 * 0.4d, this.getY(), this.getZ() + f1 + f2 * 0.4d, (dx0/d0)*r, random1.nextFloat(0.05F,0.1F),(dz0/d0)*r);
                this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - f2  - f1 * 0.4d, this.getY(), this.getZ() + f1  - f2 * 0.4d, (dx1/d1)*r   , random1.nextFloat(0.05F,0.1F), (dz1/d1)*r);
            }
        }else {
            super.handleEntityEvent(pId);
        }

    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
        data.addAnimationController(new AnimationController(this,"controller_States"
                ,0,this::predicate));
    }

    private <E extends IAnimatable>PlayState predicate(AnimationEvent<E> event) {
        if(this.getChargedState() == ChargedStates.PREPARE){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ravager.prepare", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
        }else if(this.getChargedState() == ChargedStates.CHARGED){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ravager.charged", ILoopType.EDefaultLoopTypes.LOOP));
        }else if(this.isSitting()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ravager.sit", ILoopType.EDefaultLoopTypes.LOOP));
        }else {
            event.getController().clearAnimationCache();
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }
    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    public boolean doHurtTarget(Entity pEntity) {
        this.attackTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(pEntity);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    public boolean hasArmor(){
        return !this.getContainer().getItem(0).isEmpty() && !(this.inventory.getItem(0).getItem() instanceof  SaddleItem);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        if (this.hasArmor()) {
            this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
        } else {
            this.playSound(SoundEvents.HORSE_STEP, 0.35F, -0.4F);
        }
    }
    @Override
    public boolean canEquipOnFeet(ItemStack p_39690_) {
        return super.canEquipOnFeet(p_39690_);
    }

    @Override
    public boolean canEquipOnLegs(ItemStack p_39690_) {
        return Block.byItem(p_39690_.getItem()) instanceof DrumBlock;
    }

    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return !pLevel.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    class RavagerMeleeAttackGoal extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(WildRavagerEntity.this, 1.0D, true);
        }

        protected double getAttackReachSqr(LivingEntity pAttackTarget) {
            float f = WildRavagerEntity.this.getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + pAttackTarget.getBbWidth());
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }
    }

    static class RavagerNavigation extends GroundPathNavigation {
        public RavagerNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new RavagerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }
    }

    static class RavagerNodeEvaluator extends WalkNodeEvaluator {

        protected BlockPathTypes evaluateBlockPathType(BlockGetter pLevel, boolean pCanOpenDoors, boolean pCanEnterDoors, BlockPos pPos, BlockPathTypes pNodeType) {
            return pNodeType == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(pLevel, pCanOpenDoors, pCanEnterDoors, pPos, pNodeType);
        }
    }

    public enum ChargedStates{
        PREPARE(1),
        CHARGED(2),
        FINISH(3),
        CAN_CHARGED(0);

        private static final ChargedStates[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ChargedStates::getId)).toArray(ChargedStates[]::new);
        private final int id;

        ChargedStates(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static ChargedStates byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }
    }
}
