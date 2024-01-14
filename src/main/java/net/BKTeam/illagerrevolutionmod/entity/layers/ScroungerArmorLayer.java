package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.ScroungerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class ScroungerArmorLayer extends GeoLayerRenderer<ScroungerEntity> {

    private final ScroungerModel model;

    private final ResourceLocation LOCATION=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/scrounger/armor/scrounger_armor_chest_leather.png");

    public ScroungerArmorLayer(IGeoRenderer entityRendererIn, ScroungerModel model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ScroungerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityLivingBaseIn.hasChest()) {
            this.renderCopyModel(this.model,LOCATION,matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
    }
}

