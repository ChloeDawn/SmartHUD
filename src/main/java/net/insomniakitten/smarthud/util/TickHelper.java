package net.insomniakitten.smarthud.util;

import net.insomniakitten.smarthud.SmartHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = SmartHUD.MOD_ID, value = Side.CLIENT)
public class TickHelper {
    
    private static long ticksElapsed;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
            ticksElapsed++;
        }
    }
    
    public static long getTicksElapsed() {
        return ticksElapsed;
    }

}
