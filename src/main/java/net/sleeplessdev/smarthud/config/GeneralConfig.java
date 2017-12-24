package net.sleeplessdev.smarthud.config;

import net.minecraftforge.common.config.Config;
import net.sleeplessdev.smarthud.SmartHUD;

@Config(modid = SmartHUD.ID, name = SmartHUD.ID + "/general", category = "")
public final class GeneralConfig {

    @Config.Name("whitelist")
    public static final Whitelist WHITELIST = new Whitelist();

    private GeneralConfig() {}

    public static final class Whitelist {
        public boolean isEnabled = true;
        public boolean logMissingEntries = false;
    }

}
