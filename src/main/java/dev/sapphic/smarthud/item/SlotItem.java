package dev.sapphic.smarthud.item;

import dev.sapphic.smarthud.config.SlotWhitelist;
import dev.sapphic.smarthud.config.SlotsConfig;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Objects;

public final class SlotItem extends CountableItem {
  private final boolean ignoreDmg;
  private final boolean ignoreNbt;

  private SlotItem(final ItemStack stack, final boolean ignoreDmg, final boolean ignoreNbt) {
    super(stack);
    this.ignoreDmg = ignoreDmg;
    this.ignoreNbt = ignoreNbt;
  }

  public static void account(
      final Collection<SlotItem> items, final ItemStack stack, final int dimension) {
    for (final SlotWhitelist.Entry entry : SlotWhitelist.entries()) {
      if (!entry.equals(stack, dimension)) {
        continue;
      }

      if (SlotsConfig.cumulative) {
        for (final SlotItem item : items) {
          if (areSimilar(item, stack)) {
            item.count(stack);
            return;
          }
        }
      }

      items.add(new SlotItem(stack, entry.ignoresDmg(), entry.ignoresNbt()));
      return;
    }
  }

  private static boolean areSimilar(final SlotItem item, final ItemStack stack) {
    return (item.stack().getItem() == stack.getItem())
        && (item.ignoreDmg || (item.stack().getItemDamage() == stack.getItemDamage()))
        && (item.ignoreNbt
            || Objects.equals(item.stack().getTagCompound(), stack.getTagCompound()));
  }
}
