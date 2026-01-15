package net.lazy.kobe.client;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum MiningPerkDisplay {

    MINING_SPEED_5(
            "Mining Speed",
            "Mine blocks slightly faster",
            5,
            Items.IRON_PICKAXE
    ),

    MINING_SPEED_10(
            "Mining Speed",
            "Mine blocks faster",
            10,
            Items.GOLDEN_PICKAXE
    ),

    ORE_XP_10(
            "Ore XP Bonus",
            "Gain more XP from ores",
            10,
            Items.EXPERIENCE_BOTTLE
    ),

    EXTRA_DROPS_15(
            "Extra Drops",
            "Chance to receive an extra ore drop",
            15,
            Items.DIAMOND
    ),

    EXTRA_DROPS_25(
            "Extra Drops",
            "Higher chance for extra ore drops",
            25,
            Items.EMERALD
    );

    public final String title;
    public final String description;
    public final int unlockLevel;
    public final ItemStack icon;

    MiningPerkDisplay(String title, String description, int unlockLevel, Item iconItem) {
        this.title = title;
        this.description = description;
        this.unlockLevel = unlockLevel;
        this.icon = new ItemStack(iconItem);
    }

    public boolean unlocked(int masteryLevel) {
        return masteryLevel >= unlockLevel;
    }
}
