package net.BKTeam.illagerrevolutionmod.entity.client.entityrenderers;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.projectile.ArrowBeast;

public class ArrowBeastRender extends ArrowRenderer<ArrowBeast> {
    public ArrowBeastRender(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }
    @Override
    public ResourceLocation getTextureLocation(ArrowBeast pEntity) {
        return new ResourceLocation(IllagerRevolutionMod.MOD_ID,"textures/projetiles/arrow_beast_projectile.png");
    }
}
