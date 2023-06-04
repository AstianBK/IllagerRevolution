package net.BKTeam.illagerrevolutionmod.event.loot;

import com.mojang.serialization.Codec;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, IllagerRevolutionMod.MOD_ID);

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM =
            SERIALIZERS.register("add_item", AddItem.CODEC_SUPPLIER);
}
