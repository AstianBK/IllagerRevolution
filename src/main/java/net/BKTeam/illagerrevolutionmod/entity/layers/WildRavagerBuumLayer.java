package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.block.custom.DrumBlock;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WildRavagerBuumLayer<T extends WildRavagerEntity,M extends EntityModel<T>> extends RenderLayer<T,M> {
    private BumModel model = null;
    public WildRavagerBuumLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderRaven(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
        this.renderRaven(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }

    private void renderRaven(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulderIn) {
        if(entitylivingbaseIn.hasDrum()){
            if (model == null) {
                model = new BumModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.BUUM));
            }
            ItemStack stack = entitylivingbaseIn.getContainer().getItem(1);
            if(Block.byItem(stack.getItem()) instanceof DrumBlock drumBlock){
                matrixStackIn.pushPose();
                matrixStackIn.scale(0.6f,0.6f,0.6f);
                matrixStackIn.translate(leftShoulderIn ? -1.5f : 1.0f , entitylivingbaseIn.isSitting() ? 1.0F : 0.0F, entitylivingbaseIn.isSitting() ? 0.7D : 0.3D);
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(this.getTexture(drumBlock.getDrum())));
                model.renderOnShoulder(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch, entitylivingbaseIn.tickCount);
                matrixStackIn.popPose();
            }

        }
    }

    private ResourceLocation getTexture(DrumBlock.Drum drum){
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/drum/drum_"+drum.getName()+".png");
    }
}

