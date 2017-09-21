package net.insomniakitten.smarthud.feature.pickup; 
 
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

public class PickupConfig {

    @Config.Name("Is Enabled")
    @Config.Comment({ "Should the HUD be enabled? If false, the HUD won't render." })
    @Config.LangKey("config.smarthud.pickup.enabled")
    public boolean isEnabled = true;

    @Config.Name("Item Limit")
    @Config.Comment("The maximum number of items that can be listed on the HUD at one time.")
    @Config.LangKey("config.smarthid.pickup.limit")
    public int itemLimit = 10;

    @Config.Name("Display Priority Mode")
    @Config.Comment({ "Configure the order items are sorted on the HUD.",
                      "0: The most recently picked up item will be moved to the first slot",
                      "1: The order will remain the same, only item counts will be changed" })
    @Config.LangKey("config.smarthud.pickup.priority")
    @Config.RangeInt(min = 0, max = 1)
    public int priorityMode = 0;

    @Config.Name("Pickup Display Time")
    @Config.Comment("The amount of time in milliseconds the picked up item should be displayed on the HUD.")
    @Config.LangKey("config.smarthud.pickup.time")
    public int displayTime = 3000;

    @Config.Name("HUD Style")
    @Config.Comment("Configure how items are displayed on the HUD when picked up")
    @Config.LangKey("config.smarthud.pickup.style")
    public PickupStyle hudStyle = PickupStyle.BOTH;

    public enum PickupStyle {
        BOTH(true, true),
        ICON_ONLY(true, false),
        NAME_ONLY(false, true);

        private final boolean hasIcon, hasLabel;

        PickupStyle(boolean hasIcon, boolean hasLabel) {
            this.hasIcon = hasIcon;
            this.hasLabel = hasLabel;
        }

        public boolean hasItemIcon() {
            return hasIcon;
        }

        public boolean hasItemName() {
            return hasLabel;
        }
    }


}
