package dev.sapphic.smarthud.config;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.item.SlotItem;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

@Config(modid = SmartHud.MOD_ID, name = SmartHud.MOD_ID + "/general", category = "whitelist")
public final class SlotWhitelist {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final Gson GSON = new Gson();
  private static final Type TYPE =
      new TypeToken<Set<SlotItem>>() {
        private static final long serialVersionUID = -7857956607945326797L;
      }.getType();

  @Config.Name("isEnabled")
  public static boolean enabled = true;

  private static ImmutableCollection<SlotItem> items = ImmutableSet.of();

  private SlotWhitelist() {}

  public static ImmutableCollection<SlotItem> get() {
    return items;
  }

  public static void rebuild() {
    if (enabled) {
      try (final Reader reader = Files.newBufferedReader(generateConfigs())) {
        items =
            GSON.<Set<SlotItem>>fromJson(reader, TYPE).stream()
                .filter(item -> !item.stack().isEmpty())
                .collect(ImmutableSet.toImmutableSet());
      } catch (final JsonParseException | IOException e) {
        LOGGER.catching(e);
      }
    } else {
      items = ImmutableSet.of(new SlotItem(Items.CLOCK), new SlotItem(Items.COMPASS));
    }
  }

  private static Path generateConfigs() {
    final Path userDefined = SmartHud.configs().resolve("whitelist.json");

    try (final @Nullable InputStream stream =
        SmartHud.class.getResourceAsStream("/defaults.json")) {
      Objects.requireNonNull(stream, "defaults.json");
      Files.copy(stream, userDefined);
    } catch (final FileAlreadyExistsException ignored) {
    } catch (final IOException e) {
      LOGGER.catching(e);
    }

    return userDefined;
  }
}
