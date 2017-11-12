package net.insomniakitten.smarthud;

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

import net.insomniakitten.smarthud.feature.hotbar.HotbarConfig;
import net.insomniakitten.smarthud.feature.pickup.PickupConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = SmartHUD.ID, name = "smarthud/general", category = "")
@Config.LangKey("config.smarthud.general")
public final class SmartHUDConfig {

    @Config.Name("Hotbar HUD")
    @Config.LangKey("config.smarthud.hotbar")
    public static final HotbarConfig HOTBAR = new HotbarConfig();

    @Config.Name("Item Pickup HUD")
    @Config.LangKey("config.smarthud.pickup")
    public static final PickupConfig PICKUP = new PickupConfig();

}
