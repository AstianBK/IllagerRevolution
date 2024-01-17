package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerGModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WildRavagerArmorLayer extends GeoRenderLayer<WildRavagerEntity> {


    private final ResourceLocation TEXTURE_SADDLE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/armor/wild_ravager_armor_saddle.png");

    public WildRavagerArmorLayer(GeoRenderer<WildRavagerEntity> entityRendererIn, WildRavagerGModel model) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, WildRavagerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ItemStack itemstack = animatable.getContainer().getItem(0);
        if (itemstack.getItem() instanceof BeastArmorItem armor) {
            renderType=RenderType.entityCutoutNoCull(armor.getArmorTexture(itemstack));
            renderer.reRender(getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);
        }
        if(itemstack.is(Items.SADDLE)){
            renderType=RenderType.entityCutoutNoCull(TEXTURE_SADDLE);
            renderer.reRender(getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);
        }
    }
}

