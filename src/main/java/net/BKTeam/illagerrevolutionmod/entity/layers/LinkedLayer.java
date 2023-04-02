package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.FallenKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.CallbackI;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class LinkedLayer extends GeoLayerRenderer<FallenKnight> {
    private final ResourceLocation LINKED_ARMOR=new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final FallenKnightModel<FallenKnight> model;
    private float tick;

    public LinkedLayer(IGeoRenderer<FallenKnight> entityRendererIn,FallenKnightModel<FallenKnight> model) {
        super(entityRendererIn);
        this.model=model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, FallenKnight entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entityLivingBaseIn.itIsLinked()){
            this.tick=(float) entityLivingBaseIn.tickCount+partialTicks;
            model.getModelLocation(entityLivingBaseIn);
            model.getAnimationFileLocation(entityLivingBaseIn);
            model.getTextureLocation(entityLivingBaseIn);
            renderCopyModel(this.model,LINKED_ARMOR,matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
    }

    @Override
    public RenderType getRenderType(ResourceLocation textureLocation){
        return RenderType.energySwirl(textureLocation,this.tick*0.01f,this.tick*0.01f);
    }
}
