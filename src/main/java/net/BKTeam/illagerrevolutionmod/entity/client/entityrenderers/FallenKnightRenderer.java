package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.FallenKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.BKEyesLayer;
import net.BKTeam.illagerrevolutionmod.entity.layers.LinkedLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import static net.BKTeam.illagerrevolutionmod.ModConstants.POTION_BONE_IDENT;
import static net.BKTeam.illagerrevolutionmod.ModConstants.RIGHT_HAND_BONE_IDENT;

public class FallenKnightRenderer extends GeoEntityRenderer<FallenKnightEntity> {

    public FallenKnightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FallenKnightModel<>());
        this.addRenderLayer(new LinkedLayer(this,new FallenKnightModel<>()));
        this.addRenderLayer(new BKEyesLayer<>(this));
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, FallenKnightEntity animatable) {
                switch (bone.getName()) {
                    case RIGHT_HAND_BONE_IDENT:
                        return animatable.getMainHandItem();
                    case POTION_BONE_IDENT:
                        break;
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, FallenKnightEntity animatable) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack stack, GeoBone bone, ItemStack item, FallenKnightEntity currentEntity, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (item == currentEntity.getMainHandItem() || item == currentEntity.getOffhandItem()) {

                    stack.mulPose(Axis.XP.rotationDegrees(270F));
                    boolean shieldFlag = item.getItem() instanceof CrossbowItem;

                    if (item == currentEntity.getMainHandItem()) {
                        if (shieldFlag) {
                            stack.translate(0.0F, 0.125F, 0.0F);
                        }else {

                        }
                    } else {
                        if (shieldFlag) {
                            stack.translate(0, 0.0, -15);
                        }else {

                        }

                    }
                    // stack.mulPose(Vector3f.YP.rotationDegrees(180));

                    // stack.scale(0.75F, 0.75F, 0.75F);
                }
                super.renderStackForBone(stack, bone, item, currentEntity, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(FallenKnightEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
