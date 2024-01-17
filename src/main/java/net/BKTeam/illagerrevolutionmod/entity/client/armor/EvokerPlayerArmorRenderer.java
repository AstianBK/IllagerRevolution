package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorEvokerRobeItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EvokerPlayerArmorRenderer extends GeoArmorRenderer<ArmorEvokerRobeItem> {

        public EvokerPlayerArmorRenderer() {
            super(new EvokerPlayerArmorModel());
        }
}

