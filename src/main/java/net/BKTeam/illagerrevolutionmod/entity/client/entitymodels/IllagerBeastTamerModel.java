package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastTamerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class IllagerBeastTamerModel<I extends AbstractIllager> extends GeoModel<IllagerBeastTamerEntity> {

    protected final ResourceLocation MODEL_RESLOC;
    protected final ResourceLocation TEXTURE_DEFAULT;
    protected final String ENTITY_REGISTRY_PATH_NAME;

    protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(IllagerRevolutionMod.MOD_ID,
            "animations/beasttamerillager.animation.json");

    public IllagerBeastTamerModel(ResourceLocation model, ResourceLocation textureDefault,
                                  String entityName) {
        super();
        this.MODEL_RESLOC = model;
        this.TEXTURE_DEFAULT = textureDefault;
        this.ENTITY_REGISTRY_PATH_NAME = entityName;
    }

    @Override
    public ResourceLocation getModelResource(IllagerBeastTamerEntity object) {
        return MODEL_RESLOC;
    }

    @Override
    public ResourceLocation getTextureResource(IllagerBeastTamerEntity  object) {
        return TEXTURE_DEFAULT;
    }

    @Override
    public ResourceLocation getAnimationResource(IllagerBeastTamerEntity  animatable) {
        return ANIMATION_RESLOC;
    }
    public void setCustomAnimations(IllagerBeastTamerEntity entity, int instanceId, AnimationState customPredicate) {
        super.setCustomAnimations(entity, instanceId, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        if (head != null) {
            head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(extraData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
