package net.insomniakitten.smarthud.config;

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
import net.insomniakitten.smarthud.feature.armor.ArmorConfig;
import net.insomniakitten.smarthud.feature.hotbar.HotbarConfig;
import net.insomniakitten.smarthud.feature.pickup.PickupConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = SmartHUD.MOD_ID, name = "smarthud/general", category = "")
@Config.LangKey("config.smarthud.general")
public class GeneralConfig {

    @Config.Name("Hotbar HUD")
    @Config.LangKey("config.smarthud.hotbar")
    public static HotbarConfig configHotbar = new HotbarConfig();

    @Config.Name("Armor HUD")
    @Config.LangKey("config.smarthud.armor")
    public static ArmorConfig configArmor = new ArmorConfig();

    @Config.Name("Item Pickup HUD")
    @Config.LangKey("config.smarthud.pickup")
    public static PickupConfig configPickup = new PickupConfig();

}
