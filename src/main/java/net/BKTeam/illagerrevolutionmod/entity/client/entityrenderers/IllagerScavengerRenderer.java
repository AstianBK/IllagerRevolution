package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.entity.layers.ScavengerJunkArmorLayer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.IllagerScavengerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import java.util.Map;

import static net.BKTeam.illagerrevolutionmod.ModConstants.*;

public class IllagerScavengerRenderer extends ExtendedGeoEntityRenderer<IllagerScavengerEntity> {

    private static final Map<IllagerScavengerEntity.Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(IllagerScavengerEntity.Variant.class), (p_114874_) -> {
        p_114874_.put(IllagerScavengerEntity.Variant.BROWN, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer.png"));
        p_114874_.put(IllagerScavengerEntity.Variant.DARKPURPLE, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer2.png"));
        p_114874_.put(IllagerScavengerEntity.Variant.DARKGREEN, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer3.png"));
        p_114874_.put(IllagerScavengerEntity.Variant.DARKBLUE, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer4.png"));
        p_114874_.put(IllagerScavengerEntity.Variant.DARKGRAY, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer5.png"));
        p_114874_.put(IllagerScavengerEntity.Variant.GRAY, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/entity/illagerminerbadlands/badlandsminer6.png"));
    });

    public IllagerScavengerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IllagerScavengerModel());
        this.shadowRadius = 0.5f;
        this.addLayer(new ScavengerJunkArmorLayer(this,new IllagerScavengerModel()));
    }

    @Override
    public ResourceLocation getTextureLocation(IllagerScavengerEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getIdVariant());
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, IllagerScavengerEntity currentEntity) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, IllagerScavengerEntity currentEntity) {
        switch (boneName) {
            case RIGHT_HAND_BONE_IDENT:
                return currentEntity.getMainHandItem();
            case POTION_BONE_IDENT:
                break;
        }
        return null;
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, IllagerScavengerEntity currentEntity) {
        return null;

    }

    @Override
    protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, IllagerScavengerEntity currentEntity, IBone bone) {
        if (item == currentEntity.getMainHandItem() || item == currentEntity.getOffhandItem()) {

            stack.mulPose(Vector3f.XP.rotationDegrees(270F));
            boolean shieldFlag = item.getItem() instanceof ShieldItem;

            if (item == currentEntity.getMainHandItem()) {
                if (shieldFlag) {
                    stack.translate(0, 0.125, -15);
                }else {

                }
            } else {
                if (shieldFlag) {
                    stack.translate(0, 0.125, 0.25);
                    stack.mulPose(Vector3f.YP.rotationDegrees(180));
                }
            }
            // stack.mulPose(Vector3f.YP.rotationDegrees(180));

            // stack.scale(0.75F, 0.75F, 0.75F);
        }
    }

    @Override
    protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, IllagerScavengerEntity currentEntity) {

    }

    @Override
    protected void postRenderItem(PoseStack stack, ItemStack item, String boneName, IllagerScavengerEntity currentEntity, IBone bone) {

    }

    @Override
    protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, IllagerScavengerEntity currentEntity) {

    }

    @Override
    public RenderType getRenderType(IllagerScavengerEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1, 1, 1);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
