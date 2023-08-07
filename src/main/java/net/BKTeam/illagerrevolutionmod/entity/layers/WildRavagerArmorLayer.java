package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerGModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class WildRavagerArmorLayer extends GeoLayerRenderer<WildRavagerEntity> {

    private final WildRavagerGModel model;

    private final ResourceLocation TEXTURE_SADDLE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/armor/wild_ravager_armor_saddle.png");

    public WildRavagerArmorLayer(IGeoRenderer<WildRavagerEntity> entityRendererIn,WildRavagerGModel model) {
        super(entityRendererIn);
        this.model=model;
    }


    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, WildRavagerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entityLivingBaseIn.getContainer().getItem(0);
        if (itemstack.getItem() instanceof BeastArmorItem armorItem) {
            this.model.getModelResource(entityLivingBaseIn);
            this.model.getTextureResource(entityLivingBaseIn);
            this.model.getAnimationResource(entityLivingBaseIn);
            this.renderCopyModel(this.model,armorItem.getArmorTexture(itemstack),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
        if(itemstack.is(Items.SADDLE)){
            this.model.getModelResource(entityLivingBaseIn);
            this.model.getTextureResource(entityLivingBaseIn);
            this.model.getAnimationResource(entityLivingBaseIn);
            this.renderCopyModel(this.model,TEXTURE_SADDLE,matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
    }
}

