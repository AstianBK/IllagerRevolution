package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.ScroungerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.CuteLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.ScroungerArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScroungerRenderer extends GeoEntityRenderer<ScroungerEntity> {
    public ScroungerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new ScroungerModel());
        this.addRenderLayer(new CuteLayer<>(this,new ScroungerModel()));
        this.addRenderLayer(new ScroungerArmorLayer(this,new ScroungerModel()));
        this.addRenderLayer(new WarPaintBeastGeckoLayer<>(this,new ScroungerModel()));
        this.shadowRadius = 0.5f;
    }
}
