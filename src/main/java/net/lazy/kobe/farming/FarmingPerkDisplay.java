package net.lazy.kobe.farming;

public enum FarmingPerkDisplay {

    // examples – we can flesh these out later
    CROP_DROPS_1(5, "Crop Drops +10%"),
    CROP_DROPS_2(10, "Crop Drops +20%"),
    AUTO_REPLANT(15, "Auto Replant"),
    XP_BOOST(25, "Farming XP +10%");

    public final int unlockLevel;
    public final String title;

    FarmingPerkDisplay(int unlockLevel, String title) {
        this.unlockLevel = unlockLevel;
        this.title = title;
    }
}
