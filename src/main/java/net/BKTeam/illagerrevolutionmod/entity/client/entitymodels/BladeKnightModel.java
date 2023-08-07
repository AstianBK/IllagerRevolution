package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.BladeKnightEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class BladeKnightModel<I extends AbstractIllager> extends AnimatedGeoModel<BladeKnightEntity> {

    protected final ResourceLocation MODEL_RESLOC;
    protected final ResourceLocation TEXTURE_DEFAULT;
    protected final ResourceLocation TEXTURE_HURT;
    protected final String ENTITY_REGISTRY_PATH_NAME;

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/blade_knight.animation.json");

    public BladeKnightModel(ResourceLocation model, ResourceLocation textureDefault, ResourceLocation textureHurt, String entityName) {
        super();
        this.MODEL_RESLOC = model;
        this.TEXTURE_HURT=textureHurt;
        this.TEXTURE_DEFAULT =textureDefault;
        this.ENTITY_REGISTRY_PATH_NAME = entityName;
    }

    @Override
    public void setCustomAnimations(BladeKnightEntity entity, int instanceId, AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null && !entity.isCastingSpell() && !entity.isAttackingShield()) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }

    @Override
    public ResourceLocation getModelResource(BladeKnightEntity object) {
        return MODEL_RESLOC;
    }

    @Override
    public ResourceLocation getTextureResource(BladeKnightEntity object) {
        if(!object.isPhase2()){
            if(object.hasCustomName()){
                return object.getCustomName().getString().equals("Edge Knight") ? new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                        "textures/entity/blade_knight/edge_knight.png")  : TEXTURE_DEFAULT ;
            }
            return TEXTURE_DEFAULT;
        }
        if(object.hasCustomName()){
            return object.getCustomName().getString().equals("Edge Knight") ? new ResourceLocation(IllagerRevolutionMod.MOD_ID,
                    "textures/entity/blade_knight/edge_knight_lowhealth.png")  : TEXTURE_HURT ;
        }
        return TEXTURE_HURT;
    }

    @Override
    public ResourceLocation getAnimationResource(BladeKnightEntity animatable) {
        return ANIMATION_RESLOC;
    }
}
