package dev.sapphic.smarthud.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.function.IntPredicate;

public final class CachedItem {

    private final ItemStack stack;
    private final int actualCount;
    private int meta = OreDictionary.WILDCARD_VALUE;
    private int count;
    private long timestamp;

    private boolean ignoreNBT = true;
    private boolean ignoreDmg = true;

    private IntPredicate dimensionPredicate;

    public CachedItem(ItemStack stack, int count) {
        this.stack = stack.copy();
        this.actualCount = stack.getCount();
        this.stack.setCount(1);
        this.count = count;
        this.timestamp = TickListener.getTicksElapsed();
        this.dimensionPredicate = i -> true;
    }

    public CachedItem(ItemStack stack) {
        this(stack, 1);
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setMetadata(int meta) {
        this.stack.setItemDamage(meta);
        this.meta = meta;
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;
    }

    public void setIgnoreDmg(boolean ignoreDmg) {
        this.ignoreDmg = ignoreDmg;
    }

    public void setDimensionPredicate(IntPredicate predicate) {
        this.dimensionPredicate = predicate;
    }

    public static void tryCache(List<CachedItem> cache, ItemStack stack, boolean mergeDuplicates) {
        boolean shouldCache = true;
        int count = stack.getCount();
        for (CachedItem item : cache) {
            if (item.matchesStack(stack, false) && mergeDuplicates) {
                item.setCount(item.getCount() + count);
                shouldCache = false;
                break;
            }
        }
        if (shouldCache) {
            cache.add(new CachedItem(stack, count));
        }
    }

    public void renewTimestamp() {
        timestamp = TickListener.getTicksElapsed();
    }

    public String getName() {
        return stack.getDisplayName();
    }

    public long getRemainingTicks(int cooldown) {
        long time = TickListener.getTicksElapsed();
        return (timestamp + cooldown / 50) - time;
    }

    public boolean matchesDimension(int dimension) {
        return dimensionPredicate.test(dimension);
    }

    public boolean matchesStack(ItemStack stack, boolean fuzzy) {
        ItemStack match = stack.copy();
        match.setCount(1);
        if (fuzzy) {
            return this.meta == OreDictionary.WILDCARD_VALUE
                   ? this.stack.getItem() == match.getItem()
                   : ItemStack.areItemsEqualIgnoreDurability(this.stack, match);
        } else {
            boolean isItemEqual = ignoreDmg
                                  ? ItemStack.areItemsEqualIgnoreDurability(this.stack, match)
                                  : ItemStack.areItemsEqual(this.stack, match);
            boolean isNBTEqual = ignoreNBT || ItemStack.areItemStackTagsEqual(this.stack, match);
            return isItemEqual && isNBTEqual;
        }
    }

}
