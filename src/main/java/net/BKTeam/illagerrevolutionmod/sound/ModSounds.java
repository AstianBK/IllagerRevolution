package net.BKTeam.illagerrevolutionmod.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;

public class ModSounds {
        public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
                DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, IllagerRevolutionMod.MOD_ID);
        
        public static final RegistryObject<SoundEvent> RUNE_TABLE_USE =
                registerSoundEvent("rune_table_use");

        public static final RegistryObject<SoundEvent> TAMER_WHISTLE =
                registerSoundEvent("tamer_whistle");

        public static final RegistryObject<SoundEvent> RAKER_HISS =
            registerSoundEvent("raker_hiss");

        public static final RegistryObject<SoundEvent> RAKER_MEOW =
            registerSoundEvent("raker_meow");

        public static final RegistryObject<SoundEvent> SOUL_ABSORB =
            registerSoundEvent("soul_absorb");

        public static final RegistryObject<SoundEvent> SOUL_LIMIT =
            registerSoundEvent("soul_limit");

        public static final RegistryObject<SoundEvent> SOUL_RELEASE =
            registerSoundEvent("soul_release");

        public static final RegistryObject<SoundEvent> BLADE_KNIGHT_HURT =
            registerSoundEvent("blade_knight_hurt");

        public static final RegistryObject<SoundEvent> BLADE_KNIGHT_SWORDHIT1 =
            registerSoundEvent("blade_knight_swordhit1");

        public static final RegistryObject<SoundEvent> BLADE_KNIGHT_SWORDHIT2 =
            registerSoundEvent("blade_knight_swordhit2");

        public static final RegistryObject<SoundEvent> BLADE_KNIGHT_LAUGH =
            registerSoundEvent("blade_knight_laugh");

        public static final RegistryObject<SoundEvent> FALLEN_KNIGHT_REVIVE =
                registerSoundEvent("fallen_knight_revive");

        public static final RegistryObject<SoundEvent> DEATH_MARK_SOUND =
            registerSoundEvent("death_mark_sound");

        public static final RegistryObject<SoundEvent> BLEEDING_PROC =
            registerSoundEvent("bleeding_proc");


        public static RegistryObject<SoundEvent> registerSoundEvent(String name){
            return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(IllagerRevolutionMod.MOD_ID, name)));
        }
        public static void register(IEventBus eventBus){
            SOUND_EVENTS.register(eventBus);
        }
}
