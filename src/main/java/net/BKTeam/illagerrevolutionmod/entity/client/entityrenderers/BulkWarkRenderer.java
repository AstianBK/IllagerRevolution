package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BladeKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BulkwarkModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.ShieldSoulsLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import static net.BKTeam.illagerrevolutionmod.ModConstants.POTION_BONE_IDENT;
import static net.BKTeam.illagerrevolutionmod.ModConstants.RIGHT_HAND_BONE_IDENT;

public class BulkWarkRenderer extends ExtendedGeoEntityRenderer<BulkwarkEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/bulkwark/bulkwark.png");
    private static final ResourceLocation TEXTURE_LOWLIFE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/bulkwark/bulkwark_lowhealth.png");
    private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/bulkwark.geo.json");

    public BulkWarkRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BulkwarkModel<BulkwarkEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"bulkwark"));
        this.addLayer(new ShieldSoulsLayer(this,new BulkwarkModel<BulkwarkEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"bulkwark")));
        this.shadowRadius = 0.5f;
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, BulkwarkEntity currentEntity) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, BulkwarkEntity currentEntity) {
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
    protected BlockState getHeldBlockForBone(String boneName, BulkwarkEntity currentEntity) {
        return null;

    }

    @Override
    protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, BulkwarkEntity currentEntity, IBone bone) {
        CompoundTag nbt;
        int cc1=6;
        nbt=item.getOrCreateTag();
        nbt.putInt("CustomModelData", cc1);
    }

    @Override
    protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, BulkwarkEntity currentEntity) {
    }

    @Override
    protected void postRenderItem(PoseStack stack, ItemStack item, String boneName, BulkwarkEntity currentEntity, IBone bone) {
    }

    @Override
    protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, BulkwarkEntity currentEntity) {

    }

    @Override
    public void render(BulkwarkEntity pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack , MultiBufferSource bufferSource, int packedLight) {
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(BulkwarkEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1, 1, 1);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

}
