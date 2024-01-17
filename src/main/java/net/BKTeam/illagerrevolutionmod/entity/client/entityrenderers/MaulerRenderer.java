package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.MaulerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.CuteLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.MaulerArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.MaulerSaddlerLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MaulerRenderer extends GeoEntityRenderer<MaulerEntity> {

    public MaulerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new MaulerModel());
        this.addRenderLayer(new CuteLayer<>(this,new MaulerModel()));
        this.addRenderLayer(new WarPaintBeastGeckoLayer<>(this,new MaulerModel()));
        this.addRenderLayer(new MaulerSaddlerLayer(this,new MaulerModel()));
        this.addRenderLayer(new MaulerArmorLayer(this,new MaulerModel()));
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(MaulerEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.5f, 1.5f, 1.5f);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(MaulerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }


    @Override
    public Color getRenderColor(MaulerEntity animatable, float partialTick, int packedLight) {
        return !animatable.isSavager() ? Color.WHITE : Color.ofRGB(1.0f,0.6f,0.6f);
    }
}
