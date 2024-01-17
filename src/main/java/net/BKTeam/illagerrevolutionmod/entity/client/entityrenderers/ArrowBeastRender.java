package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.projectile.ArrowBeast;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ArrowBeastRender extends ArrowRenderer<ArrowBeast> {

    private final ResourceLocation TEXTURE=new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/projetiles/arrow_beast_projectile.png");

    public ArrowBeastRender(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }

    public ResourceLocation getTextureLocation(@NotNull ArrowBeast pEntity) {
        return TEXTURE;
    }
}
