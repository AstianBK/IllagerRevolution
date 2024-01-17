package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.ZombifiedModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.ZombifiedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombifiedRenderer extends GeoEntityRenderer<ZombifiedEntity> {
    public ZombifiedRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,new ZombifiedModel<>());;
        this.shadowRadius = 0.5f;
    }
}
