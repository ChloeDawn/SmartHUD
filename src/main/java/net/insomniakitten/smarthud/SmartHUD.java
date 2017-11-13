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

import com.google.common.collect.ImmutableList;
import net.insomniakitten.smarthud.feature.ISmartHUDFeature;
import net.insomniakitten.smarthud.feature.glance.GlanceFeature;
import net.insomniakitten.smarthud.feature.hotbar.HotbarFeature;
import net.insomniakitten.smarthud.feature.hotbar.InventoryCache;
import net.insomniakitten.smarthud.feature.pickup.PickupFeature;
import net.insomniakitten.smarthud.feature.pickup.PickupQueue;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SmartHUD.ID, name = SmartHUD.NAME, version = SmartHUD.VERSION, clientSideOnly = true)
@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class SmartHUD {

    public static final String ID = "smarthud";
    public static final String NAME = "Smart HUD";
    public static final String VERSION = "%VERSION%";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final ImmutableList<ISmartHUDFeature> FEATURES = ImmutableList.of(
            new GlanceFeature(), new HotbarFeature(), new PickupFeature()
    );

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        SmartHUDWhitelist.initialize();
        PickupQueue.initialize();
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(SmartHUD.ID)) {
            ConfigManager.sync(SmartHUD.ID, Config.Type.INSTANCE);
            SmartHUDWhitelist.initialize();
            InventoryCache.forceSync();
            PickupQueue.reloadQueue();
        }
    }

}
