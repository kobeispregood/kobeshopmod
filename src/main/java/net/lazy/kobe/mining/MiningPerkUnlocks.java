package net.lazy.kobe.mining;

import java.util.ArrayList;
import java.util.List;

public class MiningPerkUnlocks {

    public static List<String> getUnlockedPerks(int oldLevel, int newLevel) {
        List<String> perks = new ArrayList<>();

        check(perks, oldLevel, newLevel, 5,  "Mining Speed +5%");
        check(perks, oldLevel, newLevel, 10, "Mining Speed +10%");
        check(perks, oldLevel, newLevel, 10, "Ore XP Bonus +10%");
        check(perks, oldLevel, newLevel, 15, "Extra Drop Chance Unlocked");
        check(perks, oldLevel, newLevel, 20, "Mining Speed +18%");
        check(perks, oldLevel, newLevel, 20, "Ore XP Bonus +20%");
        check(perks, oldLevel, newLevel, 25, "Extra Drop Chance +6%");
        check(perks, oldLevel, newLevel, 30, "Mining Speed +25%");
        check(perks, oldLevel, newLevel, 30, "Ore XP Bonus +30%");
        check(perks, oldLevel, newLevel, 40, "Extra Drop Chance +10%");

        return perks;
    }

    private static void check(List<String> perks, int oldLevel, int newLevel, int trigger, String text) {
        if (oldLevel < trigger && newLevel >= trigger) {
            perks.add(text);
        }
    }
}
