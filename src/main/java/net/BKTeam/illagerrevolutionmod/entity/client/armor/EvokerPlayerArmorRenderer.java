package net.BKTeam.illagerrevolutionmod.entity.client.armor;

import net.BKTeam.illagerrevolutionmod.item.custom.ArmorEvokerRobeItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class EvokerPlayerArmorRenderer extends GeoArmorRenderer<ArmorEvokerRobeItem> {

        public EvokerPlayerArmorRenderer() {
            super(new EvokerPlayerArmorModel());

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

