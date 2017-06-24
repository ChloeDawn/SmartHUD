package net.insomniakitten.smarthud.lib;

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

import net.insomniakitten.smarthud.event.InventoryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Pair;

public class LibStore {

    /**
     * This stores any whitelisted items that will be rendered on the HUD when present in the players inventory.
     * Cached from the config String[] during postInit and onConfigChanged.
     * If the useWhitelist is false, a default list of items is used.
     */
    public static NonNullList<ItemStack> validItems = NonNullList.create();

    /**
     * This stores items found in the players inventory that match the contents of the whitelist.
     * @see InventoryHandler for the event that populates this list.
     */
    public static NonNullList<Pair<ItemStack, EnumState>> playerItems = NonNullList.create();

    /**
     * Used for tracking the location of an ItemStack on the player. Applied to a Pair along with an ItemStack.
     * @see InventoryHandler for the event that applies these in a Pair.
     */
    public enum EnumState {INVENTORY, HOTBAR, HELD}

}
