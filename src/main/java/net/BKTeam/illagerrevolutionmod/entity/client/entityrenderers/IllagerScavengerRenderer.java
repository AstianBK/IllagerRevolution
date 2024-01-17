package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.IllagerScavengerModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerScavengerEntity;
import net.BKTeam.illagerrevolutionmod.entity.layers.ScavengerJunkArmorLayer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.Map;

import static net.BKTeam.illagerrevolutionmod.ModConstants.POTION_BONE_IDENT;
import static net.BKTeam.illagerrevolutionmod.ModConstants.RIGHT_HAND_BONE_IDENT;

public class IllagerScavengerRenderer extends GeoEntityRenderer<IllagerScavengerEntity> {

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
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, IllagerScavengerEntity animatable) {
                switch (bone.getName()) {
                    case RIGHT_HAND_BONE_IDENT:
                        return animatable.getMainHandItem();
                    case POTION_BONE_IDENT:
                        break;
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, IllagerScavengerEntity animatable) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack stack, GeoBone bone, ItemStack item, IllagerScavengerEntity currentEntity, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (item == currentEntity.getMainHandItem() || item == currentEntity.getOffhandItem()) {
                    CompoundTag nbt;
                    nbt=item.getOrCreateTag();
                    nbt.putInt("CustomModelData",currentEntity.getArmorTierValue());
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
                }
                super.renderStackForBone(stack, bone, item, currentEntity, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
        this.addRenderLayer(new ScavengerJunkArmorLayer(this,new IllagerScavengerModel()));
    }

    @Override
    public ResourceLocation getTextureLocation(IllagerScavengerEntity instance) {
        if(instance.hasCustomName()){
            return instance.getCustomName().getString().equals("Swatvenger") ? new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/illagerminerbadlands/swat_scavenger.png")  : LOCATION_BY_VARIANT.get(instance.getIdVariant()) ;
        }
        return LOCATION_BY_VARIANT.get(instance.getIdVariant());
    }
}
