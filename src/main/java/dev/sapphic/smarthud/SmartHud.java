package dev.sapphic.smarthud;

import dev.sapphic.smarthud.config.ConfigScreenFactory;
import dev.sapphic.smarthud.config.SlotWhitelist;
import dev.sapphic.smarthud.item.TickerQueue;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(
    modid = SmartHud.MOD_ID,
    useMetadata = true,
    clientSideOnly = true,
    acceptedMinecraftVersions = "[1.11,1.13)",
    guiFactory = ConfigScreenFactory.CLASS)
@Mod.EventBusSubscriber(Side.CLIENT)
public final class SmartHud {
  public static final String MOD_ID = "smarthud";

  private static @MonotonicNonNull Path configs;

  public static Path configs() {
    return configs;
  }

  @SubscribeEvent
  public static void configChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
    if (MOD_ID.equals(event.getModID())) {
      ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
      SlotWhitelist.rebuild();
      TickerQueue.rebuild();
    }
  }

  @Mod.EventHandler
  public static void preInitialize(final FMLPreInitializationEvent event) {
    configs = event.getModConfigurationDirectory().toPath().resolve(MOD_ID);

    try {
      Files.createDirectories(configs);
    } catch (final IOException e) {
      LogManager.getLogger().catching(e);
    }
  }

  @Mod.EventHandler
  public static void postInitialize(final FMLPostInitializationEvent event) {
    SlotWhitelist.rebuild();
    TickerQueue.initialize();
  }
}
