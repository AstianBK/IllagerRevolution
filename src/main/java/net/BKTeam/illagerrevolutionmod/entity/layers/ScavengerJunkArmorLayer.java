package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.IllagerScavengerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ScavengerJunkArmorLayer extends GeoLayerRenderer<IllagerScavengerEntity> {

    private static final Map<IllagerScavengerEntity.ArmorTier, ResourceLocation> LOCATION_BY_TIER_ARMOR = Util.make(Maps.newEnumMap(IllagerScavengerEntity.ArmorTier.class), (p_114874_) -> {
        p_114874_.put(IllagerScavengerEntity.ArmorTier.LOW_ARMOR, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/scavenger_equip/junk_armor_1.png"));
        p_114874_.put(IllagerScavengerEntity.ArmorTier.MEDIUM_ARMOR, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/scavenger_equip/junk_armor_2.png"));
        p_114874_.put(IllagerScavengerEntity.ArmorTier.HEAVY_ARMOR, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/scavenger_equip/junk_armor_3.png"));
    });

    private final IllagerScavengerModel model;

    public ScavengerJunkArmorLayer(IGeoRenderer<IllagerScavengerEntity> entityRendererIn,IllagerScavengerModel model) {
        super(entityRendererIn);
        this.model= model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, IllagerScavengerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entityLivingBaseIn.getArmorTier() != IllagerScavengerEntity.ArmorTier.NONE){
            this.renderCopyModel(this.model,LOCATION_BY_TIER_ARMOR.get(entityLivingBaseIn.getArmorTier()),matrixStackIn,bufferIn,packedLightIn,entityLivingBaseIn,partialTicks,1.0f,1.0f,1.0f);
        }
    }
}
