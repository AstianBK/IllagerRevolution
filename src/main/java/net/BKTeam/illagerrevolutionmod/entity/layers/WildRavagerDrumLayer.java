package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.block.custom.DrumBlock;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WildRavagerDrumLayer<T extends WildRavagerEntity> extends GeoRenderLayer<T> {
    private DrumModel model = null;

    public WildRavagerDrumLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }


    private void renderDrum(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, boolean leftShoulderIn, float partialTick, GeoBone bone) {
        if(entitylivingbaseIn.hasDrum()){
            if (model == null) {
                model = new DrumModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.DRUM));
            }
            ItemStack stack = entitylivingbaseIn.getContainer().getItem(1);
            if(Block.byItem(stack.getItem()) instanceof DrumBlock drumBlock){
                matrixStackIn.pushPose();
                matrixStackIn.scale(-0.7F, -0.7F, 0.7F);
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(drumBlock.getLocation()));
                matrixStackIn.translate(leftShoulderIn ? 0.4D : -0.8D,-1.2D,0.7D);
                bone.setRotX(0.785398F);
                prepModelPartForRender(matrixStackIn,bone,model.getRoot());
                model.renderDrum(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY,bone);
                matrixStackIn.popPose();
            }
        }
    }

    @Override
    public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if(bone.getName().equals("drum_left") || bone.getName().equals("drum_right")){
            this.renderDrum(poseStack, bufferSource, packedLight, animatable,
                    bone.getName().equals("drum_left"),partialTick,bone);
            bufferSource.getBuffer(renderType);
        }
    }

    protected void prepModelPartForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {
        sourcePart.setPos(-(bone.getPivotX() - ((bone.getPivotX()) - bone.getPivotX())),
                -(bone.getPivotY() - ((bone.getPivotY()) - bone.getPivotY())),
                (bone.getPivotZ() - ((bone.getPivotZ()) - bone.getPivotZ())));

        sourcePart.xRot = -bone.getRotX();
        sourcePart.yRot = -bone.getRotY();
        sourcePart.zRot = bone.getRotZ();

    }

}

