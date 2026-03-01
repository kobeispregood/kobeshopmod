package net.lazy.kobe.farming.perk;

import net.lazy.kobe.mastery.MasteryProgress;

public class FarmingPerks {

    /* ============================================================
     * AUTO REPLANT
     * ============================================================ */

    public static boolean autoReplantEnabled(MasteryProgress progress) {
        return progress.getLevel() >= 30;
    }

    /* ============================================================
     * CROP DROP MULTIPLIER
     * ============================================================ */

    public static float cropDropMultiplier(MasteryProgress progress) {

        int level = progress.getLevel();

        if (level < 25) return 1.0f;

        float bonus = (level - 25) * 0.04f;

        bonus = Math.min(bonus, 0.80f);

        return 1.0f + bonus;
    }

    /* ============================================================
     * DOUBLE HARVEST CHANCE
     * ============================================================ */

    public static float doubleHarvestChance(MasteryProgress progress) {

        int level = progress.getLevel();

        if (level < 10) return 0f;

        float chance = 0.10f + (level - 10) * 0.005f;

        return Math.min(0.25f, chance);
    }

    /* ============================================================
     * NO TRAMPLE
     * ============================================================ */

    public static boolean noTrampleEnabled(MasteryProgress progress) {
        return progress.getLevel() >= 20;
    }

    /* ============================================================
     * BONUS XP
     * ============================================================ */

    public static int bonusXp(MasteryProgress progress) {
        return Math.min(5, 1 + progress.getLevel() / 10);
    }

    /* ============================================================
     * ENUM (OPTIONAL)
     * ============================================================ */

    public enum FarmingPerk {
        RIGHT_CLICK_HARVEST(25),
        DOUBLE_HARVEST(10),
        BONUS_XP(5),
        NO_TRAMPLE(20),
        AUTO_REPLANT(30);

        private final int requiredLevel;

        FarmingPerk(int level) {
            this.requiredLevel = level;
        }

        public int requiredLevel() {
            return requiredLevel;
        }
    }
}