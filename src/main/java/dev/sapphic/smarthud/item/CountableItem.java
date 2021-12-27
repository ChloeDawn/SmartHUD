package dev.sapphic.smarthud.item;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Type;

public abstract class CountableItem {
  @SerializedName("item")
  @JsonAdapter(ItemStackDeserializer.class)
  private final ItemStack stack;

  private transient int count;

  protected CountableItem(final ItemStack stack) {
    this.stack = stack;
  }

  public final ItemStack stack() {
    return this.stack;
  }

  public final int count() {
    return this.stack.getCount() + this.count;
  }

  public final void count(final ItemStack stack) {
    this.count += stack.getCount();
  }

  public String abbreviatedCount() {
    final StringBuilder abbr = new StringBuilder(1);
    final int count = this.count();
    final int magnitude = MathHelper.floor(StrictMath.log(count) / StrictMath.log(1000.0));
    final int value = MathHelper.floor((count / StrictMath.pow(1000.0, magnitude)) * 10.0);

    abbr.append(value / 10);

    if (((value / 10) < 10) && ((value % 10) > 0)) {
      abbr.append('.').append(value % 10);
    }

    if (magnitude > 0) {
      abbr.append("kmbtpe".charAt(magnitude - 1));
    }

    return abbr.toString();
  }

  private static final class ItemStackDeserializer implements JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(
        final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
      final ResourceLocation name = new ResourceLocation(context.deserialize(json, String.class));
      final Item item = MoreObjects.firstNonNull(ForgeRegistries.ITEMS.getValue(name), Items.AIR);

      return (item == Items.AIR) ? ItemStack.EMPTY : new ItemStack(item);
    }
  }
}
