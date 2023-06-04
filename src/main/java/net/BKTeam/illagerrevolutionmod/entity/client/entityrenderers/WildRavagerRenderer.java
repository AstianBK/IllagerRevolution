package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WildRavagerArmorLayer;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WildRavagerRenderer extends MobRenderer<WildRavagerEntity, WildRavagerModel> {
    private final ResourceLocation TEXTURE_REGULAR = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/wild_ravager/wild_ravager.png");
    public WildRavagerRenderer(EntityRendererProvider.Context p_174362_) {
        super(p_174362_, new WildRavagerModel(p_174362_.bakeLayer(ModEventBusEvents.RAVAGER)), 1.1F);
        this.addLayer(new WarPaintLayer<>(this));
        this.addLayer(new WildRavagerArmorLayer<>(this,new WildRavagerModel(p_174362_.bakeLayer(ModEventBusEvents.RAVAGER))));
    }

    @Override
    public ResourceLocation getTextureLocation(WildRavagerEntity pEntity) {
        return TEXTURE_REGULAR;
    }
}
