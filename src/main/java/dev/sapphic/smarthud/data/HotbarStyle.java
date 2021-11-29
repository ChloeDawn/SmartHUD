package dev.sapphic.smarthud.data;

public enum HotbarStyle {

    OFFHAND(0),
    HOTBAR(22),
    INVISIBLE(-1);

    private final int textureY;

    HotbarStyle(int textureY) {
        this.textureY = textureY;
    }

    public int getTextureY() {
        return textureY;
    }

}
