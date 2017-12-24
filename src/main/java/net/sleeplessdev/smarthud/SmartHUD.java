package net.sleeplessdev.smarthud;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.sleeplessdev.smarthud.config.WhitelistParser;
import net.sleeplessdev.smarthud.event.ItemPickupQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = SmartHUD.ID, name = SmartHUD.NAME, version = SmartHUD.VERSION, clientSideOnly = true)
public final class SmartHUD {

    public static final String ID = "smarthud";
    public static final String NAME = "Smart HUD";
    public static final String VERSION = "%VERSION%";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static File configPath;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        configPath = new File(event.getModConfigurationDirectory(), ID);
        if (!configPath.exists()) configPath.mkdirs();
        WhitelistParser.registerReloadListener();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        ItemPickupQueue.initialize();
    }

    public static File getConfigPath() {
        return configPath;
    }

}
