package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorIllusionerRobeItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class IllusionerPlayerArmorRenderer extends GeoArmorRenderer<ArmorIllusionerRobeItem> {

    public IllusionerPlayerArmorRenderer() {
        super(new IllusionerPlayerArmorModel());

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
