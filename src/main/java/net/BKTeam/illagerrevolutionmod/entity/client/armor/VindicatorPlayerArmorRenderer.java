package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorVindicatorJacketItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class VindicatorPlayerArmorRenderer extends GeoArmorRenderer<ArmorVindicatorJacketItem> {

    public VindicatorPlayerArmorRenderer() {
        super(new VindicatorPlayerArmorModel());

    }
}
