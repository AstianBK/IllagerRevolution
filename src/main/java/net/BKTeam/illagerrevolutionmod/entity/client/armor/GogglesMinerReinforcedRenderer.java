package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorGogglesItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class GogglesMinerReinforcedRenderer extends GeoArmorRenderer<ArmorGogglesItem> {

    public GogglesMinerReinforcedRenderer() {
        super(new GogglesMinerReinforcedModel());

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorLeftLeg";
        this.leftLegBone = "armorRightLeg";
        this.rightBootBone = "armorLeftBoot";
        this.leftBootBone = "armorRightBoot";
    }
}
