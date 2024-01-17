package net.BKTeam.illagerrevolutionmod.item.client;


import net.BKTeam.illagerrevolutionmod.item.custom.AnimatedItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AnimatedItemRenderer extends GeoItemRenderer<AnimatedItem> {
    public AnimatedItemRenderer() {
        super(new AnimatedItemModel());
    }
}
