package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WildRavagerArmorLayer <T extends WildRavagerEntity,M extends EntityModel<T>> extends RenderLayer<T,M> {

    private final ResourceLocation TEXTURE_SADDLE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/armor/wild_ravager_armor_saddle.png");

    private final WildRavagerModel model;

    public WildRavagerArmorLayer(RenderLayerParent<T, M> pRenderer, WildRavagerModel model) {
        super(pRenderer);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack =entityLivingBaseIn.getContainer().getItem(0);
        EntityModel<T> model = this.getParentModel();
        if (itemstack.getItem() instanceof BeastArmorItem armor) {
            model.prepareMobModel(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().copyPropertiesTo(model);
            VertexConsumer ivertex = bufferIn.getBuffer(RenderType.armorCutoutNoCull(armor.getArmorTexture()));
            model.setupAnim(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            model.renderToBuffer(matrixStackIn, ivertex, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if(itemstack.is(Items.SADDLE)){
            model.prepareMobModel(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().copyPropertiesTo(model);
            VertexConsumer ivertex = bufferIn.getBuffer(RenderType.armorCutoutNoCull(TEXTURE_SADDLE));
            model.setupAnim(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            model.renderToBuffer(matrixStackIn, ivertex, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }

    }
}

