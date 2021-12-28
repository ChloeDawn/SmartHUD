package dev.sapphic.smarthud.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class CountableItem {
  private final ItemStack stack;
  private int count;

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
}
