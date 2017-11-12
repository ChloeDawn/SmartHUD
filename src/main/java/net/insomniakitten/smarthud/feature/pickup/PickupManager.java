package net.insomniakitten.smarthud.feature.pickup; 
 
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

import com.google.common.collect.EvictingQueue;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import static net.insomniakitten.smarthud.SmartHUDConfig.PICKUP;

public final class PickupManager {

    protected static EvictingQueue<CachedItem> items = EvictingQueue.create(PICKUP.itemLimit);

    private PickupManager() {}

    public static void initialize() {
        PickupManager.reloadQueue();
        PickupQueue.initializeParticleQueue();
    }

    public static void reloadQueue() {
        EvictingQueue<CachedItem> newQueue = EvictingQueue.create(PICKUP.itemLimit);
        newQueue.addAll(items);
        items = newQueue;
    }

    public static boolean canRender(RenderGameOverlayEvent event) {
        return !event.isCanceled() && event.getType().equals(ElementType.CHAT) && PICKUP.isEnabled;
    }

    public static int getDisplayTimeTicks() {
        return PICKUP.displayTime / 50;
    }

    protected static void handleItemCollection(ItemStack stack) {
        ModProfiler.start(ModProfiler.Section.HANDLE_COLLECTION);

        if (!stack.isEmpty()) {
            EvictingQueue<CachedItem> newItems = EvictingQueue.create(PICKUP.itemLimit);
            newItems.addAll(items);
            if (!items.isEmpty()) {
                boolean shouldCache = true;
                for (CachedItem cachedItem : items) {
                    if (cachedItem.matches(stack)) {
                        int count = cachedItem.getCount() + stack.getCount();
                        if (PICKUP.priorityMode == 0) {
                            newItems.remove(cachedItem);
                            newItems.add(new CachedItem(stack, count));
                            shouldCache = false;
                        } else if (PICKUP.priorityMode == 1) {
                            cachedItem.setCount(count);
                            cachedItem.renewTimestamp();
                            shouldCache = false;
                        }
                        break;
                    }
                }
                if (shouldCache) {
                    newItems.add(new CachedItem(stack, stack.getCount()));
                }
            } else {
                newItems.add(new CachedItem(stack, stack.getCount()));
            }
            items = newItems;
        }

        ModProfiler.end();
    }

}
