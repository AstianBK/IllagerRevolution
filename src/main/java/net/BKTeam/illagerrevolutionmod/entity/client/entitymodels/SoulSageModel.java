package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.SoulSageEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SoulSageModel<I extends AbstractIllager> extends GeoModel<SoulSageEntity> {

    protected final ResourceLocation MODEL_RESLOC;
    protected final ResourceLocation TEXTURE_DEFAULT;
    protected final ResourceLocation TEXTURE_HURT;
    protected final String ENTITY_REGISTRY_PATH_NAME;

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/soul_sage.animation.json");

    public SoulSageModel(ResourceLocation model, ResourceLocation textureDefault, ResourceLocation textureHurt, String entityName) {
        super();
        this.MODEL_RESLOC = model;
        this.TEXTURE_HURT=textureHurt;
        this.TEXTURE_DEFAULT =textureDefault;
        this.ENTITY_REGISTRY_PATH_NAME = entityName;
    }

    public void setCustomAnimations(SoulSageEntity entity, int instanceId, AnimationState customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        if (head != null && !entity.isCastingSpell()) {
            head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(extraData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }

    @Override
    public ResourceLocation getModelResource(SoulSageEntity object) {
        return MODEL_RESLOC;
    }

    @Override
    public ResourceLocation getTextureResource(SoulSageEntity object) {
        return TEXTURE_DEFAULT;
    }

    @Override
    public ResourceLocation getAnimationResource(SoulSageEntity animatable) {
        return ANIMATION_RESLOC;
    }
}
