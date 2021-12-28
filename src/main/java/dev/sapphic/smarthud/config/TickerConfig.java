package dev.sapphic.smarthud.config;

import dev.sapphic.smarthud.SmartHud;
import net.minecraftforge.common.config.Config;

@Config.LangKey(SmartHud.MOD_ID + ".ticker")
@Config(modid = SmartHud.MOD_ID, name = SmartHud.MOD_ID + "/modules", category = "item_pickup")
public final class TickerConfig {
  @Config.Name("displayTime")
  @Config.LangKey(SmartHud.MOD_ID + ".ticker.duration")
  @Config.Comment("The duration in milliseconds that items should be shown for")
  public static int durationMillis = 3000;

  @Config.Name("hudStyle")
  @Config.LangKey(SmartHud.MOD_ID + ".ticker.style")
  @Config.Comment("The look and feel of the item ticker")
  public static TickerStyle style = TickerStyle.BOTH;

  @Config.Name("isEnabled")
  @Config.LangKey(SmartHud.MOD_ID + ".ticker.enabled")
  @Config.Comment("Enable the item ticker")
  public static boolean enabled = true;

  @Config.Name("itemLimit")
  @Config.LangKey(SmartHud.MOD_ID + ".ticker.size")
  @Config.Comment("The maximum number of items to show at once")
  public static int size = 10;

  @Config.Name("priorityMode")
  @Config.LangKey(SmartHud.MOD_ID + ".ticker.behavior")
  @Config.Comment({
    "The behavior of the item ticker",
    "0: Sort by most recent pickup",
    "1: Sort by initial pickup"
  })
  @Config.RangeInt(min = 0, max = 1)
  @Config.SlidingOption
  public static int behavior = 0;

  private TickerConfig() {}

  public static long durationTicks() {
    return (durationMillis / 1000) * 20;
  }
}
