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

import net.insomniakitten.smarthud.config.WhitelistConfig;
import net.insomniakitten.smarthud.feature.pickup.PickupManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(   modid = SmartHUD.MOD_ID,
        name = SmartHUD.MOD_NAME,
        version = SmartHUD.MOD_VERSION,
        acceptedMinecraftVersions = SmartHUD.MC_VERSIONS,
        clientSideOnly = true)

public class SmartHUD {

    public static final String MOD_ID = "smarthud";
    public static final String MOD_NAME = "Smart HUD";
    public static final String MOD_VERSION = "%mod_version%";
    public static final String MC_VERSIONS = "[1.11,1.13)";

    public static final String CONFIG_GENERAL = "smarthud/general";
    public static final String CONFIG_WHITELIST = "smarthud/whitelist";

    @Mod.Instance(SmartHUD.MOD_ID)
    public static SmartHUD instance;

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final boolean DEOBF = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        WhitelistConfig.initializeWhitelist();
        PickupManager.initialize();
    }

}
