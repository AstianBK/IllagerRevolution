package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.RakerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.ScrapperArmorLayer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RakerRenderer extends GeoEntityRenderer<RakerEntity> {

    public RakerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new RakerModel());
        this.addLayer(new ScrapperArmorLayer(this,new RakerModel()));
        this.addLayer(new WarPaintBeastGeckoLayer<>(this,new RakerModel()));
        this.shadowRadius = 0.5f;
    }
    @Override
    public RenderType getRenderType(RakerEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        animatable.refreshDimensions();
        stack.scale(animatable.getScale(), animatable.getScale(), animatable.getScale());
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
