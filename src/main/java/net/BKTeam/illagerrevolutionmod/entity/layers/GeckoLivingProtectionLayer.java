package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulBombModel;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GeckoLivingProtectionLayer<T extends LivingEntity & IAnimatable> extends GeoLayerRenderer<T> {

    private final ResourceLocation LINKED_ARMOR=new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private SoulBombModel model = null;

    public GeckoLivingProtectionLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (model == null) {
            model = new SoulBombModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEventBusEvents.ORB));
        }
        List<SoulBomb> souls = entitylivingbaseIn.level.getEntitiesOfClass(SoulBomb.class,entitylivingbaseIn.getBoundingBox().inflate(3.0d), e->e.isDefender() && e.getOwnerID()==entitylivingbaseIn.getId());
        if(getDefender(souls)) {
            matrixStackIn.pushPose();
            float f=souls.get(0).discardTimer>0 ? 0.1f : 1.0f;
            float f1=souls.get(0).discardTimer>0 ? 0.8f : 0.0f;
            float f2=souls.get(0).discardTimer>0 ? 0.4f : 0.0f;

            matrixStackIn.scale(entitylivingbaseIn.getBbHeight(),entitylivingbaseIn.getBbHeight(),entitylivingbaseIn.getBbHeight());
            float f3 = (float) entitylivingbaseIn.tickCount +partialTicks;
            this.model.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            VertexConsumer ivertex = bufferIn.getBuffer(RenderType.energySwirl(LINKED_ARMOR, f3 * 0.01f, f3 * 0.01f));
            this.model.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(matrixStackIn, ivertex, packedLightIn, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0f);
            matrixStackIn.popPose();
        }
    }

    public static boolean getDefender(List<SoulBomb> soulBombs){
        return !soulBombs.isEmpty();
    }
}

