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

import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.event.ConfigEventManager;
import net.insomniakitten.smarthud.SmartHUDWhitelist;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.insomniakitten.smarthud.util.ModProfiler.Section;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import static net.insomniakitten.smarthud.SmartHUDConfig.HOTBAR;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class InventoryCache {

    /**
     * This stores any whitelisted inventory that will be rendered on the HUD when present
     * in the players inventory. This list is populated when the following method is called:
     *
     * @see SmartHUDWhitelist#parseWhitelistEntries()
     * If useWhitelist is false, a default list of inventory is used.
     * @see SmartHUDWhitelist#useWhitelist
     */
    public static NonNullList<CachedItem> whitelist = NonNullList.create();
    private static boolean shouldSync;
    /**
     * This stores items found in the players inventory that
     * match the whitelist entries and appropriate configs.
     */
    private static NonNullList<CachedItem> inventory = NonNullList.create();

    private InventoryCache() {}

    /**
     * Called when configs sync, to re-popular the inventory cache - respecting any changed config values
     *
     * @see ConfigEventManager#onConfigChanged for the sync event
     * TODO: if(inventoryHasChanged() || shouldSync) { cacheing }
     */
    public static void forceSync() {
        shouldSync = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.player.world == null) return;

        ModProfiler.start(Section.CACHE_INVENTORY);

        NonNullList<ItemStack> inv = mc.player.inventory.mainInventory;
        int dim = mc.player.dimension;

        NonNullList<CachedItem> inventoryCache = NonNullList.create();

        for (int i = 9; i < 36; ++i) {
            ItemStack stack = inv.get(i).copy();
            if (!stack.isEmpty() && isWhitelisted(stack, dim)) {
                processItemStack(inventoryCache, stack);
            }
        }

        inventory = inventoryCache;
        shouldSync = false;

        ModProfiler.end();
    }

    public static void processItemStack(NonNullList<CachedItem> cache, ItemStack stack) {
        boolean dmg = HOTBAR.checkDamage, nbt = HOTBAR.checkNBT;
        boolean shouldCache = true;
        for (CachedItem target : cache) {
            if (target.matches(stack, !nbt, !dmg) && HOTBAR.mergeDuplicates) {
                target.setCount(target.getCount() + stack.getCount());
                shouldCache = false;
                break;
            }
        }
        if (shouldCache) {
            cache.add(new CachedItem(stack, stack.getCount()));
        }
    }

    public static boolean isWhitelisted(ItemStack stack, int dimension) {
        if (whitelist.size() < 1) return false;
        for (CachedItem cachedItem : whitelist) {
            if (cachedItem.matches(stack)) {
                DimensionType type = DimensionType.getById(dimension);
                if (cachedItem.getDimension().test(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Used to retrieve a list of whitelisted items from the player's inventory
     *
     * @return A list of inventory that match the whitelist and appropriate configs
     */
    public static NonNullList<CachedItem> getInventory() {
        return inventory;
    }

}
