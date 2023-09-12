package net.BKTeam.illagerrevolutionmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.projectile.SoulBomb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BKGui implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, PoseStack mStack, float partialTick, int screenWidth, int screenHeight) {
        if (!gui.shouldDrawSurvivalElements()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        assert player != null;
        if(mc.options.getCameraType().isFirstPerson()){
            if(!player.level.getEntitiesOfClass(SoulBomb.class,player.getBoundingBox().inflate(3.0D), e->e.isDefender() && e.getOwnerID()==player.getId()).isEmpty()){
                mStack.pushPose();
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/gui/shield_bomb_overlay.png"));
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder.vertex(0.0D, (double)screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
                bufferbuilder.vertex((double)screenWidth, (double)screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
                bufferbuilder.vertex((double)screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
                bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
                tesselator.end();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                mStack.popPose();
            }
        }

    }
}
