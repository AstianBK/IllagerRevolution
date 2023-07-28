package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerGModel;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.*;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WildRavagerRenderer extends GeoEntityRenderer<WildRavagerEntity> {
    public WildRavagerRenderer(EntityRendererProvider.Context p_174362_) {
        super(p_174362_, new WildRavagerGModel());
        this.addLayer(new CuteLayer<>(this,new WildRavagerGModel()));
        this.addLayer(new WarPaintBeastGeckoLayer<>(this,new WildRavagerGModel()));
        this.addLayer(new WildRavagerArmorLayer(this,new WildRavagerGModel()));
        this.addLayer(new WildRavagerDrumLayer(this));
    }

    @Override
    public void render(WildRavagerEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public RenderType getRenderType(WildRavagerEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}
