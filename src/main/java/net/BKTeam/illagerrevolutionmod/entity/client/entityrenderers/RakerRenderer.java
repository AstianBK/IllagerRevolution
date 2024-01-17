package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.RakerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.CuteLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.ScrapperArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RakerRenderer extends GeoEntityRenderer<RakerEntity> {

    public RakerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new RakerModel());
        this.addRenderLayer(new CuteLayer<>(this,new RakerModel()));
        this.addRenderLayer(new WarPaintBeastGeckoLayer<>(this,new RakerModel()));
        this.addRenderLayer(new ScrapperArmorLayer(this,new RakerModel()));
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(RakerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        animatable.refreshDimensions();
        return RenderType.entityTranslucent(getTextureLocation(animatable));

    }
}
