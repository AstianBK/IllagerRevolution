package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WildRavagerCuteLayer<T extends WildRavagerEntity,M extends EntityModel<T>> extends RenderLayer<T,M> {

    private final ResourceLocation TEXTURE_CUTE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/wild_ravager_cute.png");

    public WildRavagerCuteLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityModel<T> model = this.getParentModel();
        if (entityLivingBaseIn.isCute()) {
            model.prepareMobModel(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().copyPropertiesTo(model);
            VertexConsumer ivertex = bufferIn.getBuffer(RenderType.armorCutoutNoCull(TEXTURE_CUTE));
            model.setupAnim(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            model.renderToBuffer(matrixStackIn, ivertex, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

