package dev.sapphic.smarthud.config;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import dev.sapphic.smarthud.SmartHud;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
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

  private static final Gson GSON =
      new GsonBuilder()
          .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
          .setLenient()
          .create();

  private static final Type TYPE =
      new TypeToken<Set<Entry>>() {
        private static final long serialVersionUID = -7857956607945326797L;
      }.getType();

  @Config.Name("isEnabled")
  public static boolean enabled = true;

  private static ImmutableCollection<Entry> allEntries = ImmutableSet.of();
  private static ImmutableCollection<Entry> entries = ImmutableSet.of();

  private SlotWhitelist() {}

  public static ImmutableCollection<Entry> allEntries() {
    return allEntries;
  }

  public static ImmutableCollection<Entry> entries() {
    return entries;
  }

  public static void rebuild() {
    if (enabled) {
      try (final Reader reader = Files.newBufferedReader(generateConfigs())) {
        allEntries = ImmutableSet.copyOf(GSON.<Set<Entry>>fromJson(reader, TYPE));
        entries =
            allEntries.stream()
                .filter(SlotWhitelist::ensureRegistered)
                .collect(ImmutableSet.toImmutableSet());
      } catch (final JsonParseException | IOException e) {
        LOGGER.catching(e);
      }
    } else {
      entries = ImmutableSet.of(new Entry(Items.CLOCK), new Entry(Items.COMPASS));
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

  private static boolean ensureRegistered(final Entry entry) {
    if (!ForgeRegistries.ITEMS.containsKey(entry.item)) {
      LOGGER.debug("Skipping unregistered item '{}'", entry.item);
      return false;
    }

    return true;
  }

  public static final class Entry {
    @JsonAdapter(ItemDeserializer.class)
    private final ResourceLocation item;

    private final int meta;
    private final boolean ignoreNbt;
    private final boolean ignoreDmg;

    @JsonAdapter(DimensionsDeserializer.class)
    private final @Nullable IntSet dimensions;

    public Entry() {
      this(new ResourceLocation("air"));
    }

    private Entry(final Item item) {
      this(Objects.requireNonNull(item.getRegistryName()));
    }

    private Entry(final ResourceLocation item) {
      this.item = item;
      this.meta = OreDictionary.WILDCARD_VALUE;
      this.ignoreNbt = true;
      this.ignoreDmg = true;
      this.dimensions = null;
    }

    public boolean equals(final ItemStack stack, final int dimension) {
      return ((this.dimensions == null) || this.dimensions().contains(dimension))
          && this.item.equals(stack.getItem().getRegistryName())
          && ((this.meta == OreDictionary.WILDCARD_VALUE) || (stack.getItemDamage() == this.meta));
    }

    public ResourceLocation item() {
      return this.item;
    }

    public int metadata() {
      return this.meta;
    }

    public boolean ignoresNbt() {
      return this.ignoreNbt;
    }

    public boolean ignoresDmg() {
      return this.ignoreDmg;
    }

    public @Nullable IntSet dimensions() {
      return this.dimensions;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;

      hashCode = (31 * hashCode) + this.item.hashCode();
      hashCode = (31 * hashCode) + Integer.hashCode(this.meta);
      hashCode = (31 * hashCode) + Boolean.hashCode(this.ignoreNbt);
      hashCode = (31 * hashCode) + Boolean.hashCode(this.ignoreDmg);
      hashCode = (31 * hashCode) + Objects.hashCode(this.dimensions);

      return hashCode;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
      if (this == o) {
        return true;
      }

      if ((o == null) || (this.getClass() != o.getClass())) {
        return false;
      }

      final Entry that = (Entry) o;

      return (this.meta == that.meta)
          && (this.ignoreNbt == that.ignoreNbt)
          && (this.ignoreDmg == that.ignoreDmg)
          && this.item.equals(that.item)
          && Objects.equals(this.dimensions, that.dimensions);
    }

    public static final class ItemDeserializer implements JsonDeserializer<ResourceLocation> {
      @Override
      public ResourceLocation deserialize(
          final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        return new ResourceLocation(context.deserialize(json, String.class));
      }
    }

    public static final class DimensionsDeserializer implements JsonDeserializer<IntSet> {
      @Override
      public IntSet deserialize(
          final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        final IntSet dimensions = new IntOpenHashSet(context.<int[]>deserialize(json, int[].class));

        dimensions.removeIf(dimension -> !DimensionManager.isDimensionRegistered(dimension));

        if (dimensions.size() < 2) {
          if (dimensions.isEmpty()) {
            return IntSets.EMPTY_SET;
          }

          return IntSets.singleton(dimensions.iterator().nextInt());
        }

        return IntSets.unmodifiable(dimensions);
      }
    }
  }
}
