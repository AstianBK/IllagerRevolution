package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BulkwarkModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.ShieldSoulsLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BulkWarkRenderer extends GeoEntityRenderer<BulkwarkEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/bulkwark/bulkwark.png");
    private static final ResourceLocation TEXTURE_LOWLIFE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/bulkwark/bulkwark_lowhealth.png");
    private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/bulkwark.geo.json");

    public BulkWarkRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BulkwarkModel<BulkwarkEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"bulkwark"));
        this.addRenderLayer(new ShieldSoulsLayer(this,new BulkwarkModel<BulkwarkEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"bulkwark")));
        this.shadowRadius = 0.5f;
    }

}
