package net.BKTeam.illagerrevolutionmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.ModConstants;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScroungerInventoryScreen extends AbstractContainerScreen<ScroungerInventoryMenu>{

    private final ScroungerEntity scrounger;
    private float xMouse;
    private float yMouse;

    public ScroungerInventoryScreen(ScroungerInventoryMenu pMenu, Inventory pPlayerInventory, ScroungerEntity scrounger) {
        super(pMenu, pPlayerInventory, Component.nullToEmpty("Scrounger Inventory"));
        this.scrounger = scrounger;
        this.passEvents = false;
    }

    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ModConstants.BEAST_INVENTORY);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.scrounger.isTame()) {
            this.blit(pPoseStack, i + 7, j + 35, 18, this.imageHeight + 36, 18, 18);
        }
        if(!this.scrounger.getContainer().getItem(0).isEmpty()){
            this.blit(pPoseStack, i + 79, j + 17, 0, this.imageHeight + 18,90, 18);
        }
        InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.scrounger);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }
}
