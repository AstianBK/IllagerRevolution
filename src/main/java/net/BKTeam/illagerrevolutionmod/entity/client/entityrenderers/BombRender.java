package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.client.entitymodels.SoulBombModel;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.BKTeam.illagerrevolutionmod.event.ModEventBusEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BombRender<T extends SoulBomb> extends EntityRenderer<T> {
    private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));

    private final ModelPart ORB;

    public BombRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.ORB=p_174008_.bakeLayer(ModEventBusEvents.ORB).getChild("orb");
    }
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }


    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/entity/shield_bomb.png");
    }

}