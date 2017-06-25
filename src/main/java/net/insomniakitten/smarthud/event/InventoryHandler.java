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
import net.insomniakitten.smarthud.util.StackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

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
            NonNullList<ItemStack> playerCache = NonNullList.create();

            mc.mcProfiler.startSection(LibInfo.PROFILE_CACHE_ITEMS);

            if (mc.player != null && mc.player.world != null) {

                // Inventory
                for (int i = 9; i <= 35; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (StackHandler.containsStack(LibStore.validItems, stack, true, true))
                        playerCache.add(stack);

                }

                // Held
                ItemStack mainhand = StackHandler.containsStack(
                        LibStore.validItems, mc.player.getHeldItemMainhand(), true, true) ?
                        mc.player.getHeldItemMainhand() : ItemStack.EMPTY;
                ItemStack offhand = StackHandler.containsStack(
                        LibStore.validItems, mc.player.getHeldItemOffhand(), true, true) ?
                        mc.player.getHeldItemOffhand() : ItemStack.EMPTY;
                if (!mainhand.isEmpty()) LibStore.itemMainHand = mainhand;
                if (!offhand.isEmpty()) LibStore.itemOffHand = offhand;

            }

            // Only write playerCache to store if state has changed
            if (!LibStore.playerItems.equals(playerCache))
                LibStore.playerItems = playerCache;

            mc.mcProfiler.endStartSection(LibInfo.PROFILE_CACHE_ITEMS);
        }
    }

    /** Used to retrieve items from the player's inventory store
     * @return A NonNullList of ItemStacks
     */
    public static NonNullList<ItemStack> getPlayerItems() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (ItemStack stack : LibStore.playerItems) {
            boolean isWhitelisted = StackHandler.containsStack(
                    LibStore.validItems, stack, true, true);
            boolean alreadyExists = StackHandler.containsStack(
                    list, stack, !LibConfig.checkDamage, !LibConfig.checkNBT);

            if (isWhitelisted && (!alreadyExists || !LibConfig.avoidDuplicates))
                    list.add(stack);
        }
        return list;
    }

}
