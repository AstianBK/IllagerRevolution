package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.BKTeam.illagerrevolutionmod.block.custom.DrumBlock;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class WildRavagerDrumLayer extends GeoLayerRenderer<WildRavagerEntity> {
    private DrumModel model = null;

    public WildRavagerDrumLayer(IGeoRenderer<WildRavagerEntity> entityRendererIn) {
        super(entityRendererIn);
    }


    private void renderRaven(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,WildRavagerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulderIn) {
        if(entitylivingbaseIn.hasDrum()){
            if (model == null) {
                model = new DrumModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.DRUM));
            }
            ItemStack stack = entitylivingbaseIn.getContainer().getItem(1);
            if(Block.byItem(stack.getItem()) instanceof DrumBlock drumBlock){
                matrixStackIn.pushPose();
                matrixStackIn.scale(0.7f,0.7f,0.7f);
                matrixStackIn.translate(leftShoulderIn ? -0.9f : 1.2f , entitylivingbaseIn.isSitting() ? 3.5F : 4.5F, entitylivingbaseIn.isSitting() ? 0.7D : 0.3D);
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(drumBlock.getLocation()));
                model.renderOnShoulder(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, entitylivingbaseIn.tickCount);
                matrixStackIn.popPose();
            }

        }
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, WildRavagerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderRaven(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
        this.renderRaven(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }
}

