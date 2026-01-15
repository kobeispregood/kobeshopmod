package net.lazy.kobe.farming.perk;

import net.lazy.kobe.farming.FarmingData;

public class FarmingPerks {

    /* ============================================================
     *  AUTO-REPLANT
     * ============================================================ */

    public static boolean autoReplantEnabled(FarmingData data) {
        return data.getLevel() >= 30;
    }

    /* ============================================================
     *  CROP DROP MULTIPLIER (BUFFED + SMOOTH)
     *
     *  Level 25 → 1.00x
     *  Level 30 → 1.20x
     *  Level 35 → 1.40x
     *  Level 40 → 1.60x
     *  Level 45 → 1.80x (CAP)
     * ============================================================ */

    public static float cropDropMultiplier(FarmingData data) {

        int level = data.getLevel();

        if (level < 25) return 1.0f;

        // +4% per level after 25
        float bonus = (level - 25) * 0.04f;

        // Hard cap at +80%
        bonus = Math.min(bonus, 0.80f);

        return 1.0f + bonus;
    }

    /* ============================================================
     *  PERK DEFINITIONS
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

    /* ============================================================
     *  DOUBLE HARVEST CHANCE (BUFFED)
     *
     *  Level 10 → 10%
     *  Level 20 → 15%
     *  Level 30 → 20%
     *  Level 40 → 25% (CAP)
     * ============================================================ */

    public static float doubleHarvestChance(FarmingData data) {

        int level = data.getLevel();

        if (level < 10) return 0f;

        float chance = 0.10f + (level - 10) * 0.005f;

        return Math.min(0.25f, chance);
    }

    /* ============================================================
     *  NO TRAMPLE
     * ============================================================ */

    public static boolean noTrampleEnabled(FarmingData data) {
        return data.getLevel() >= 20;
    }

    /* ============================================================
     *  BONUS XP (SCALED)
     *
     *  Level 5  → +1 XP
     *  Level 15 → +2 XP
     *  Level 25 → +3 XP
     *  Level 35 → +4 XP
     *  Level 45 → +5 XP
     * ============================================================ */

    public static int bonusXp(FarmingData data) {
        return Math.min(5, 1 + data.getLevel() / 10);
    }
}
