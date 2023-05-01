package net.BKTeam.illagerrevolutionmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.effect.init_effect;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Random;


@OnlyIn(Dist.CLIENT)
public class Hearts_Effect implements IGuiOverlay {
    long healthBlinkTime = 0;
    long lastHealthTime = 0;

    @Override
    public void render(ForgeGui gui, PoseStack mStack, float partialTick, int screenWidth, int screenHeight) {
        if (!gui.shouldDrawSurvivalElements()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if(player.hasEffect(init_effect.DEEP_WOUND.get()) || player.hasEffect(init_effect.DEATH_MARK.get())){
            mStack.pushPose();
            mStack.translate(0, 0, 0.01);

            int health = Mth.ceil(player.getHealth());
            float absorb = Mth.ceil(player.getAbsorptionAmount());
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float)attrMaxHealth.getValue();


            int ticks = gui.getGuiTicks();
            boolean highlight = this.healthBlinkTime > (long)ticks && (this.healthBlinkTime - (long)ticks) / 3L % 2L == 1L;

            if (Util.getMillis() - this.lastHealthTime > 1000L) {
                this.lastHealthTime = Util.getMillis();
            }

            float f = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(health, health));
            int regen = -1;
            if (player.hasEffect(MobEffects.REGENERATION)){
                regen = ticks % Mth.ceil(f + 5.0F);
            }

            Random rand = new Random();
            rand.setSeed((long)(ticks * 312871));

            int absorptionHearts = Mth.ceil(absorb / 2.0f) - 1;
            int hearts = Mth.ceil(healthMax / 2.0f) - 1;
            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int totalHealthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);
            int extraHealthRows = totalHealthRows - healthRows;
            int extraRowHeight = Mth.clamp(10 - (healthRows - 2), 3, 10);

            int left = screenWidth / 2 - 91;
            int top = screenHeight - ((ForgeGui)Minecraft.getInstance().gui).leftHeight + healthRows * rowHeight;
            if (rowHeight != 10){
                top += 10 - rowHeight;
            }

            gui.leftHeight += extraHealthRows * extraRowHeight;

            String s1=player.hasEffect(init_effect.DEEP_WOUND.get()) ? "" : "2";
            RenderSystem.setShaderTexture(0, new ResourceLocation(IllagerRevolutionMod.MOD_ID, "textures/gui/icons"+s1+".png"));

            for (int i = absorptionHearts + hearts; i > absorptionHearts + hearts; -- i) {
                int row = (i + 1) / 10;
                int heart = (i + 1) % 10;
                int x = left + heart * 8;
                int y = top - extraRowHeight * Math.max(0, row - healthRows + 1) - rowHeight * Math.min(row, healthRows - 1);
                mc.gui.blit(mStack, x, y, highlight ? 9 : 0, 18, 9, 9);
            }
            for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; -- i) {
                int row = i / 10;
                int heart = i % 10;
                int x = left + heart * 8;
                int y = top - row * rowHeight;

                if (health <= 4) y += rand.nextInt(2);
                if (i == regen) y -= 2;
                RenderSystem.enableBlend();
                if (i * 2 + 1 < health){
                    mc.gui.blit(mStack, x, y, 0, 0, 9, 9);
                } else if (i * 2 + 1 == health){
                    mc.gui.blit(mStack, x, y, 9, 0, 9, 9);
                }
                RenderSystem.disableBlend();
            }
            mStack.popPose();
        }
    }
}