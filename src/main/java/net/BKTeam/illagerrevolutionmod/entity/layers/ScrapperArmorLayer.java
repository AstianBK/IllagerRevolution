package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.RakerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class ScrapperArmorLayer extends GeoLayerRenderer<RakerEntity> {

    private final RakerModel model;

    public ScrapperArmorLayer(IGeoRenderer entityRendererIn, RakerModel model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, RakerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack =entityLivingBaseIn.getItemBySlot(EquipmentSlot.FEET);
        ItemStack itemstack1 = entityLivingBaseIn.getItemBySlot(EquipmentSlot.LEGS);
        if (itemstack.getItem() instanceof BeastArmorItem armor) {
            this.renderCopyModel(this.model,armor.getArmorTexture(itemstack),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
        if(itemstack1.getItem() instanceof BeastArmorItem armorItem){
            this.renderCopyModel(this.model,armorItem.getArmorTexture(itemstack1),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }

    }
}

