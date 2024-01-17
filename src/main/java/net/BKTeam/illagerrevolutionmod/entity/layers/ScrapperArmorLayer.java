package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.RakerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class ScrapperArmorLayer extends GeoRenderLayer<RakerEntity> {

    private final RakerModel model;

    public ScrapperArmorLayer(GeoRenderer entityRendererIn, RakerModel model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, RakerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ItemStack itemstack =animatable.getItemBySlot(EquipmentSlot.FEET);
        ItemStack itemstack1 = animatable.getItemBySlot(EquipmentSlot.LEGS);
        if (itemstack.getItem() instanceof BeastArmorItem armor) {
            renderType=RenderType.entityCutoutNoCull(armor.getArmorTexture(itemstack));
            renderer.reRender(bakedModel,poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);
        }
        if(itemstack1.getItem() instanceof BeastArmorItem armorItem){
            renderType=RenderType.entityCutoutNoCull(armorItem.getArmorTexture(itemstack1));
            renderer.reRender(bakedModel,poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);

        }
    }
    
}

