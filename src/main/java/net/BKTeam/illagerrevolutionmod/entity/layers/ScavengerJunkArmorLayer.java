package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.IllagerScavengerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
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
public class ScavengerJunkArmorLayer extends GeoRenderLayer<IllagerScavengerEntity> {

    private static final Map<IllagerScavengerEntity.ArmorTier, ResourceLocation> LOCATION_BY_TIER_ARMOR = Util.make(Maps.newEnumMap(IllagerScavengerEntity.ArmorTier.class), (p_114874_) -> {
        p_114874_.put(IllagerScavengerEntity.ArmorTier.LOW_ARMOR, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/scavenger_equip/junk_armor_1.png"));
        p_114874_.put(IllagerScavengerEntity.ArmorTier.MEDIUM_ARMOR, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/scavenger_equip/junk_armor_2.png"));
        p_114874_.put(IllagerScavengerEntity.ArmorTier.HEAVY_ARMOR, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/scavenger_equip/junk_armor_3.png"));
    });
    public ScavengerJunkArmorLayer(GeoRenderer<IllagerScavengerEntity> entityRendererIn, IllagerScavengerModel model) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, IllagerScavengerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        renderType = RenderType.entityCutoutNoCull(LOCATION_BY_TIER_ARMOR.get(animatable.getArmorTier()));
        if(animatable.getArmorTier() != IllagerScavengerEntity.ArmorTier.NONE){
            renderer.reRender(getDefaultBakedModel(animatable),poseStack,
                    bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),
                    partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0F,1.0F,1.0F,1.0F);
        }
    }


}
