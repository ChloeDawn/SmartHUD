package dev.sapphic.smarthud.config;

import dev.sapphic.smarthud.SmartHud;
import net.minecraftforge.common.config.Config;

@Config(modid = SmartHud.MOD_ID, name = SmartHud.MOD_ID + "/modules", category = "hotbar")
public final class SlotsConfig {
  @Config.Name("alwaysShow")
  public static boolean drawEmpty = false;

  @Config.Name("hudStyle")
  public static SlotsStyle style = SlotsStyle.OFFHAND;

  @Config.Name("isEnabled")
  public static boolean enabled = true;

  @Config.Name("mergeDuplicates")
  public static boolean cumulative = true;

  @Config.Name("renderOverlays")
  public static boolean drawOverlays = true;

  @Config.Name("showStackSize")
  public static boolean drawCounts = false;

  @Config.Name("slotLimit")
  @Config.RangeInt(min = 1, max = 9)
  @Config.SlidingOption
  public static int size = 3;

  private SlotsConfig() {}
}
