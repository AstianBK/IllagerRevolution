package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.FallenKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnight;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
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
        if(entityLivingBaseIn.getIdOwner()!=null){
            if(entityLivingBaseIn.getOwner().getMainHandItem().is(ModItems.ILLAGIUM_ALT_RUNED_BLADE.get())){
                if(entityLivingBaseIn.itIsLinked() && entityLivingBaseIn.isArmed()){
                    float f=!entityLivingBaseIn.getDamageLink() ? 0.1f : 1.0f;
                    float f1=!entityLivingBaseIn.getDamageLink() ? 0.8f : 0.0f;
                    float f2=!entityLivingBaseIn.getDamageLink() ? 0.4f : 0.0f;

                    tick=(float) entityLivingBaseIn.tickCount+partialTicks;
                    model.getModelLocation(entityLivingBaseIn);
                    model.getAnimationFileLocation(entityLivingBaseIn);
                    model.getTextureLocation(entityLivingBaseIn);
                    renderCopyModel(this.model,LINKED_ARMOR,matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,f,f1,f2);
                }
            }
        }

    }

    @Override
    public RenderType getRenderType(ResourceLocation textureLocation){
        return RenderType.energySwirl(textureLocation,tick*0.01f,tick*0.01f);
    }
}
