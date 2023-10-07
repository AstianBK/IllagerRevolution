package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.custom.BulkwarkEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class PlayerLikedLayer <T extends LivingEntity,M extends EntityModel<T>> extends RenderLayer<T,M>{
    private final ResourceLocation LINKED_ARMOR=new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    public PlayerLikedLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);

    }
    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if(pLivingEntity instanceof Player player){
            List<FallenKnightEntity> knights = player.level.getEntitiesOfClass(FallenKnightEntity.class,player.getBoundingBox().inflate(20.0d), e->e.getOwner()==player);
            if(Util.getNumberOfLinked(knights)>0 && player.getMainHandItem().is(ModItems.ILLAGIUM_ALT_RUNED_BLADE.get())){
                float f = (float) pLivingEntity.tickCount + pPartialTicks;
                EntityModel<T> model = this.getParentModel();
                model.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
                this.getParentModel().copyPropertiesTo(model);
                VertexConsumer ivertex = pBuffer.getBuffer(RenderType.energySwirl(LINKED_ARMOR, f * 0.01f, f * 0.01f));
                model.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                model.renderToBuffer(pMatrixStack, ivertex, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        BulkwarkEntity bulkwark=pLivingEntity.level.getNearestEntity(BulkwarkEntity.class, TargetingConditions.DEFAULT,pLivingEntity,pLivingEntity.getX(),pLivingEntity.getY(),pLivingEntity.getZ(),pLivingEntity.getBoundingBox().inflate(30.0D));
        if(bulkwark!=null){
            if(bulkwark.isAbsorbMode() && bulkwark.isAlliedTo(pLivingEntity)){
                float f = (float) pLivingEntity.tickCount + pPartialTicks;
                EntityModel<T> model = this.getParentModel();
                model.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
                this.getParentModel().copyPropertiesTo(model);
                VertexConsumer ivertex = pBuffer.getBuffer(RenderType.energySwirl(LINKED_ARMOR, f * 0.01f, f * 0.01f));
                model.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                model.renderToBuffer(pMatrixStack, ivertex, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }
}