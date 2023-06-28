package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.Patreon;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.WildRavagerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.WarPaintLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WildRavagerArmorLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.WildRavagerBuumLayer;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WildRavagerRenderer extends MobRenderer<WildRavagerEntity, WildRavagerModel> {
    private static final Map<IllagerBeastEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(IllagerBeastEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT1, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager1.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT2, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager2.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT3, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager3.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT4, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager4.png"));
        p_114874_.put(IllagerBeastEntity.Variant.VARIANT5, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager5.png"));
    });
    private static final ResourceLocation TEXTURE_CUTE= new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/wild_ravager/wild_ravager5.png");

    public WildRavagerRenderer(EntityRendererProvider.Context p_174362_) {
        super(p_174362_, new WildRavagerModel(p_174362_.bakeLayer(ModEventBusEvents.RAVAGER)), 1.1F);
        this.addLayer(new WarPaintLayer<>(this));
        this.addLayer(new WildRavagerArmorLayer<>(this));
        this.addLayer(new WildRavagerBuumLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(WildRavagerEntity pEntity) {
        if(pEntity.getCustomName()!=null && Patreon.isPatreon(Minecraft.getInstance().player,IllagerRevolutionMod.CUTE_SKIN_UUID)){
            return pEntity.getCustomName().getString().equals("Cute") ? TEXTURE_CUTE   : LOCATION_BY_VARIANT.get(pEntity.getIdVariant());
        }
        return LOCATION_BY_VARIANT.get(pEntity.getIdVariant());
    }

    @Override
    public void render(WildRavagerEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
