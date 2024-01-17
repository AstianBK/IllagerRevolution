package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BulkwarkModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ShieldSoulsLayer extends GeoRenderLayer<BulkwarkEntity> {

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

    public ShieldSoulsLayer(GeoRenderer<BulkwarkEntity> entityRendererIn, BulkwarkModel<BulkwarkEntity> model) {
        super(entityRendererIn);
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, BulkwarkEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        renderType = RenderType.entityCutoutNoCull(LOCATION_BY_VARIANT.get(animatable.getShieldHealthStat()));
        renderer.reRender(this.getDefaultBakedModel(animatable),poseStack,bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);
    }


}

