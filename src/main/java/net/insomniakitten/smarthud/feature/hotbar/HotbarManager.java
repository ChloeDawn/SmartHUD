package net.insomniakitten.smarthud.feature.hotbar; 
 
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

import net.insomniakitten.smarthud.config.GeneralConfig;
import net.insomniakitten.smarthud.util.CachedItem;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import static net.insomniakitten.smarthud.config.GeneralConfig.configHotbar;

public class HotbarManager {

    public static boolean canRender(RenderGameOverlayEvent event) {
        return !event.isCanceled() && event.getType().equals(ElementType.HOTBAR) && configHotbar.isEnabled;
    }

    /**
     * Method used to calculate the required offset of the attack indicator
     *
     * @return An int used to offset the element
     */
    public static int getAttackIndicatorOffset() {
        NonNullList<CachedItem> cachedItems = InventoryCache.getInventory();
        int slot = 20, padding = 9;
        if (cachedItems.size() > 0) {
            int limit = GeneralConfig.configHotbar.slotLimit;
            int slots = cachedItems.size() < limit ? cachedItems.size() : limit;
            return (slot * slots) + padding;
        } else if (GeneralConfig.configHotbar.alwaysShow) {
            return slot + padding;
        } else return 0;
    }

}
