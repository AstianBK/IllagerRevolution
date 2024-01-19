package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.SoulSageEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;


@OnlyIn(Dist.CLIENT)
public class BKEyesLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {


    public BKEyesLayer(GeoRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if(animatable instanceof LivingEntity livingEntity){
            renderType=RenderType.eyes(getTextureCute(livingEntity));
            renderer.reRender(bakedModel,poseStack,
                    bufferSource,animatable,renderType,bufferSource.getBuffer(renderType),
                    partialTick,packedLight, OverlayTexture.NO_OVERLAY,1.0F,1.0F,1.0F,1.0F);

        }
    }


    public ResourceLocation getTextureCute(LivingEntity entity){
        String name="blade_knight";
        String part=entity instanceof SoulSageEntity ? "gem" : "eyes";
        if (entity.getEncodeId()!=null){
            name=entity.getEncodeId().split(":")[1];
        }
        if(entity instanceof FallenKnightEntity fallen){
            name=fallen.getIsFrozen()? "frozen_"+name : name;
        }
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/"+name+"/"+name+"_"+part+".png");
    }
}