package net.insomniakitten.smarthud.util; 
 
/*
 *  Copyright 2017 InsomniaKitten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import net.insomniakitten.smarthud.feature.pickup.PickupManager;
import net.insomniakitten.smarthud.util.dimension.AnyDimension;
import net.insomniakitten.smarthud.util.dimension.DimensionPredicate;
import net.minecraft.item.ItemStack;

public class CachedItem {

    private final ItemStack stack;
    private int count;
    private final int actualCount;
    private long timestamp;
    private DimensionPredicate dimension;

    public CachedItem(ItemStack stack, int count) {
        this.stack = stack.copy();
        this.actualCount = stack.getCount();
        this.stack.setCount(1);
        this.count = count;
        this.timestamp = TickHelper.getTicksElapsed();
        this.dimension = AnyDimension.INSTANCE;
    }

    public CachedItem(ItemStack stack) {
        this(stack, 1);
    }

    public CachedItem setDimension(DimensionPredicate dimension) {
        this.dimension = dimension;
        return this;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void renewTimestamp() {
        timestamp = TickHelper.getTicksElapsed();
    }

    public DimensionPredicate getDimension() {
        return dimension;
    }

    public String getName() {
        return stack.getDisplayName();
    }

    public boolean hasExpired() {
        long expiration = timestamp + PickupManager.getDisplayTimeTicks();
        return expiration < TickHelper.getTicksElapsed();
    }

    public long getRemainingTicks() {
        int cooldown = PickupManager.getDisplayTimeTicks();
        long time = TickHelper.getTicksElapsed();
        return (timestamp + cooldown) - time;
    }

    public boolean matches(ItemStack stack) {
        ItemStack match = stack.copy();
        match.setCount(1);
        return ItemStack.areItemsEqualIgnoreDurability(this.stack, match);
    }

    public boolean matches(ItemStack stack, boolean ignoreNBT, boolean ignoreDamage) {
        ItemStack match = stack.copy();
        match.setCount(1);
        boolean isItemEqual = ignoreDamage ?
                ItemStack.areItemsEqualIgnoreDurability(this.stack, match)
                : ItemStack.areItemsEqual(this.stack, match);
        boolean isNBTEqual = ignoreNBT || ItemStack.areItemStackTagsEqual(this.stack, match);
        return isItemEqual && isNBTEqual;
    }

}
