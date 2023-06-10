package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.MaulerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.MaulerArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.MaulerSaddlerLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class MaulerRenderer extends GeoEntityRenderer<MaulerEntity> {

    public MaulerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new MaulerModel());
        this.addLayer(new WarPaintBeastGeckoLayer<>(this,new MaulerModel()));
        this.addLayer(new MaulerSaddlerLayer(this,new MaulerModel()));
        this.addLayer(new MaulerArmorLayer(this,new MaulerModel()));
        this.shadowRadius = 0.5f;
    }
    @Override
    public RenderType getRenderType(MaulerEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        animatable.refreshDimensions();
        stack.scale(1.5f, 1.5f, 1.5f);
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
