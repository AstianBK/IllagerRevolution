package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulBombModel;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GeckoLivingProtectionLayer<T extends LivingEntity & IAnimatable> extends GeoLayerRenderer<T> {

    private final ResourceLocation LINKED_ARMOR=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/shield_bomb.png");
    private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));


    private SoulBombModel model = null;

    public GeckoLivingProtectionLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (model == null) {
            model = new SoulBombModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.ORB));
        }
        List<SoulBomb> souls = entitylivingbaseIn.level.getEntitiesOfClass(SoulBomb.class,entitylivingbaseIn.getBoundingBox().inflate(3.0d), e->e.isDefender() && e.getOwnerID()==entitylivingbaseIn.getId());
        if(getDefender(souls)) {
            matrixStackIn.pushPose();
            float f = getY(entitylivingbaseIn, partialTicks);
            float f1 = ((float)entitylivingbaseIn.tickCount + partialTicks) * 3.0F;
            float f3 = (float) entitylivingbaseIn.tickCount +partialTicks;
            float f4= 0.1f ;
            float f5= 0.8f ;
            float f6 = 0.4f ;
            matrixStackIn.translate(0.0D,0.2D,0.0D);
            matrixStackIn.translate(0.0D, (double)(1.5F + f / 2.0F), 0.0D);
            matrixStackIn.scale(entitylivingbaseIn.getBbHeight(),entitylivingbaseIn.getBbHeight(),entitylivingbaseIn.getBbHeight());
            int i = OverlayTexture.NO_OVERLAY;

            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
            matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
            this.model.renderToBuffer(matrixStackIn,bufferIn.getBuffer(RenderType.energySwirl(LINKED_ARMOR  ,f3*0.01f,f3*0.01f)),packedLightIn,i,f4,f5,f6,1.0f);
            this.model.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            matrixStackIn.popPose();
        }
    }

    public static boolean getDefender(List<SoulBomb> soulBombs){
        return !soulBombs.isEmpty();
    }

    public static float getY(LivingEntity p_114159_, float p_114160_) {
        float f = (float)p_114159_.tickCount + p_114160_;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }
}

