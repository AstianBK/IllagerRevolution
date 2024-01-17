package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.block.custom.DrumBlock;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WildRavagerDrumLayer extends GeoRenderLayer<WildRavagerEntity> {
    private DrumModel model = null;

    public WildRavagerDrumLayer(GeoRenderer<WildRavagerEntity> entityRendererIn) {
        super(entityRendererIn);
    }


    private void renderDrum(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, WildRavagerEntity entitylivingbaseIn, boolean leftShoulderIn) {
        if(entitylivingbaseIn.hasDrum()){
            if (model == null) {
                model = new DrumModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.DRUM));
            }
            ItemStack stack = entitylivingbaseIn.getContainer().getItem(1);
            if(Block.byItem(stack.getItem()) instanceof DrumBlock drumBlock){
                matrixStackIn.pushPose();
                matrixStackIn.scale(0.7f,0.7f,0.7f);
                float f4 = 0.0F;
                if(entitylivingbaseIn.prepareTimer>0){
                    float f5=(20.0F-(float) entitylivingbaseIn.prepareTimer)/20.0F;
                    f4 = Mth.lerp(f5,0.0F+(0.5F*f5),0.5F-(0.5F*f5));
                }
                matrixStackIn.translate(leftShoulderIn ? -0.9f : 1.2f , entitylivingbaseIn.isSitting() ? 2.8F : 4.5F+f4 , entitylivingbaseIn.isSitting() ? 0.7D : 0.3D);
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(drumBlock.getLocation()));
                model.renderOnShoulder(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
                matrixStackIn.popPose();
            }

        }
    }

    @Override
    public void render(PoseStack poseStack, WildRavagerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        this.renderDrum(poseStack, bufferSource, packedLight, animatable, true);
        this.renderDrum(poseStack, bufferSource, packedLight, animatable, false);
    }
}

