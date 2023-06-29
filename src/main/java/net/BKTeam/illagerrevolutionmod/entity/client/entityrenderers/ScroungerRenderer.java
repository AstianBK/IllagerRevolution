package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.ScroungerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.ScroungerArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
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

public class ScroungerRenderer extends ExtendedGeoEntityRenderer<ScroungerEntity> {

    public ScroungerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new ScroungerModel());
        this.addLayer(new ScroungerArmorLayer(this,new ScroungerModel()));
        this.addLayer(new WarPaintBeastGeckoLayer<>(this,new ScroungerModel()));
        this.shadowRadius = 0.5f;
    }
    @Override
    public RenderType getRenderType(ScroungerEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        animatable.refreshDimensions();
        stack.scale(1.0f, 1.0f, 1.0f);
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, ScroungerEntity animatable) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, ScroungerEntity animatable) {
        return null;
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack stack, String boneName) {
        return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, ScroungerEntity animatable) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, ScroungerEntity currentEntity, IBone bone) {
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState state, String boneName, ScroungerEntity animatable) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack stack, String boneName, ScroungerEntity animatable, IBone bone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState state, String boneName, ScroungerEntity animatable) {

    }
}
