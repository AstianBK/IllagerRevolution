package net.BKTeam.illagerrevolutionmod.entity.custom;

import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class IllagerBeastEntity extends TamableAnimal implements ContainerListener, IAnimatable {


    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PAINT_COLOR =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PAINTED =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ID_VARIANT =
            SynchedEntityData.defineId(IllagerBeastEntity.class, EntityDataSerializers.INT);

    IllagerBeastEntity(EntityType<? extends TamableAnimal> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    public int getTypeIdVariant(){
        return this.entityData.get(ID_VARIANT);
    }

    public MaulerEntity.Variant getIdVariant(){
        return MaulerEntity.Variant.byId(this.getTypeIdVariant() & 255);
    }

    public void setIdVariant(int pId){
        this.entityData.set(ID_VARIANT,pId);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_VARIANT, 0);
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_PAINT_COLOR,-1);
        this.entityData.define(PAINTED,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isSitting", this.isSitting());
        compound.putBoolean("isPainted",this.isPainted());
        compound.putInt("color",this.getColor().getId());
        compound.putInt("Variant", this.getTypeIdVariant());
    }
    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_PAINT_COLOR));
    }

    public void setColor(DyeColor pcolor) {
        this.entityData.set(DATA_PAINT_COLOR, pcolor.getId());
    }

    public boolean isPainted(){
        return this.entityData.get(PAINTED);
    }

    public void setPainted(boolean pBoolean){
        this.entityData.set(PAINTED,pBoolean);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSitting(compound.getBoolean("isSitting"));
        this.setPainted(compound.getBoolean("isPainted"));
        this.setIdVariant(compound.getInt("Variant"));
        if (compound.contains("color", 99)) {
            this.setColor(DyeColor.byId(compound.getInt("color")));
        }
    }
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if(this.random.nextFloat()>0.99f){
            this.setIdVariant(4);
        }else {
            this.setIdVariant(this.random.nextInt(0,4));
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    public Beast getTypeBeast(){
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        Random random = new Random();
        if(this.getIdVariant()==Variant.VARIANT5){
            double xp=this.getX() - this.getBbWidth() * Mth.sin (this.yBodyRot * Mth.PI/180) + random.nextDouble(-0.4d,0.4d);
            double yp=this.getY() + random.nextDouble(0.0d,2.0d);
            double zp=this.getZ() + this.getBbWidth() * Mth.cos (this.yBodyRot * Mth.PI/180) + random.nextDouble(-0.4d,0.4d);
            this.level.addParticle(ParticleTypes.GLOW,xp,yp,zp,0.0f,0.0f,0.0f);
        }
    }

    protected void updateContainerEquipment() {
    }


    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack=this.getItemInHand(pHand);
        if(!itemstack.isEmpty()){
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
            }
            else if(itemstack.is(Items.WATER_BUCKET) && this.isPainted()){
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
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    @Override
    public void containerChanged(Container pInvBasic) {

    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
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
