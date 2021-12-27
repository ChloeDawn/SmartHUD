package dev.sapphic.smarthud.config;

public enum SlotsStyle {
  OFFHAND(0),
  HOTBAR(22),
  INVISIBLE(-1);

  private final int textureV;

  SlotsStyle(final int textureV) {
    this.textureV = textureV;
  }

  public final int getTextureV() {
    return this.textureV;
  }

  public final boolean isTextured() {
    return this != INVISIBLE;
  }
}
