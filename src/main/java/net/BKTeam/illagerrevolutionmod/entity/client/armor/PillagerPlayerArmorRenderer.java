package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorPillagerVestItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class PillagerPlayerArmorRenderer extends GeoArmorRenderer<ArmorPillagerVestItem> {

    public PillagerPlayerArmorRenderer() {
        super(new PillagerPlayerArmorModel());

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
