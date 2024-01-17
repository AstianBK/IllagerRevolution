package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.IllagerMinerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerMinerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import static net.BKTeam.illagerrevolutionmod.ModConstants.POTION_BONE_IDENT;
import static net.BKTeam.illagerrevolutionmod.ModConstants.RIGHT_HAND_BONE_IDENT;

public class IllagerMinerRenderer extends GeoEntityRenderer<IllagerMinerEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/illagerminer/miner.png");
    private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/illagerminer.geo.json");

    public IllagerMinerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager,
                new IllagerMinerModel(MODEL_RESLOC, TEXTURE, "illagerminer"));
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, IllagerMinerEntity animatable) {
                switch (bone.getName()) {
                    case RIGHT_HAND_BONE_IDENT:
                        return animatable.getMainHandItem();
                    case POTION_BONE_IDENT:
                        break;
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, IllagerMinerEntity animatable) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack stack, GeoBone bone, ItemStack item, IllagerMinerEntity currentEntity, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (item == currentEntity.getMainHandItem() || item == currentEntity.getOffhandItem()) {
                    stack.mulPose(Axis.XP.rotationDegrees(270F));
                    boolean shieldFlag = item.getItem() instanceof ShieldItem;

                    if (item == currentEntity.getMainHandItem()) {
                        if (shieldFlag) {
                            stack.translate(0, 0.125, -15);
                        }else {

                        }
                    } else {
                        if (shieldFlag) {
                            stack.translate(0, 0.125, 0.25);
                            stack.mulPose(Axis.YP.rotationDegrees(180));
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
}
