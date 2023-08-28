package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BladeKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import static net.BKTeam.illagerrevolutionmod.ModConstants.*;

public class BladeKnightRenderer extends ExtendedGeoEntityRenderer<BladeKnightEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/blade_knight/blade_knight.png");
    private static final ResourceLocation TEXTURE_LOWLIFE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/blade_knight/blade_knight_lowhealth.png");
    private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/blade_knight.geo.json");

    public BladeKnightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BladeKnightModel<BladeKnightEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"blade_knight"));
        this.shadowRadius = 0.5f;
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, BladeKnightEntity currentEntity) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, BladeKnightEntity currentEntity) {
        switch (boneName) {
            case RIGHT_HAND_BONE_IDENT:
                return currentEntity.getMainHandItem();
            case POTION_BONE_IDENT:
                break;
        }
        return null;
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, BladeKnightEntity currentEntity) {
        return null;

    }

    @Override
    protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, BladeKnightEntity currentEntity, IBone bone) {
        float cc = 290.0f;
        CompoundTag nbt;
        int cc1=6;
        if ((item == currentEntity.getMainHandItem() || item == currentEntity.getOffhandItem()) && !currentEntity.hasCombo()) {
            stack.mulPose(Vector3f.XP.rotationDegrees(cc));
            stack.mulPose(Vector3f.YP.rotationDegrees(0f));
            boolean shieldFlag = item.getItem() instanceof ShieldItem;

            if (item == currentEntity.getMainHandItem()) {
                if (shieldFlag) {
                    stack.translate(0, 0.125, -15);
                }
            } else {
                if (shieldFlag) {
                    stack.translate(0, 0.125, 0.25);
                    stack.mulPose(Vector3f.YP.rotationDegrees(120.0f));
                }
            }
        }
        nbt=item.getOrCreateTag();
        nbt.putInt("CustomModelData", cc1);
    }

    @Override
    protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, BladeKnightEntity currentEntity) {
    }

    @Override
    protected void postRenderItem(PoseStack stack, ItemStack item, String boneName, BladeKnightEntity currentEntity, IBone bone) {
    }

    @Override
    protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, BladeKnightEntity currentEntity) {

    }

    @Override
    public RenderType getRenderType(BladeKnightEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1, 1, 1);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

}
