package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulBombModel;
import net.BKTeam.illagerrevolutionmod.entity.custom.FallenKnightEntity;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class LivingProtectionLayer<T extends LivingEntity,M extends EntityModel<T>> extends RenderLayer<T,M>{
    private final ResourceLocation LINKED_ARMOR=new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private final SoulBombModel model;
    public LivingProtectionLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
        this.model=new SoulBombModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.ORB));
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if(this.model!=null){
            List<SoulBomb> souls = pLivingEntity.level.getEntitiesOfClass(SoulBomb.class,pLivingEntity.getBoundingBox().inflate(3.0d),e->e.isDefender() && e.getOwnerID()==pLivingEntity.getId());
            if(getDefender(souls)){
                pMatrixStack.pushPose();
                float f=souls.get(0).discardTimer>0 ? 0.1f : 1.0f;
                float f1=souls.get(0).discardTimer>0 ? 0.8f : 0.0f;
                float f2=souls.get(0).discardTimer>0 ? 0.4f : 0.0f;

                pMatrixStack.scale(pLivingEntity.getBbHeight(),pLivingEntity.getBbHeight(),pLivingEntity.getBbHeight());
                float f3 = (float) pLivingEntity.tickCount + pPartialTicks;
                this.model.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
                VertexConsumer ivertex = pBuffer.getBuffer(RenderType.energySwirl(LINKED_ARMOR, f3 * 0.01f, f3 * 0.01f));
                this.model.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                this.model.renderToBuffer(pMatrixStack, ivertex, pPackedLight, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0f);
                pMatrixStack.popPose();
            }
        }
    }

    public static boolean getDefender(List<SoulBomb> soulBombs){
        return !soulBombs.isEmpty();
    }
}