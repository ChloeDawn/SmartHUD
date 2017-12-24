package net.sleeplessdev.smarthud.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class CachedItem {

    private final ItemStack stack;
    private final int actualCount;
    private int meta = OreDictionary.WILDCARD_VALUE;
    private int count;
    private long timestamp;

    private boolean ignoreNBT = true;
    private boolean ignoreDmg = true;

    private DimensionPredicate dimension;

    public CachedItem(ItemStack stack, int count) {
        this.stack = stack.copy();
        this.actualCount = stack.getCount();
        this.stack.setCount(1);
        this.count = count;
        this.timestamp = TickHelper.getTicksElapsed();
        this.dimension = DimensionPredicate.ANY;
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

    public void renewTimestamp() {
        timestamp = TickHelper.getTicksElapsed();
    }

    public DimensionPredicate getDimension() {
        return dimension;
    }

    public void setDimension(DimensionPredicate dimension) {
        this.dimension = dimension;
    }

    public String getName() {
        return stack.getDisplayName();
    }

    public long getRemainingTicks(int cooldown) {
        long time = TickHelper.getTicksElapsed();
        return (timestamp + cooldown / 50) - time;
    }

    public boolean matches(ItemStack stack, boolean fuzzy) {
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
