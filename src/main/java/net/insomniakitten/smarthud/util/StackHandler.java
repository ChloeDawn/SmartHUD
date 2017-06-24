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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.text.DecimalFormat;

public class StackHandler {

    private static final String[] NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int MAX_LENGTH = 4;

    /** Abbreviates a number with a suffix after a certain length, e.g. 1500 -> 1.5k
     * @param value The number you want to abbreviate
     * @return The abbreviated number
     */
    public static String getAbbreviatedValue(Number value) {
        String shorthand = new DecimalFormat("##0E0").format(value);
        shorthand = shorthand.replaceAll("E[0-9]",
                NUM_SUFFIXES[Character.getNumericValue(shorthand.charAt(shorthand.length() - 1)) / 3]);
        while (shorthand.length() > MAX_LENGTH || shorthand.matches("[0-9]+\\.[a-z]"))
            shorthand = shorthand.substring(0, shorthand.length() - 2)
                    + shorthand.substring(shorthand.length() - 1);
        return shorthand;
    }

    /**
     * Compares an ItemStack against a list to see if the list contains the item
     *
     * @param list         The list you want to compare against
     * @param stack        The ItemStack you want to match
     * @param ignoreDamage This will prevent damage value differences causing a mismatch if false
     * @param ignoreNBT    This will prevent NBT differences causing a mismatch if false
     * @return A boolean, true if the list contains the ItemStack
     */
    public static boolean containsStack(
            NonNullList<ItemStack> list, ItemStack stack,
            boolean ignoreDamage, boolean ignoreNBT) {
        if (list.size() > 0)
            for (ItemStack stack1 : list) {
                boolean isItemEqual = ignoreDamage ?
                        ItemStack.areItemsEqualIgnoreDurability(stack1, stack)
                        : ItemStack.areItemsEqual(stack1, stack);
                boolean isNBTEqual = ignoreNBT || ItemStack.areItemStackTagsEqual(stack1, stack);
                if (isItemEqual && isNBTEqual) return true;
            }
        return false;
    }

    /**
     * Used to query the Item registry for a resource name
     *
     * @param modid The modid used to create a ResourceLocation from
     * @param name  The resource name to create a ResourceLocation from
     * @param meta  The metadata of the item to create an ItemStack for. -1 equals none specified
     * @return An ItemStack matching the resource name. If one isn't found, returns an empty stack
     */
    public static ItemStack getStackFromResourceName(String modid, String name, int meta) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(modid, name));
        //Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid, name));
        // TODO: Figure out why exactly this is breaking
        if (item != null)
            return meta >= 0 ? new ItemStack(item, 1, meta) : new ItemStack(item);
        else return ItemStack.EMPTY;
    }

}
