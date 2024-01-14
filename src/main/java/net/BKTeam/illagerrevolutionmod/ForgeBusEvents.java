package net.BKTeam.illagerrevolutionmod;

import net.BKTeam.illagerrevolutionmod.keybind.BKKeybinds;
import net.BKTeam.illagerrevolutionmod.network.PacketHandler;
import net.BKTeam.illagerrevolutionmod.network.PacketSyncMountAttacks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IllagerRevolutionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeBusEvents {

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        onInput(mc, event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        onInput(mc, event.getButton(), event.getAction());
    }

    private static void onInput(Minecraft mc, int key, int action) {
        if (mc.screen == null && BKKeybinds.attackKey1.consumeClick()) {
            PacketHandler.sendToServer(new PacketSyncMountAttacks(key,(byte) 0));
        }
        if(mc.screen == null && BKKeybinds.attackKey2.consumeClick()) {
            PacketHandler.sendToServer(new PacketSyncMountAttacks(key,(byte) 1));
        }
        if(mc.screen == null && BKKeybinds.attackKey3.consumeClick()) {
            PacketHandler.sendToServer(new PacketSyncMountAttacks(key,(byte) 2));
        }
    }
}
