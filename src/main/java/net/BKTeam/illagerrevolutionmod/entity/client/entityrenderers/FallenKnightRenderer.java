package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.FallenKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.LinkedLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import static net.BKTeam.illagerrevolutionmod.ModConstants.POTION_BONE_IDENT;
import static net.BKTeam.illagerrevolutionmod.ModConstants.RIGHT_HAND_BONE_IDENT;

public class FallenKnightRenderer extends ExtendedGeoEntityRenderer<FallenKnightEntity> {

    public FallenKnightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FallenKnightModel<>());
        this.addLayer(new LinkedLayer(this,new FallenKnightModel<>()));
        this.shadowRadius = 0.5f;
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, FallenKnightEntity currentEntity) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, FallenKnightEntity currentEntity) {
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
    protected BlockState getHeldBlockForBone(String boneName, FallenKnightEntity currentEntity) {
        return null;

    }

    @Override
    protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, FallenKnightEntity currentEntity, IBone bone) {
        if (item == currentEntity.getMainHandItem() || item == currentEntity.getOffhandItem()) {

            stack.mulPose(Vector3f.XP.rotationDegrees(270F));
            boolean shieldFlag = item.getItem() instanceof ShieldItem;

            if (item == currentEntity.getMainHandItem()) {
                if (shieldFlag) {
                    stack.translate(0, 0.125, -15);
                }else {

                }
            } else {
                if (shieldFlag) {
                    stack.translate(0, 0.125, 0.25);
                    stack.mulPose(Vector3f.YP.rotationDegrees(180));
                }else {

                }

            }
            // stack.mulPose(Vector3f.YP.rotationDegrees(180));

            // stack.scale(0.75F, 0.75F, 0.75F);
        }
    }

    @Override
    protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, FallenKnightEntity currentEntity) {

    }

    @Override
    protected void postRenderItem(PoseStack stack, ItemStack item, String boneName, FallenKnightEntity currentEntity, IBone bone) {

    }

    @Override
    protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, FallenKnightEntity currentEntity) {

    }

    @Override
    public RenderType getRenderType(FallenKnightEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1, 1, 1);
        return RenderType.entityTranslucent(textureLocation);
    }
}
