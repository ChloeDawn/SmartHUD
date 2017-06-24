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
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = LibInfo.MOD_ID, name = LibInfo.CONFIG_GENERAL)
@Config.LangKey("config.smarthud.general")
@Mod.EventBusSubscriber(modid = LibInfo.MOD_ID)
public class LibConfig {

    static {
        if (SmartHUD.DEOBF)
            SmartHUD.LOGGER.info("Registering LibConfig to the Event Bus");
    }

    @Config.Name("Enable HUD")
    @Config.Comment({"Should the HUD be enabled? If false, the HUD won't render."})
    @Config.LangKey("config.smarthud.general.isenabled")
    public static boolean isEnabled = true;

    @Config.Name("Slot Limit")
    @Config.Comment({"The number of slots that can be displayed on the HUD at one time.\n" +
            "The HUD will display the first x number of slots with matching item stacks " +
            "from the player's inventory."})
    @Config.LangKey("config.smarthud.general.itemlimit")
    @Config.RangeInt(min = 1, max = 9)
    public static int slotLimit = 3;

    @Config.Name("Always Show")
    @Config.Comment({"Should a slot always be rendered on the HUD, even if the player has no valid items?\n" +
            "If false, slots will only be rendered when valid items are present."})
    @Config.LangKey("config.smarthud.general.alwaysshow")
    public static boolean alwaysShow = false;

    @Config.Name("Avoid Duplicates")
    @Config.Comment({"Should the HUD only show one of each item when multiple of the same item are found?\n" +
            "If false, having two clocks in seperate slots in your inventory would display " +
            "two clocks on the HUD, for example."})
    @Config.LangKey("config.smarthud.general.avoidduplicates")
    public static boolean avoidDuplicates = true;

    @Config.Name("Show Stack Size")
    @Config.Comment({"Should the hud show the stack size for an item, if the player has multiple of the " +
            "item in their inventory?\n" +
            "When avoidDuplicates is enabled, the stack size will be shown as the total count of that item across " +
            "your entire inventory.\n" +
            "If false, the items will be rendered with no stack size information."})
    @Config.LangKey("config.smarthud.general.showstacksize")
    public static boolean showStackSize = false;

    @Config.Name("Check Damage")
    @Config.Comment({"Should the mod check the item's damage to avoid multiple of the same item being merged when" +
            "they have differing damage values?\n" +
            "If false, items such as pickaxes will be merged even if they have different remaining durability."})
    @Config.LangKey("config.smarthud.general.checkdamage")
    public static boolean checkDamage = false;

    @Config.Name("Check NBT")
    @Config.Comment({"Should the mod check an item's NBT information to avoid multiple of the same item being" +
            " merged when they have unique NBT information?\n" +
            "If false, any items with matching names (and metadata if checkMeta is false)" +
            " will be \"merged\" regardless of NBT,\n" +
            " and only the first of that item from the player's inventory will be displayed on the HUD.\n" +
            "This config option will only function when avoidDuplicates is enabled."})
    @Config.LangKey("config.smarthud.general.checknbt")
    public static boolean checkNBT = false;

    @Config.Name("Render Overlays")
    @Config.Comment({"Should the item's overlay be rendered on the HUD?\n" +
            "If false, the HUD will not render the durability of tools, or any other mod-added item overlays."})
    @Config.LangKey("config.smarthud.general.renderoverlays")
    public static boolean renderOverlays = true;

    @Config.Name("HUD Style")
    @Config.Comment({"Change the style of the HUD's slot texture.\n" +
            "0: Off-hand style, 1: Hotbar style, 2: Invisible"})
    @Config.LangKey("config.smarthud.general.hudstyle")
    @Config.RangeInt(min = 0, max = 2)
    public static int hudStyle = 0;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(LibInfo.MOD_ID))
            ConfigManager.sync(LibInfo.MOD_ID, Config.Type.INSTANCE);
    }


}
