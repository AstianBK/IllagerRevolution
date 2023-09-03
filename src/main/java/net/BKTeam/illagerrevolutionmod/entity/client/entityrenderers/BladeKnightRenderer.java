package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BladeKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID ,"textures/entity/soul_drain.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);


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
    public boolean shouldRender(BladeKnightEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        if (super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ)) {
            return true;
        } else {
            if (pLivingEntity.hasActiveAttackTarget()) {
                LivingEntity livingentity = pLivingEntity.getActiveAttackTarget();
                if (livingentity != null) {
                    Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vec31 = this.getPosition(pLivingEntity, (double)pLivingEntity.getEyeHeight(), 1.0F);
                    return pCamera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
                }
            }

            return false;
        }
    }

    @Override
    public void render(BladeKnightEntity pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack , MultiBufferSource bufferSource, int packedLight) {
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferSource, packedLight);
        LivingEntity livingentity = pEntity.getActiveAttackTarget();
        if (livingentity != null) {
            float f = 1.0F;
            float f1 = (float)pEntity.level.getGameTime() + pPartialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = pEntity.getEyeHeight();
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.0D, (double)f3, 0.0D);
            Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, pPartialTicks);
            Vec3 vec31 = this.getPosition(pEntity, (double)f3, pPartialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + 1.0D);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
            float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
            float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
            float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = bufferSource.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = pMatrixStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (pEntity.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            pMatrixStack.popPose();
        }
    }

    private static void vertex(VertexConsumer p_114842_, Matrix4f p_114843_, Matrix3f p_114844_, float p_114845_, float p_114846_, float p_114847_, int p_114848_, int p_114849_, int p_114850_, float p_114851_, float p_114852_) {
        p_114842_.vertex(p_114843_, p_114845_, p_114846_, p_114847_).color(p_114848_, p_114849_, p_114850_, 255).uv(p_114851_, p_114852_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_114844_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private Vec3 getPosition(LivingEntity pLivingEntity, double p_114804_, float p_114805_) {
        double d0 = Mth.lerp((double)p_114805_, pLivingEntity.xOld, pLivingEntity.getX());
        double d1 = Mth.lerp((double)p_114805_, pLivingEntity.yOld, pLivingEntity.getY()) + p_114804_;
        double d2 = Mth.lerp((double)p_114805_, pLivingEntity.zOld, pLivingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    @Override
    public RenderType getRenderType(BladeKnightEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1, 1, 1);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

}
