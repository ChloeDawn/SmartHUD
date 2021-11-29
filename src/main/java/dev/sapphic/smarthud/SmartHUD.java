package dev.sapphic.smarthud;

import dev.sapphic.smarthud.config.WhitelistParser;
import dev.sapphic.smarthud.event.ItemPickupQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = SmartHUD.ID,
     name = SmartHUD.NAME,
     version = SmartHUD.VERSION,
     dependencies = "after:*",
     clientSideOnly = true)
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
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        WhitelistParser.reloadWhitelistEntries();
        ItemPickupQueue.initialize();
    }

    public static File getConfigPath() {
        return configPath;
    }

}
