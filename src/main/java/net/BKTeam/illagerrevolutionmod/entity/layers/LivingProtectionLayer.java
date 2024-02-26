package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.api.IAbilityKnightCapability;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulBombModel;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.BKTeam.illagerrevolutionmod.procedures.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@OnlyIn(Dist.CLIENT)
public class LivingProtectionLayer<T extends LivingEntity,M extends EntityModel<T>> extends RenderLayer<T,M>{
    private final ResourceLocation LINKED_ARMOR = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/shield_bomb.png");
    private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));


    private final SoulBombModel model;
    public LivingProtectionLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
        this.model=new SoulBombModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.ORB));
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if(this.model!=null){
            List<SoulBomb> souls = pLivingEntity.level().getEntitiesOfClass(SoulBomb.class,pLivingEntity.getBoundingBox().inflate(3.0d),e->e.isDefender() && e.getOwnerID()==pLivingEntity.getId());
            if(getDefender(souls)){
                pMatrixStack.pushPose();
                float f = getY(pLivingEntity, pPartialTicks);
                float f1 = ((float)pLivingEntity.tickCount + pPartialTicks) * 3.0F;
                float f3 = (float) pLivingEntity.tickCount + pPartialTicks;
                float f4;
                float f5;
                float f6;
                int i = OverlayTexture.NO_OVERLAY;
                if(pLivingEntity.hurtDuration>0){
                    f4=1.0F;
                    f5=0.0F;
                    f6=0.0F;
                }else {
                    f4=1.0F;
                    f5=1.0F;
                    f6=1.0F;
                }

                pMatrixStack.translate(0.0D,-0.4D,0.0D);
                pMatrixStack.translate(0.0D, (double)(1.5F + f / 2.0F), 0.0D);
                pMatrixStack.scale(pLivingEntity.getBbHeight(),pLivingEntity.getBbHeight(),pLivingEntity.getBbHeight());
                pMatrixStack.mulPose(Axis.YP.rotationDegrees(f1));
                pMatrixStack.mulPose(new Quaternionf().setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
                this.model.renderToBuffer(pMatrixStack,pBuffer.getBuffer(RenderType.energySwirl(LINKED_ARMOR,f3*0.01f,f3*0.01f)),pPackedLight,i,f4,f5,f6,1.0F);
                pMatrixStack.popPose();
            }
        }
    }

    public static boolean getDefender(List<SoulBomb> soulBombs){
        return soulBombs!=null && !soulBombs.isEmpty();
    }

    public static float getY(LivingEntity p_114159_, float p_114160_) {
        float f = (float)p_114159_.tickCount + p_114160_;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

}