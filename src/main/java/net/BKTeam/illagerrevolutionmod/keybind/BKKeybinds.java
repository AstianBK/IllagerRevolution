package net.BKTeam.illagerrevolutionmod.keybind;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.event.KeyEvent;

@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BKKeybinds {
    public static KeyMapping attackKey1;
    public static KeyMapping attackKey2;
    public static KeyMapping attackKey3;

    @SubscribeEvent
    public static void register(final RegisterKeyMappingsEvent event) {
        attackKey1 = create("attack_key1", KeyEvent.VK_C);
        attackKey2 = create("attack_key2", KeyEvent.VK_G);
        attackKey3 = create("attack_key3", KeyEvent.VK_V);

        event.register(attackKey1);
        event.register(attackKey2);
        event.register(attackKey3);
    }

    private static KeyMapping create(String name, int key) {
        return new KeyMapping("key." + IllagerRevolutionMod.MOD_ID + "." + name, key, "key.category." + IllagerRevolutionMod.MOD_ID);
    }
}
