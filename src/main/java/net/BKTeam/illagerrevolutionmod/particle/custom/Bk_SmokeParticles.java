package net.BKTeam.illagerrevolutionmod.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class Bk_SmokeParticles extends LargeSmokeParticle {
    protected Bk_SmokeParticles(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ, SpriteSet spriteSet) {
        super(level,x,y,z,sX,sY,sZ,spriteSet);
        this.setColor(0.34f,0.14f,0.38f);
    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new Bk_SmokeParticles(world,x,y,z,xSpeed,ySpeed,zSpeed,this.spriteSet);
        }
    }
}
