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

import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.util.StackHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = LibInfo.MOD_ID, name = LibInfo.CONFIG_WHITELIST, category = "whitelist")
@Config.LangKey("config.smarthud.whitelist")
@Mod.EventBusSubscriber(modid = LibInfo.MOD_ID)
public class LibWhitelist {

    static {
        if (SmartHUD.DEOBF)
            SmartHUD.LOGGER.info("Registering LibWhitelist to the Event Bus");
    }

    @Config.Name("Use Whitelist")
    @Config.Comment({"Should the HUD use the configurable whitelist when checking for valid items?\n" +
            "If false, Smart HUD will fall back to only checking for vanilla clocks and compasses."})
    @Config.LangKey("config.smarthud.whitelist.usewhitelist")
    public static boolean useWhitelist = true;

    @Config.Name("Item List")
    @Config.Comment({"Configure items that will be displayed on the HUD when present in the players inventory.\n" +
                    "Follow the format modid:resourcename:metadata otherwise the item will not be registered.\n" +
                    "Metadata is not required, and not defining it will default the check to any metadata.\n" +
                    "This information can be obtained via Advanced Tooltips (F3+H) in-game."})
    @Config.LangKey("config.smarthud.whitelist.list")
    public static String[] itemList = generateDefaultEntries();

    private static String[] generateDefaultEntries() {
        String[] defaults = new String[6];
        defaults[0] = "minecraft:clock";
        defaults[1] = "minecraft:compass";
        defaults[2] = "randomthings:goldencompass";
        defaults[3] = "endercompass:ender_compass";
        defaults[4] = "toughasnails:thermometer";
        defaults[5] = "toughasnails:season_clock";
        return defaults;
    }

    public static void initializeWhitelist() {
        if (SmartHUD.DEOBF)
            SmartHUD.LOGGER.info("Processing items " + (
                    useWhitelist ?
                            "from the whitelist"
                            : "from the list of default entries"));
        LibStore.validItems = LibWhitelist.useWhitelist ?
                LibWhitelist.processConfigWhitelist()
                : LibWhitelist.populateDefaultList();
    }

    /**
     * Queries the Item registry with a resource location generated from each String in the whitelist String[].
     *
     * @return A list of ItemStacks that match the list of resource names. Any invalid resource names are ignored.
     */
    public static NonNullList<ItemStack> processConfigWhitelist() {
        NonNullList<ItemStack> cache = NonNullList.create();
        for (String entry : itemList) {
            String[] data = entry.split(":");
            if (data.length > 1) {
                String modid = data[0], name = data[1];
                int meta = data.length > 2 ? Integer.valueOf(data[2]) : -1;

                ItemStack stack = StackHandler.getStackFromResourceName(modid, name, meta);
                if (!stack.isEmpty()) cache.add(stack);
            }
        }
        return cache;
    }

    /**
     * The default list of items that the mod will fall back to if useWhitelist is false in the config.
     */
    public static NonNullList<ItemStack> populateDefaultList() {
        NonNullList<ItemStack> defaults = NonNullList.create();
        defaults.add(new ItemStack(Items.CLOCK));
        defaults.add(new ItemStack(Items.COMPASS));
        return defaults;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(LibInfo.MOD_ID)) {
            ConfigManager.sync(LibInfo.MOD_ID, Config.Type.INSTANCE);
            initializeWhitelist();
        }
    }

}