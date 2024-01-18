package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WarPaintBeastGeckoLayer<T extends IllagerBeastEntity> extends GeoRenderLayer<T> {


    public WarPaintBeastGeckoLayer(GeoRenderer entityRendererIn, GeoModel<T> model) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isPainted()) {
            float[] f=animatable.getColor().getTextureDiffuseColors();
            renderType = RenderType.entityCutoutNoCull(getTextureWarPaint(animatable));
            renderer.reRender(getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,f[0],f[1],f[2],1.0f);
        }
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    public ResourceLocation getTextureWarPaint(T entity){
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/"+entity.getTypeBeast().getBeastName()+"/paint/"+entity.getTypeBeast().getBeastName()+"_warpaint.png");
    }
}

