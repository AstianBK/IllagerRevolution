package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;


@OnlyIn(Dist.CLIENT)
public class CuteLayer<T extends IllagerBeastEntity> extends GeoLayerRenderer<T> {

    private final AnimatedGeoModel<T> model;

    public CuteLayer(IGeoRenderer entityRendererIn, AnimatedGeoModel<T> model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityLivingBaseIn.isCute()) {
            this.renderCopyModel(this.model, getTextureCute(entityLivingBaseIn),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
    }

    public ResourceLocation getTextureCute(T entity){
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/"+entity.getTypeBeast().getBeastName()+"/"+entity.getTypeBeast().getBeastName()+"_cute.png");
    }
}