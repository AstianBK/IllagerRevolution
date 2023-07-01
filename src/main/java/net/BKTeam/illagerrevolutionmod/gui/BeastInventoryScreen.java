package net.BKTeam.illagerrevolutionmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.BKTeam.illagerrevolutionmod.ModConstants;
import net.BKTeam.illagerrevolutionmod.entity.custom.IllagerBeastEntity;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BeastInventoryScreen extends AbstractContainerScreen<BeastInventoryMenu>{
    private final IllagerBeastEntity beast;
    private float xMouse;
    private float yMouse;

    public BeastInventoryScreen(BeastInventoryMenu pMenu, Inventory pPlayerInventory, IllagerBeastEntity pBeast) {
        super(pMenu, pPlayerInventory, Component.nullToEmpty(pBeast.getTypeBeast().getBeastName()+" Inventory"));
        this.beast = pBeast;
        this.passEvents = false;
    }

    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ModConstants.BEAST_INVENTORY);
        boolean flag =this.beast.getTypeBeast() == Beast.SCROUNGER;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = this.beast.getTypeBeast().getRow();
        int i1 = flag ? 79 : 7;
        int j1 = flag ? 17 : 35;
        this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.beast.canViewInventory()) {
            this.blit(pPoseStack, i + i1, j + j1, 0, this.imageHeight + k, 18, 18);
            if(flag){
                this.blit(pPoseStack, i + i1 + 18, j + j1, 18, this.imageHeight + k, 18, 18);
            }else {
                this.blit(pPoseStack, i + i1, j + j1 + 18, 18, this.imageHeight + k, 18, 18);
            }
        }
        InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.beast);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }
}
