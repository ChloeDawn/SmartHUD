package net.sleeplessdev.smarthud.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.sleeplessdev.smarthud.config.WhitelistParser;

import java.util.List;

public final class StackHelper {

    private StackHelper() {}

    public static boolean isWhitelisted(ItemStack stack, int dimension) {
        DimensionType type = DimensionType.getById(dimension);
        for (CachedItem item : WhitelistParser.getWhitelist()) {
            if (item.matches(stack, true) && item.getDimension().test(type)) {
                return true;
            }
        }
        return false;
    }

    public static void processStack(List<CachedItem> cache, ItemStack stack, boolean mergeDuplicates) {
        boolean shouldCache = true;
        int count = stack.getCount();
        for (CachedItem item : cache) {
            if (item.matches(stack, false) && mergeDuplicates) {
                item.setCount(item.getCount() + count);
                shouldCache = false;
                break;
            }
        }
        if (shouldCache) {
            cache.add(new CachedItem(stack, count));
        }
    }

}
