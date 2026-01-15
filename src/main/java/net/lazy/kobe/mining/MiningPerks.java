package net.lazy.kobe.mining;

import net.minecraft.util.RandomSource;

public class MiningPerks {

    // -------------------------
    // MINING SPEED (GEN ONLY)
    // -------------------------
    public static float getMiningSpeedMultiplier(int level) {
        if (level >= 40) return 1.30f;
        if (level >= 30) return 1.25f;
        if (level >= 20) return 1.18f;
        if (level >= 10) return 1.10f;
        if (level >= 5)  return 1.05f;
        return 1.0f;
    }

    // -------------------------
    // ORE XP BONUS
    // -------------------------
    public static float getOreXpMultiplier(int level) {
        if (level >= 30) return 1.30f;
        if (level >= 20) return 1.20f;
        if (level >= 10) return 1.10f;
        return 1.0f;
    }

    // -------------------------
    // EXTRA DROP CHANCE
    // -------------------------
    public static boolean rollExtraDrop(int level, RandomSource rand) {
        double chance =
                level >= 40 ? 0.10 :
                        level >= 25 ? 0.06 :
                                level >= 15 ? 0.03 :
                                        0.0;

        return chance > 0 && rand.nextDouble() < chance;
    }
    public static int getSpeedPercent(int level) {
        return Math.round((getMiningSpeedMultiplier(level) - 1.0f) * 100f);
    }

    public static int getOreXpPercent(int level) {
        float mult = getOreXpMultiplier(level);
        return mult <= 1.0f ? 0 : Math.round((mult - 1.0f) * 100f);
    }

    public static int getExtraDropPercent(int level) {
        if (level >= 40) return 10;
        if (level >= 25) return 6;
        if (level >= 15) return 3;
        return 0;
    }
}
