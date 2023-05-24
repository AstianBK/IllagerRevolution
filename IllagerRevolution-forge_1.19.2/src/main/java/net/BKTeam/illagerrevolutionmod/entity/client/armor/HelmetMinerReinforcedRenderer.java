package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class HelmetMinerReinforcedRenderer extends GeoArmorRenderer<IllagiumArmorItem> {

    public HelmetMinerReinforcedRenderer() {
        super(new HelmetMinerReinforcedModel());

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
