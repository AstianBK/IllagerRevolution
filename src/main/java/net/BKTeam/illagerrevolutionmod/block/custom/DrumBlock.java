package net.BKTeam.illagerrevolutionmod.block.custom;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.DrumBlockSpeedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DrumBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Drum drum;
    private final ResourceLocation location;

    public DrumBlock(Properties properties,Drum drum) {
        super(properties);
        this.drum=drum;
        this.location=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/drum/drum_"+drum.getName()+".png");

    }
    public Drum getDrum() {
        return this.drum;
    }

    public ResourceLocation getLocation(){
        return this.location;
    }

    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* FACING */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        pLevel.playSound(null,pPos,SoundEvents.ENDER_DRAGON_GROWL, SoundSource.BLOCKS,1.0f,-1.0f);
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DrumBlockSpeedEntity(pPos,pState);
    }

    public enum Drum{
        DAMAGE_DRUM("damage",new MobEffectInstance(MobEffects.DAMAGE_BOOST,500,0,false,false)),
        HEAL_DRUM("heal",new MobEffectInstance(MobEffects.REGENERATION,100,0,false,false)),
        SPEED_DRUM("speed",new MobEffectInstance(MobEffects.MOVEMENT_SPEED,500,0,false,false));

        final String name;

        final MobEffectInstance effect;
        Drum(String name,MobEffectInstance effect){
            this.name=name;
            this.effect=effect;
        }

        public String getName() {
            return this.name;
        }

        public MobEffectInstance getEffect(){
            return this.effect;
        }
    }
}


