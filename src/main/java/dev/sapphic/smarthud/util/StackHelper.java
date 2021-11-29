package dev.sapphic.smarthud.util;

import dev.sapphic.smarthud.config.WhitelistParser;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class StackHelper {

    private StackHelper() {}

    public static boolean isWhitelisted(ItemStack stack, int dimension) {
        for (CachedItem item : WhitelistParser.getWhitelist()) {
            if (item.matchesStack(stack, true) && item.matchesDimension(dimension)) {
                return true;
            }
        }
        return false;
    }

    public static void processStack(List<CachedItem> cache, ItemStack stack, boolean mergeDuplicates) {
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

}
