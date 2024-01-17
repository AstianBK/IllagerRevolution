package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerGModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.CuteLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintBeastGeckoLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WildRavagerArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WildRavagerDrumLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class WildRavagerRenderer extends GeoEntityRenderer<WildRavagerEntity> {
    public WildRavagerRenderer(EntityRendererProvider.Context p_174362_) {
        super(p_174362_, new WildRavagerGModel());
        this.addRenderLayer(new CuteLayer<>(this,new WildRavagerGModel()));
        this.addRenderLayer(new WarPaintBeastGeckoLayer<>(this,new WildRavagerGModel()));
        this.addRenderLayer(new WildRavagerArmorLayer(this,new WildRavagerGModel()));
        this.addRenderLayer(new WildRavagerDrumLayer(this));
    }

    @Override
    public void render(WildRavagerEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public RenderType getRenderType(WildRavagerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}
