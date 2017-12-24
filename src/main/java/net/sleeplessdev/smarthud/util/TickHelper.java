package net.sleeplessdev.smarthud.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.sleeplessdev.smarthud.SmartHUD;

@EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class TickHelper {

    private static long ticksElapsed;

    private TickHelper() {}

    @SubscribeEvent
    protected static void onClientTick(ClientTickEvent event) {
        if (event.phase == Phase.END) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (!mc.isGamePaused()) ticksElapsed++;
        }
    }

    public static long getTicksElapsed() {
        return ticksElapsed;
    }

}
