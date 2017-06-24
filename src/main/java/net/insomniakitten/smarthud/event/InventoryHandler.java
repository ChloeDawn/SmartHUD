package net.insomniakitten.smarthud.event;

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
import net.insomniakitten.smarthud.lib.LibConfig;
import net.insomniakitten.smarthud.lib.LibInfo;
import net.insomniakitten.smarthud.lib.LibStore;
import net.insomniakitten.smarthud.lib.LibStore.EnumState;
import net.insomniakitten.smarthud.util.StackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings({"MethodCallSideOnly", "LocalVariableDeclarationSideOnly"})
@Mod.EventBusSubscriber(Side.CLIENT)
public class InventoryHandler {

    static {
        if (SmartHUD.DEOBF)
            SmartHUD.LOGGER.info("Registering InventoryHandler to the Event Bus");
    }

    @SubscribeEvent
    public static void onCacheInventory(TickEvent.PlayerTickEvent event) {
        if (LibConfig.isEnabled && !Minecraft.getMinecraft().isGamePaused() && LibStore.validItems.size() > 0) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.mcProfiler.startSection(LibInfo.PROFILE_CACHE_ITEMS);
            NonNullList<Pair<ItemStack, EnumState>> playerCache = NonNullList.create();
            if (mc.player != null && mc.player.world != null) {

                EntityPlayer player = mc.player;
                InventoryPlayer inventory = player.inventory;

                int hotbar1 = 0, hotbar9 = 8, inventory1 = 9, inventory27 = 35;

                // Hotbar
                for (int i = hotbar1; i <= hotbar9; i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (StackHandler.containsStack(LibStore.validItems, stack, true, true))
                        playerCache.add(Pair.of(stack, EnumState.HOTBAR));
                }

                // Inventory
                for (int i = inventory1; i <= inventory27; i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (StackHandler.containsStack(LibStore.validItems, stack, true, true))
                        playerCache.add(Pair.of(stack, EnumState.INVENTORY));

                }

                // Held
                ItemStack hand = player.getHeldItemMainhand();
                ItemStack offhand = player.getHeldItemOffhand();
                if (LibStore.validItems.contains(hand) || LibStore.validItems.contains(offhand)) {
                    playerCache.add(Pair.of(hand, EnumState.HELD));
                    playerCache.add(Pair.of(offhand, EnumState.HELD));
                }
            }

            // Only write playerCache to store if state has changed
            if (!LibStore.playerItems.equals(playerCache))
                LibStore.playerItems = playerCache;

            mc.mcProfiler.endStartSection(LibInfo.PROFILE_CACHE_ITEMS);
        }
    }

    /** Used to retrieve items from the store that match the inventory type given
     * @param stateIn The inventory type you want to query
     *                @see LibStore.EnumState
     * @return A NonNullList of ItemStacks matching the query
     */
    public static NonNullList<ItemStack> getPlayerItems(EnumState stateIn) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (Pair<ItemStack, EnumState> pair : LibStore.playerItems) {
            ItemStack stack = pair.getKey();
            EnumState state = pair.getValue();

            boolean isWhitelisted = StackHandler.containsStack(
                    LibStore.validItems, stack, true, true);
            boolean alreadyExists = StackHandler.containsStack(
                    list, stack, !LibConfig.checkDamage, !LibConfig.checkNBT);

            if (state.equals(stateIn) && isWhitelisted) {
                if (!alreadyExists || !LibConfig.avoidDuplicates)
                    list.add(stack);
            }
        }
        return list;
    }

}
