package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.MaulerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class MaulerSaddlerLayer extends GeoRenderLayer<MaulerEntity> {


    private final ResourceLocation LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/mauler/mauler_saddle.png");

    public MaulerSaddlerLayer(GeoRenderer entityRendererIn, MaulerModel model) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, MaulerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isSaddled()) {
            renderType = RenderType.entityCutoutNoCull(LOCATION);
            renderer.reRender(getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);
        }
    }


}

