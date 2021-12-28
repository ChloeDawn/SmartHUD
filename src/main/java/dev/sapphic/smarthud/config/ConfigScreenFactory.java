package dev.sapphic.smarthud.config;

import dev.sapphic.smarthud.SmartHud;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public final class ConfigScreenFactory extends DefaultGuiFactory {
  public static final String CLASS = "dev.sapphic.smarthud.config.ConfigScreenFactory";

  public ConfigScreenFactory() {
    super(SmartHud.MOD_ID, I18n.format(SmartHud.MOD_ID + ".config"));
  }

  private static List<IConfigElement> elements() {
    final List<IConfigElement> categories = new ArrayList<>(2);

    final IConfigElement slots = ConfigElement.from(SlotsConfig.class);
    final IConfigElement ticker = ConfigElement.from(TickerConfig.class);
    final IConfigElement whitelist = ConfigElement.from(SlotWhitelist.class);

    slots.getChildElements().add(whitelist);
    categories.add(slots);
    categories.add(ticker);

    return categories;
  }

  @Override
  public GuiScreen createConfigGui(final GuiScreen parentScreen) {
    return new GuiConfig(parentScreen, elements(), this.modid, null, false, false, this.title);
  }
}
