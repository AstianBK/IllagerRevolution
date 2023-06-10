package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.ScroungerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class ScroungerArmorLayer extends GeoLayerRenderer<ScroungerEntity> {

    private final ScroungerModel model;

    public ScroungerArmorLayer(IGeoRenderer entityRendererIn, ScroungerModel model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ScroungerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack =entityLivingBaseIn.getContainer().getItem(0);
        if (itemstack.getItem() instanceof BeastArmorItem armor) {
            this.model.getModelResource(entityLivingBaseIn);
            this.model.getTextureResource(entityLivingBaseIn);
            this.model.getAnimationResource(entityLivingBaseIn);
            this.renderCopyModel(this.model,armor.getArmorTexture(),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
    }
}

