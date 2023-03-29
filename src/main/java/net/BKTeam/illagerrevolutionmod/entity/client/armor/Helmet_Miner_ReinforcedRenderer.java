package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class Helmet_Miner_ReinforcedRenderer extends GeoArmorRenderer<IllagiumArmorItem> {

    public Helmet_Miner_ReinforcedRenderer() {
        super(new Helmet_Miner_ReinforcedModel());

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
