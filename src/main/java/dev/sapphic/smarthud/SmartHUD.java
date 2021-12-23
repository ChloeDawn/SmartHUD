package dev.sapphic.smarthud;

import dev.sapphic.smarthud.config.WhitelistParser;
import dev.sapphic.smarthud.event.ItemPickupQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(modid = SmartHUD.ID, useMetadata = true, clientSideOnly = true, acceptedMinecraftVersions = "[1.11,1.13)")
public final class SmartHUD {

    public static final String ID = "smarthud";

    public static final Logger LOGGER = LogManager.getLogger("SmartHUD");

    private static Path configPath;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        configPath = event.getModConfigurationDirectory().toPath().resolve(ID);
        try {
            Files.createDirectories(configPath);
        } catch (final IOException e) {
            LOGGER.catching(e);
        }
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        WhitelistParser.reloadWhitelistEntries();
        ItemPickupQueue.initialize();
    }

    public static Path getConfigPath() {
        return configPath;
    }

}
