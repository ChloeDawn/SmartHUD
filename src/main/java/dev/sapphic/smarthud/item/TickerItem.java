package dev.sapphic.smarthud.item;

import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.config.TickerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SmartHud.MOD_ID)
public final class TickerItem extends CountableItem {
  private static long gameTicks;

  private transient long timestamp = gameTicks;

  public TickerItem(final ItemStack stack) {
    super(stack);
  }

  @SubscribeEvent
  public static void tick(final TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (!Minecraft.getMinecraft().isGamePaused()) {
        ++gameTicks;
      }
    }
  }

  public void renewTimestamp() {
    this.timestamp = gameTicks;
  }

  public long remainingTime() {
    return (this.timestamp + TickerConfig.durationTicks()) - gameTicks;
  }
}
