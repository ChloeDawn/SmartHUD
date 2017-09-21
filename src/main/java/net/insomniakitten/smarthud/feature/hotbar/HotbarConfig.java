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

import net.minecraftforge.common.config.Config;

public class HotbarConfig {

    @Config.Name("Is Enabled")
    @Config.Comment({ "Should the HUD be enabled? If false, the HUD won't render." })
    @Config.LangKey("config.smarthud.hotbar.enabled")
    public boolean isEnabled = true;

    @Config.Name("Slot Limit")
    @Config.Comment({ "The number of slots that can be displayed on the HUD at one time.",
                      "The HUD will display the first x number of slots with matching item stacks ",
                      "from the player's inventory." })
    @Config.LangKey("config.smarthud.hotbar.limit")
    @Config.RangeInt(min = 1, max = 9)
    public int slotLimit = 3;

    @Config.Name("Always Show")
    @Config.Comment({ "Should a slot always be rendered on the HUD, even if the player has no valid items?",
                      "If false, slots will only be rendered when valid items are present." })
    @Config.LangKey("config.smarthud.hotbar.always")
    public boolean alwaysShow = false;

    @Config.Name("Merge Duplicates")
    @Config.Comment({ "Should the HUD only show one of each item when multiple of the same item are found?",
                      "If false, having two clocks in seperate slots in your inventory would display two clocks on the HUD, for example." })
    @Config.LangKey("config.smarthud.hotbar.merge")
    public boolean mergeDuplicates = true;

    @Config.Name("Show Stack Size")
    @Config.Comment({ "Should the hud show the stack size for an item, if the player has multiple of the item in their inventory?",
                      "When mergeDuplicates is enabled, the stack size will be shown as the total count of that item across your entire inventory.",
                      "If false, the items will be rendered with no stack size information." })
    @Config.LangKey("config.smarthud.hotbar.size")
    public boolean showStackSize = false;

    @Config.Name("Check Damage")
    @Config.Comment({ "Should the mod check the item's damage to avoid multiple of the same item being merged when" +
                              "they have differing damage values?",
                      "If false, items such as pickaxes will be merged even if they have different remaining durability." })
    @Config.LangKey("config.smarthud.hotbar.damage")
    public boolean checkDamage = false;

    @Config.Name("Check NBT")
    @Config.Comment({ "Should the mod check an item's NBT information to avoid multiple of the same item being merged when they have unique NBT information?",
                      "If false, any items with matching names (and metadata if checkMeta is false) will be \"merged\" regardless of NBT,",
                      "and only the first of that item from the player's inventory will be displayed on the HUD.",
                      "This config option will only function when mergeDuplicates is enabled." })
    @Config.LangKey("config.smarthud.hotbar.nbt")
    public boolean checkNBT = false;

    @Config.Name("Render Overlays")
    @Config.Comment({ "Should the item's overlay be rendered on the HUD?",
                      "If false, the HUD will not render the durability of tools, or any other mod-added item overlays." })
    @Config.LangKey("config.smarthud.hotbar.overlays")
    public boolean renderOverlays = true;

    @Config.Name("HUD Style")
    @Config.Comment({ "Change the style of the HUD's slot texture." })
    @Config.LangKey("config.smarthud.hotbar.style")
    public HotbarStyle hudStyle = HotbarStyle.OFFHAND;

    public enum HotbarStyle {
        OFFHAND(0),
        HOTBAR(22),
        INVISIBLE(-1);

        private final int textureY;

        HotbarStyle(int textureY) {
            this.textureY = textureY;
        }

        public int getTextureY() {
            return textureY;
        }
    }

}
