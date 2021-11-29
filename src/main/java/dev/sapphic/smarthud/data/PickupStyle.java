package dev.sapphic.smarthud.data;

public enum PickupStyle {

    BOTH(true, true),
    ICON_ONLY(true, false),
    NAME_ONLY(false, true);

    private final boolean hasIcon, hasLabel;

    PickupStyle(boolean hasIcon, boolean hasLabel) {
        this.hasIcon = hasIcon;
        this.hasLabel = hasLabel;
    }

    public boolean hasItemIcon() {
        return hasIcon;
    }

    public boolean hasItemName() {
        return hasLabel;
    }

}
