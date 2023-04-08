package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorVindicatorJacketItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class VindicatorPlayerArmorRenderer extends GeoArmorRenderer<ArmorVindicatorJacketItem> {

    public VindicatorPlayerArmorRenderer() {
        super(new VindicatorPlayerArmorModel());

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
