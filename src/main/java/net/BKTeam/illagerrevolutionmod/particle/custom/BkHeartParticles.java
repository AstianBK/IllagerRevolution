package net.BKTeam.illagerrevolutionmod.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class BkHeartParticles extends SmokeParticle {
    protected BkHeartParticles(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, float pQuadSizeMultiplier, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pQuadSizeMultiplier, pSprites);
        this.setColor(1.0f,0.0f,0.0f);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BkHeartParticles(world,x,y,z,xSpeed,ySpeed,zSpeed,1.0F,this.spriteSet);
        }
    }
}
