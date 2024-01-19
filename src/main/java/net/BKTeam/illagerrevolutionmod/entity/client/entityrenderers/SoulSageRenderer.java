package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulSageModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.SoulSageEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.BKEyesLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;


public class SoulSageRenderer extends GeoEntityRenderer<SoulSageEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/soul_sage/soul_sage.png");
    private static final ResourceLocation TEXTURE_LOWLIFE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/soul_sage/soul_sage.png");
    private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/soul_sage.geo.json");

    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID ,"textures/entity/soul_drain.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);


    public SoulSageRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SoulSageModel<SoulSageEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"soul_sage"));
        this.addRenderLayer(new BKEyesLayer<>(this));
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(SoulSageEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    public boolean shouldRender(SoulSageEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        if (super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ)) {
            return true;
        } else {
            if (pLivingEntity.hasActiveAttackTarget()) {
                LivingEntity livingentity = pLivingEntity.getActiveAttackTarget0();
                LivingEntity livingentity1 = pLivingEntity.getActiveAttackTarget1();
                LivingEntity livingentity2 = pLivingEntity.getActiveAttackTarget2();
                if (livingentity != null) {
                    Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vec31 = this.getPosition(pLivingEntity, (double)pLivingEntity.getEyeHeight(), 1.0F);
                    return pCamera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
                }
                if (livingentity1 != null) {
                    Vec3 vec3 = this.getPosition(livingentity1, (double)livingentity1.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vec31 = this.getPosition(pLivingEntity, (double)pLivingEntity.getEyeHeight(), 1.0F);
                    return pCamera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
                }
                if (livingentity2 != null) {
                    Vec3 vec3 = this.getPosition(livingentity2, (double)livingentity2.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vec31 = this.getPosition(pLivingEntity, (double)pLivingEntity.getEyeHeight(), 1.0F);
                    return pCamera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
                }
            }

            return false;
        }
    }

    @Override
    public void render(SoulSageEntity pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack , MultiBufferSource bufferSource, int packedLight) {
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferSource, packedLight);
        List<LivingEntity> livings= pEntity.getDrainEntities();
        for (LivingEntity living : livings){
            if(living!=null){
                float f = 1.0F;
                float f1 = (float)pEntity.level().getDayTime() + pPartialTicks;
                float f2 = f1 * 0.5F % 1.0F;
                float f3 = pEntity.getEyeHeight();
                pMatrixStack.pushPose();
                pMatrixStack.translate(0.0D, (double)f3, 0.0D);
                Vec3 vec3 = this.getPosition(living, (double)living.getBbHeight() * 0.5D, pPartialTicks);
                Vec3 vec31 = this.getPosition(pEntity, (double)f3, pPartialTicks);
                Vec3 vec32 = vec3.subtract(vec31);
                float f4 = (float)(vec32.length() + 1.0D);
                vec32 = vec32.normalize();
                float f5 = (float)Math.acos(vec32.y);
                float f6 = (float)Math.atan2(vec32.z, vec32.x);
                pMatrixStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
                pMatrixStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
                int i = 1;
                float f7 = f1 * 0.05F * -1.5F;
                float f8 = f * f;
                int j = 84;
                int k = 255;
                int l = 166;
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

}
