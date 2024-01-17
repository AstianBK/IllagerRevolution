package net.BKTeam.illagerrevolutionmod.data.server.tags;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.IllagiumCrossbowItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class BKItemProperties {
    public static void register() {

        //Copied from vanilla to mimic normal crossbow
        ItemProperties.register(ModItems.ILLAGIUM_CROSSBOW.get(), new ResourceLocation(IllagerRevolutionMod.MOD_ID, "pull"), (p_239427_0_, p_239427_1_, p_239427_2_, intIn) -> {
            if (p_239427_2_ == null) {
                return 0.0F;
            } else {
                return IllagiumCrossbowItem.isCharged(p_239427_0_) ? 0.0F : ((float)p_239427_0_.getUseDuration() - (float)p_239427_2_.getUseItemRemainingTicks()) / (float)IllagiumCrossbowItem.getChargeDuration(p_239427_0_);
            }
        });
        ItemProperties.register(ModItems.ILLAGIUM_CROSSBOW.get(), new ResourceLocation(IllagerRevolutionMod.MOD_ID, "pulling"), (p_239426_0_, p_239426_1_, p_239426_2_, intIn) -> {
            return p_239426_2_ != null && p_239426_2_.isUsingItem() && p_239426_2_.getUseItem() == p_239426_0_ && !IllagiumCrossbowItem.isCharged(p_239426_0_) ? 1.0F : 0.0F;
        });
        ItemProperties.register(ModItems.ILLAGIUM_CROSSBOW.get(), new ResourceLocation(IllagerRevolutionMod.MOD_ID, "charged"), (p_239425_0_, p_239425_1_, p_239425_2_, intIn) -> {
            return p_239425_2_ != null && IllagiumCrossbowItem.isCharged(p_239425_0_) ?  1.0F : 0.0F;
        });
        ItemProperties.register(ModItems.ILLAGIUM_CROSSBOW.get(), new ResourceLocation(IllagerRevolutionMod.MOD_ID, "ammo"), (p_239425_0_, p_239425_1_, p_239425_2_, intIn) -> {
            return p_239425_2_ != null && IllagiumCrossbowItem.isCharged(p_239425_0_) ?  (float) IllagiumCrossbowItem.storeAmmo(p_239425_0_) : 0.0F;
        });
        ItemProperties.register(ModItems.ILLAGIUM_CROSSBOW.get(), new ResourceLocation(IllagerRevolutionMod.MOD_ID, "firework"), (p_239424_0_, p_239424_1_, p_239424_2_, intIn) -> {
            return p_239424_2_ != null && IllagiumCrossbowItem.isCharged(p_239424_0_) && IllagiumCrossbowItem.containsChargedProjectile(p_239424_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
        });
    }
}
