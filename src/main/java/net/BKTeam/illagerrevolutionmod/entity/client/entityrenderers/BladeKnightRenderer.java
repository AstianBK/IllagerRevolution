package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.BladeKnightModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import static net.BKTeam.illagerrevolutionmod.ModConstants.POTION_BONE_IDENT;
import static net.BKTeam.illagerrevolutionmod.ModConstants.RIGHT_HAND_BONE_IDENT;

public class BladeKnightRenderer extends GeoEntityRenderer<BladeKnightEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/blade_knight/blade_knight.png");
    private static final ResourceLocation TEXTURE_LOWLIFE = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "textures/entity/blade_knight/blade_knight_lowhealth.png");
    private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "geo/blade_knight.geo.json");

    public BladeKnightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BladeKnightModel<BladeKnightEntity>(MODEL_RESLOC,TEXTURE,TEXTURE_LOWLIFE,"blade_knight"));
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, BladeKnightEntity animatable) {
                switch (bone.getName()) {
                    case RIGHT_HAND_BONE_IDENT:
                        return animatable.getMainHandItem();
                    case POTION_BONE_IDENT:
                        break;
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, BladeKnightEntity animatable) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack stack, GeoBone bone, ItemStack item, BladeKnightEntity currentEntity, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                CompoundTag nbt;
                int cc1=6;
                nbt=item.getOrCreateTag();
                nbt.putInt("CustomModelData", cc1);
                super.renderStackForBone(stack, bone, item, currentEntity, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
        this.shadowRadius = 0.5f;
    }

}
