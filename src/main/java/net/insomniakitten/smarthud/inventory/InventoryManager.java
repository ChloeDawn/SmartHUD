package net.insomniakitten.smarthud.inventory;

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
import net.insomniakitten.smarthud.config.SyncManager;
import net.insomniakitten.smarthud.config.WhitelistConfig;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.Profiler;
import net.insomniakitten.smarthud.util.Profiler.Section;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import static net.insomniakitten.smarthud.config.GeneralConfig.configHotbar;

@Mod.EventBusSubscriber(modid = SmartHUD.MOD_ID, value = Side.CLIENT)
public class InventoryManager {

    private static boolean shouldSync;


    /**
     * Called when configs sync, to re-popular the inventory cache - respecting any changed config values
     * @see SyncManager#onConfigChanged for the sync event
     * TODO: if(inventoryHasChanged() || shouldSync) { cacheing }
     */
    public static void forceSync() {
        shouldSync = true;
    }

    /**
     * This stores any whitelisted inventory that will be rendered on the HUD when present
     * in the players inventory. This list is populated when the following method is called:
     * @see WhitelistConfig#processWhitelist()
     * If useWhitelist is false, a default list of inventory is used.
     * @see WhitelistConfig#useWhitelist
     */
    public static NonNullList<CachedItem> whitelist = NonNullList.create();

    /**
     * This stores items found in the players hotbar and inventory
     * that match the whitelist entries and appropriate configs.
     */
    private static NonNullList<CachedItem> hotbar = NonNullList.create(), inventory = NonNullList.create();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.player.world == null) return;

        Profiler.start(Section.CACHE_INVENTORY);

        NonNullList<ItemStack> inv = mc.player.inventory.mainInventory;
        int dim = mc.player.dimension;

        NonNullList<CachedItem> hotbarCache = NonNullList.create();
        NonNullList<CachedItem> inventoryCache = NonNullList.create();

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = inv.get(i).copy();
            if (!stack.isEmpty() && isWhitelisted(stack, dim)) {
                processItemStack(hotbarCache, stack);
            }
        }

        for (int i = 9; i < 36; ++i) {
            ItemStack stack = inv.get(i).copy();
            if (!stack.isEmpty() && isWhitelisted(stack, dim)) {
                processItemStack(inventoryCache, stack);
            }
        }

        hotbar = hotbarCache;
        inventory = inventoryCache;
        shouldSync = false;

        Profiler.end();
    }

    private static void processItemStack(NonNullList<CachedItem> cache, ItemStack stack) {
        boolean dmg = configHotbar.checkDamage, nbt = configHotbar.checkNBT;
        boolean shouldCache = true;
        for (CachedItem target : cache) {
            if (target.matches(stack, !nbt, !dmg) && configHotbar.mergeDuplicates) {
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
        } return false;
    }

    /**
     * Used to retrieve a list of whitelisted items from the player's hotbar
     * @return A list of inventory that match the whitelist and appropriate configs
     */
    public static NonNullList<CachedItem> getHotbar() {
        return hotbar;
    }

    /**
     * Used to retrieve a list of whitelisted items from the player's inventory
     * @return A list of inventory that match the whitelist and appropriate configs
     */
    public static NonNullList<CachedItem> getInventory() {
        return inventory;
    }

}
