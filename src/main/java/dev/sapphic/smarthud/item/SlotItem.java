package dev.sapphic.smarthud.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import dev.sapphic.smarthud.config.SlotWhitelist;
import dev.sapphic.smarthud.config.SlotsConfig;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

public final class SlotItem extends CountableItem {
  @SerializedName("meta")
  private final int metadata;

  @SerializedName("ignore_nbt")
  private final boolean ignoresNbt;

  @SerializedName("ignore_dmg")
  private final boolean ignoresDmg;

  @SerializedName("dimensions")
  @JsonAdapter(DimensionsDeserializer.class)
  private final @Nullable IntSet dimensions;

  public SlotItem(final ItemStack stack) {
    super(stack);
    this.metadata = OreDictionary.WILDCARD_VALUE;
    this.ignoresNbt = true;
    this.ignoresDmg = true;
    this.dimensions = null;
  }

  public SlotItem(final Item item) {
    this(new ItemStack(item));
  }

  public SlotItem() {
    this(ItemStack.EMPTY);
  }

  public static void account(
      final Collection<SlotItem> items, final ItemStack stack, final int dimension) {
    for (final SlotItem item : SlotWhitelist.get()) {
      if (((item.dimensions == null) || item.dimensions.contains(dimension))
          && (item.stack().getItem() == stack.getItem())
          && ((item.metadata == OreDictionary.WILDCARD_VALUE)
              || (stack.getItemDamage() == item.metadata))) {
        account(items, stack);
        return;
      }
    }
  }

  private static void account(final Collection<SlotItem> items, final ItemStack stack) {
    if (SlotsConfig.cumulative) {
      for (final SlotItem item : items) {
        if (item.areSimilar(stack)) {
          item.count(stack);
          return;
        }
      }
    }

    items.add(new SlotItem(stack));
  }

  @Override
  public String toString() {
    return "{"
        + "item:"
        + this.stack().getItem().getRegistryName()
        + ", meta:"
        + this.metadata
        + ", ignore_nbt:"
        + this.ignoresNbt
        + ", ignore_dmg:"
        + this.ignoresDmg
        + ", dimensions:"
        + this.dimensions
        + "}";
  }

  private boolean areSimilar(final ItemStack stack) {
    if ((this.stack().getItem() == stack.getItem())
        && (this.ignoresDmg || (this.stack().getItemDamage() == stack.getItemDamage()))) {
      return this.ignoresNbt
          || Objects.equals(this.stack().getTagCompound(), stack.getTagCompound());
    }

    return false;
  }

  private static final class DimensionsDeserializer implements JsonDeserializer<IntSet> {
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
