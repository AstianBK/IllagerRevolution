package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BulkwarkModel;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.MaulerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ShieldSoulsLayer extends GeoLayerRenderer<BulkwarkEntity> {

    private static final Map<BulkwarkEntity.ShieldHealth, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(BulkwarkEntity.ShieldHealth.class), (p_114874_) -> {
        p_114874_.put(BulkwarkEntity.ShieldHealth.NONE, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_0.png"));
        p_114874_.put(BulkwarkEntity.ShieldHealth.SOUL_1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_1.png"));
        p_114874_.put(BulkwarkEntity.ShieldHealth.SOUL_2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_2.png"));
        p_114874_.put(BulkwarkEntity.ShieldHealth.SOUL_3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_3.png"));
        p_114874_.put(BulkwarkEntity.ShieldHealth.SOUL_4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_4.png"));
        p_114874_.put(BulkwarkEntity.ShieldHealth.SOUL_5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_5.png"));
        p_114874_.put(BulkwarkEntity.ShieldHealth.SOUL_6, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/bulkwark/shield/bulkwark_6.png"));
    });

    private final BulkwarkModel<BulkwarkEntity> model;

    public ShieldSoulsLayer(IGeoRenderer entityRendererIn, BulkwarkModel<BulkwarkEntity> model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, BulkwarkEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.model.getModelResource(entityLivingBaseIn);
        this.model.getTextureResource(entityLivingBaseIn);
        this.model.getAnimationResource(entityLivingBaseIn);
        this.renderCopyModel(this.model,LOCATION_BY_VARIANT.get(entityLivingBaseIn.getShieldHealthStat()),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
    }
}

