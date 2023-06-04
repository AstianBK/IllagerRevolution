package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.MaulerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class WarPaintMaulerLayer extends GeoLayerRenderer<MaulerEntity> {

    private final MaulerModel model;

    private final ResourceLocation LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/mauler/paint/mauler_warpaint.png");

    public WarPaintMaulerLayer(IGeoRenderer entityRendererIn, MaulerModel model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MaulerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityLivingBaseIn.isPainted()) {
            float[] f=entityLivingBaseIn.getColor().getTextureDiffuseColors();
            this.model.getModelResource(entityLivingBaseIn);
            this.model.getTextureResource(entityLivingBaseIn);
            this.model.getAnimationResource(entityLivingBaseIn);
            this.renderCopyModel(this.model,LOCATION,matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,f[0],f[1],f[2]);
        }
    }
}

