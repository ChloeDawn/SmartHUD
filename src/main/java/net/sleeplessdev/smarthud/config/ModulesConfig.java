package net.sleeplessdev.smarthud.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.sleeplessdev.smarthud.SmartHUD;
import net.sleeplessdev.smarthud.data.HotbarStyle;
import net.sleeplessdev.smarthud.data.PickupStyle;

@Config(modid = SmartHUD.ID, name = SmartHUD.ID + "/modules", category = "")
@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class ModulesConfig {

    @Config.Name("hotbar")
    public static final Hotbar HOTBAR_HUD = new Hotbar();

    @Config.Name("item_pickup")
    public static final ItemPickup ITEM_PICKUP_HUD = new ItemPickup();

    private ModulesConfig() {}

    @SubscribeEvent
    protected static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (SmartHUD.ID.equals(event.getModID())) {
            ConfigManager.sync(SmartHUD.ID, Config.Type.INSTANCE);
        }
    }

    public static final class Hotbar {
        public boolean alwaysShow = false;

        public HotbarStyle hudStyle = HotbarStyle.OFFHAND;

        public boolean isEnabled = true;

        public boolean mergeDuplicates = true;

        public boolean renderOverlays = true;

        public boolean showStackSize = false;

        @Config.RangeInt(min = 1, max = 9)
        public int slotLimit = 3;
    }

    public static final class ItemPickup {
        //public PickupAnimation animationStyle = PickupAnimation.GLIDE;

        public int displayTime = 3000;

        public PickupStyle hudStyle = PickupStyle.BOTH;

        public boolean isEnabled = true;

        public int itemLimit = 10;

        @Config.Comment({ "0: The most recently picked up item will be moved to the first slot",
                          "1: The order will remain the same, only item counts will be changed" })
        @Config.RangeInt(min = 0, max = 1)
        public int priorityMode = 0;
    }

}
