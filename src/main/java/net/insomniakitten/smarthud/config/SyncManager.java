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
import net.insomniakitten.smarthud.feature.hotbar.InventoryCache;
import net.insomniakitten.smarthud.feature.pickup.PickupManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class SyncManager {

    private SyncManager() {}

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(SmartHUD.ID)) {
            ConfigManager.sync(SmartHUD.ID, Config.Type.INSTANCE);
            WhitelistConfig.initialize();
            InventoryCache.forceSync();
            PickupManager.reloadQueue();
        }
    }

}
