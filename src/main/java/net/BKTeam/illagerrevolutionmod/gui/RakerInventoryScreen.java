package net.BKTeam.illagerrevolutionmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;

public class RakerInventoryScreen extends AbstractContainerScreen<RakerInventoryMenu>{

    private static final ResourceLocation RAKER_INVENTORY_LOCATION = new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/gui/containers/raker.png");
    private final RakerEntity raker;
    private float xMouse;
    private float yMouse;

    public RakerInventoryScreen(RakerInventoryMenu pMenu, Inventory pPlayerInventory, RakerEntity pRaker) {
        super(pMenu, pPlayerInventory, Component.nullToEmpty("Raker Inventory"));
        this.raker = pRaker;
        this.passEvents = false;
    }

    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, RAKER_INVENTORY_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.raker.isTame()) {
            this.blit(pPoseStack, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
            this.blit(pPoseStack, i + 7, j + 53, 18, this.imageHeight + 54, 18, 18);
        }
        InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.raker);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }
}
