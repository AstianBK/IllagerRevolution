package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.ScroungerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
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
public class ScroungerArmorLayer extends GeoRenderLayer<ScroungerEntity> {
    private final ResourceLocation LOCATION=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/scrounger/armor/scrounger_armor_chest_leather.png");

    public ScroungerArmorLayer(GeoRenderer entityRendererIn, ScroungerModel model) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, ScroungerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if(animatable.hasChest()){
            renderType=RenderType.entityCutoutNoCull(LOCATION);
            renderer.reRender(bakedModel,poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);

        }
    }
}

