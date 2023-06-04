package net.BKTeam.illagerrevolutionmod.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IllagerRevolutionMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> BKSOULS_PARTICLES =
            PARTICLE_TYPES.register("bksouls", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SOUL_PROJECTILE_PARTICLES =
            PARTICLE_TYPES.register("souls_projectile", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SMOKE_BK_PARTICLES =
            PARTICLE_TYPES.register("smokebk", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_PARTICLES =
            PARTICLE_TYPES.register("bloodbk", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> RUNE_SOUL_PARTICLES =
            PARTICLE_TYPES.register("rune_soul", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> RUNE_CURSED_PARTICLES =
            PARTICLE_TYPES.register("rune_cursed", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
