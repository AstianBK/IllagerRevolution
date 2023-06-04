package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.keybind.BKKeybinds;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSyncAttackMauler;
import net.BKTeam.illagerrevolutionmod.network.PacketSyncAttackMauler2;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InputEvents {
    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        onInput(mc, event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        onInput(mc, event.getButton(), event.getAction());
    }

    private static void onInput(Minecraft mc, int key, int action) {
        if (mc.screen == null && BKKeybinds.attackKey1.consumeClick()) {
            PacketHandler.sendToServer(new PacketSyncAttackMauler(key));
        }
        if(mc.screen == null && BKKeybinds.attackKey2.consumeClick()) {
            PacketHandler.sendToServer(new PacketSyncAttackMauler2(key));
        }
    }
}
