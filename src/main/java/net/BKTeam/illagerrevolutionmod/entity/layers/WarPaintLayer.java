package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class WarPaintLayer<T extends WildRavagerEntity,M extends EntityModel<T>> extends RenderLayer<T,M>{
    private final ResourceLocation WAR_PAINT=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/paint/wild_ravager_warpaint.png");

    public WarPaintLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);

    }
    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing,
                       float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
                       float pHeadPitch) {
        if(pLivingEntity.isPainted()){
            EntityModel<T> model = this.getParentModel();
            float[] f=pLivingEntity.getColor().getTextureDiffuseColors();
            renderColoredCutoutModel(model,WAR_PAINT,pMatrixStack,pBuffer,pPackedLight,pLivingEntity,f[0],f[1],f[2]);
        }
    }
}