package dev.sapphic.smarthud.config;

import dev.sapphic.smarthud.SmartHud;
import net.minecraftforge.common.config.Config;

@Config.LangKey(SmartHud.MOD_ID + ".slots")
@Config(modid = SmartHud.MOD_ID, name = SmartHud.MOD_ID + "/modules", category = "hotbar")
public final class SlotsConfig {
  @Config.Name("alwaysShow")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.empty")
  @Config.Comment("Always draw a slot regardless of items present")
  public static boolean includeEmpty = false;

  @Config.Name("hudStyle")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.style")
  @Config.Comment("The look and feel of the item slots")
  public static SlotsStyle style = SlotsStyle.OFFHAND;

  @Config.Name("isEnabled")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.enabled")
  @Config.Comment("Enable the item slots")
  public static boolean enabled = true;

  @Config.Name("mergeDuplicates")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.cumulative")
  @Config.Comment("Merge similar items into the same slot")
  public static boolean cumulative = true;

  @Config.Name("renderOverlays")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.overlays")
  @Config.Comment("Draw the item overlays, such as enchantment glints")
  public static boolean overlays = true;

  @Config.Name("showStackSize")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.counts")
  @Config.Comment("Draw the (cumulative) counts of items")
  public static boolean counts = false;

  @Config.Name("slotLimit")
  @Config.LangKey(SmartHud.MOD_ID + ".slots.size")
  @Config.Comment("The maximum number of item slots to draw")
  @Config.RangeInt(min = 1, max = 9)
  @Config.SlidingOption
  public static int size = 3;

  private SlotsConfig() {}
}
