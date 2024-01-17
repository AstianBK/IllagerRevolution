package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GeckoLivingProtectionLayer<T extends LivingEntity & GeoEntity> extends GeoRenderLayer<T> {

    private final ResourceLocation LINKED_ARMOR=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/shield_bomb.png");
    private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));


    private SoulBombModel model = null;

    public GeckoLivingProtectionLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (model == null) {
            model = new SoulBombModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.ORB));
        }
        List<SoulBomb> souls = animatable.level().getEntitiesOfClass(SoulBomb.class,animatable.getBoundingBox().inflate(3.0d), e->e.isDefender() && e.getOwnerID()==animatable.getId());
        if(getDefender(souls)) {
            poseStack.pushPose();
            float f = getY(animatable, partialTick);
            float f1 = ((float)animatable.tickCount + partialTick) * 3.0F;
            float f3 = (float) animatable.tickCount +partialTick;
            float f4;
            float f5;
            float f6;
            poseStack.translate(0.0D,0.2D,0.0D);
            poseStack.translate(0.0D, (double)(1.5F + f / 2.0F), 0.0D);
            poseStack.scale(animatable.getBbHeight(),animatable.getBbHeight(),animatable.getBbHeight());
            int i = OverlayTexture.NO_OVERLAY;
            if(souls.get(0).discardMoment){
                f4=1.0F;
                f5=0.0F;
                f6=0.0F;
            }else {
                f4=1.0F;
                f5=1.0F;
                f6=1.0F;
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(f1));
            poseStack.mulPose(new Quaternionf().setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
            this.model.renderToBuffer(poseStack,bufferSource.getBuffer(RenderType.energySwirl(LINKED_ARMOR  ,f3*0.01f,f3*0.01f)),packedLight,i,f4,f5,f6 ,1.0f);
            poseStack.popPose();
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

