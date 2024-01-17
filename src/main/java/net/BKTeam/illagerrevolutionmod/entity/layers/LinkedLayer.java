package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.FallenKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
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
public class LinkedLayer extends GeoRenderLayer<FallenKnightEntity> {
    private final ResourceLocation LINKED_ARMOR=new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final FallenKnightModel<FallenKnightEntity> model;
    private float tick;

    public LinkedLayer(GeoRenderer<FallenKnightEntity> entityRendererIn, FallenKnightModel<FallenKnightEntity> model) {
        super(entityRendererIn);
        this.model=model;
    }

    @Override
    public void render(PoseStack poseStack, FallenKnightEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        if(animatable.getOwner()!=null){
            if(animatable.getOwner().getMainHandItem().is(ModItems.ILLAGIUM_ALT_RUNED_BLADE.get())){
                if(animatable.itIsLinked() && animatable.isArmed()){
                    float f=!animatable.getDamageLink() ? 0.1f : 1.0f;
                    float f1=!animatable.getDamageLink() ? 0.8f : 0.0f;
                    float f2=!animatable.getDamageLink() ? 0.4f : 0.0f;

                    tick=(float) animatable.tickCount+partialTick;
                    renderType = RenderType.energySwirl(LINKED_ARMOR,tick*0.01f,tick*0.01f);
                    renderer.reRender(this.getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,f,f1,f2,1.0f);
                }
            }
        }
    }
}
