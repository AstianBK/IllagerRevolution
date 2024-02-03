package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.Patreon;
import net.BKTeam.illagerrevolutionmod.api.IHasInventory;
import net.BKTeam.illagerrevolutionmod.api.IOpenBeatsContainer;
import net.BKTeam.illagerrevolutionmod.enchantment.BKMobType;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.BKTeam.illagerrevolutionmod.network.PacketGlowEffect;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class IllagerBeastEntity extends TamableAnimal implements GeoEntity,ContainerListener, IHasInventory {


    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PAINT_COLOR =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PAINTED =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ID_VARIANT =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> ON_COMBAT =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> EXCITED =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    private int combatTimer;

    protected SimpleContainer inventory;

    private int excitedTimer;

    IllagerBeastEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.combatTimer=0;
        this.excitedTimer=0;
        this.createInventory();
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    public int getTypeIdVariant() {
        return this.entityData.get(ID_VARIANT);
    }

    public IllagerBeastEntity.Variant getIdVariant() {
        return IllagerBeastEntity.Variant.byId(this.getTypeIdVariant() & 255);
    }

    public void setIdVariant(int pId) {
        this.entityData.set(ID_VARIANT, pId);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ON_COMBAT,false);
        this.entityData.define(ID_VARIANT, 0);
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_PAINT_COLOR, -1);
        this.entityData.define(PAINTED, false);
        this.entityData.define(EXCITED,false);
    }

    @Override
    public MobType getMobType() {
        return BKMobType.BEAST_ILLAGER;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isExcited",this.isExcited());
        compound.putBoolean("onCombat",this.onCombat());
        compound.putBoolean("isSitting", this.isSitting());
        compound.putBoolean("isPainted", this.isPainted());
        compound.putInt("color", this.getColor().getId());
        compound.putInt("Variant", this.getTypeIdVariant());
    }

    public boolean isExcited() {
        return this.entityData.get(EXCITED);
    }

    public boolean isCute(){
        return this.getOwner() != null && this.getCustomName() != null && Patreon.isPatreon((Player) this.getOwner(), IllagerRevolutionMod.ACOLYTES_SKIN_UUID) && this.getCustomName().getString().equals("Cute");
    }

    public boolean isUndead(){
        return this.getOwner() != null && this.getCustomName() != null && Patreon.isPatreon((Player) this.getOwner(), IllagerRevolutionMod.MAGES_SKIN_UUID) && this.getCustomName().getString().equals("Undead");
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_PAINT_COLOR));
    }

    public void setColor(DyeColor pcolor) {
        this.entityData.set(DATA_PAINT_COLOR, pcolor.getId());
    }

    public boolean isPainted() {
        return this.entityData.get(PAINTED);
    }

    public void setPainted(boolean pBoolean) {
        this.entityData.set(PAINTED, pBoolean);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIsExcited(compound.getBoolean("isExcited"));
        this.setOnCombat(compound.getBoolean("onCombat"));
        this.setSitting(compound.getBoolean("isSitting"));
        this.setPainted(compound.getBoolean("isPainted"));
        this.setIdVariant(compound.getInt("Variant"));
        if (compound.contains("color", 99)) {
            this.setColor(DyeColor.byId(compound.getInt("color")));
        }
    }
    public int getRowInventory(int slot){
        return 36+18*slot;
    }
    public int getColumnInventory(int slot){
        return 8;
    }


    public void setIsExcited(boolean isExcited) {
        this.entityData.set(EXCITED,isExcited);
        if(isExcited){
            this.excitedTimer=500;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getSpeedBase()*1.1D);
        }else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getSpeedBase());
            this.excitedTimer=0;
        }
    }

    public double getSpeedBase(){
        return this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if (this.random.nextFloat() > 0.99f) {
            this.setIdVariant(4);
        } else {
            this.setIdVariant(this.random.nextInt(0, 4));
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    public Beast getTypeBeast() {
        return null;
    }

    public boolean onCombat() {
        return this.entityData.get(ON_COMBAT);
    }
    public void setOnCombat(boolean pBoolean){
        this.entityData.set(ON_COMBAT,pBoolean);
        this.combatTimer=pBoolean ? 500 : 0;
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide){
            if(this.getIdVariant()==Variant.VARIANT5 && this.level().random.nextFloat()<0.4f){
                PacketHandler.sendToAllTracking(new PacketGlowEffect(this),this);
            }
        }
        if(this.onCombat()){
            this.combatTimer--;
        }
        if(this.combatTimer<0){
            this.setOnCombat(false);
        }
        if(!this.onCombat()){
            if(this.isTame()){
                if(this.getMaxHealth()!=this.getHealth()){
                    if(this.tickCount%20==0){
                        if(!this.level().isClientSide){
                            this.heal(1);
                        }
                        this.spawParticleHeal();
                    }
                }
            }
        }
        if(this.isExcited()){
            this.excitedTimer--;
        }
        if(this.excitedTimer<0){
            this.setIsExcited(false);
        }
    }

    public void spawParticleHeal() {
        Random random = new Random();
        for (int i = 0 ; i<5 ; i++){
            double box = this.getBbWidth();
            double xp = this.getX() + random.nextDouble(-box, box);
            double yp = this.getY() + random.nextDouble(0.0d, this.getBbHeight());
            double zp = this.getZ() + random.nextDouble(-box, box);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,xp,yp,zp,0.0F,0.0F,0.0F);
        }
    }
    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.setOnCombat(true);
        return super.doHurtTarget(pEntity);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.setOnCombat(true);
        return super.hurt(pSource, pAmount);
    }

    protected void updateContainerEquipment() {
    }

    protected int getInventorySize() {
        return 0;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public void containerChanged(Container pInvBasic) {

    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }

    public void openInventory(Player player) {
        IllagerBeastEntity beast = (IllagerBeastEntity) ((Object) this);
        if (!this.level().isClientSide && player instanceof IOpenBeatsContainer) {
            ((IOpenBeatsContainer)player).openRakerInventory(beast, this.inventory);
        }
    }

    @Override
    public SimpleContainer getContainer() {
        return this.inventory;
    }

    public boolean hasInventoryChanged(Container beastContainer) {
        return this.inventory!= beastContainer;
    }

    public boolean canViewInventory() {
        return this.isTame();
    }

    public boolean canEquipOnFeet(ItemStack p_39690_) {
        return p_39690_.getItem() instanceof BeastArmorItem beastItem && beastItem.getBeast()==this.getTypeBeast();
    }

    public boolean canEquipOnLegs(ItemStack p_39690_) {
        return p_39690_.getItem() instanceof BeastArmorItem beastItem && beastItem.getBeast()==this.getTypeBeast();
    }


    public enum Variant {
        VARIANT1(0),
        VARIANT2(1),
        VARIANT3(2),
        VARIANT4(3),
        VARIANT5(4);

        private static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final int id;

        Variant(int p_30984_) {
            this.id = p_30984_;
        }

        public int getId() {
            return this.id;
        }

        public static Variant byId(int p_30987_) {
            return BY_ID[p_30987_ % BY_ID.length];
        }
    }
}
