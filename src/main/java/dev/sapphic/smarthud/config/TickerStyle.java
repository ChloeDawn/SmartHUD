package dev.sapphic.smarthud.config;

public enum TickerStyle {
  BOTH(true, true),
  ICON_ONLY(true, false),
  NAME_ONLY(false, true);

  private final boolean icons;
  private final boolean labels;

  TickerStyle(final boolean icons, final boolean labels) {
    this.icons = icons;
    this.labels = labels;
  }

  public boolean hasIcons() {
    return this.icons;
  }

  public boolean hasLabels() {
    return this.labels;
  }
}
