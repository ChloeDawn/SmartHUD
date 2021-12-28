package dev.sapphic.smarthud.config;

import dev.sapphic.smarthud.SmartHud;
import net.minecraftforge.common.config.Config;

@Config(modid = SmartHud.MOD_ID, name = SmartHud.MOD_ID + "/modules", category = "item_pickup")
public final class TickerConfig {
  @Config.Name("displayTime")
  public static int durationMillis = 3000;

  @Config.Name("hudStyle")
  public static TickerStyle style = TickerStyle.BOTH;

  @Config.Name("isEnabled")
  public static boolean enabled = true;

  @Config.Name("itemLimit")
  public static int size = 10;

  @Config.Name("priorityMode")
  @Config.Comment({
    "0: The most recently picked up item will be moved to the first slot",
    "1: The order will remain the same, only item counts will be changed"
  })
  @Config.RangeInt(min = 0, max = 1)
  @Config.SlidingOption
  public static int behavior = 0;

  private TickerConfig() {}

  public static long durationTicks() {
    return (durationMillis / 1000) * 20;
  }
}
