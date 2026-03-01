package net.lazy.kobe.combat;

public record CombatStats(
        float baseDamageMultiplier,
        float critChance,
        float critMultiplier,
        float lifestealPercent,
        float defence
) {

    public CombatStats add(CombatStats other) {
        return new CombatStats(
                this.baseDamageMultiplier * other.baseDamageMultiplier,
                this.critChance + other.critChance,
                this.critMultiplier * other.critMultiplier,
                this.lifestealPercent + other.lifestealPercent,
                this.defence + other.defence
        );
    }

    /* =============================================================
     *  DEFAULT (NO MASTERY / NO BONUSES)
     * ============================================================= */
    public static CombatStats empty() {
        return new CombatStats(
                1.0f, // no bonus damage
                0.0f, // no crit chance
                1.0f, // no crit multiplier
                0.0f,  // no lifesteal
                0.0f // no defense
        );
    }

    public static final CombatStats DUNHAM_DICE_STATS =
            new CombatStats(
                    1.0f,   // no base damage bonus
                    0.05f,  // +5% crit chance
                    1.05f,  // +5% crit damage
                    0.0f,    // no lifesteal
                    0.0f    // no defense
            );

    /* =============================================================
     *  MASTERY LEVEL → COMBAT STATS
     * ============================================================= */
    public static CombatStats fromLevel(int level) {

        float baseDamage =
                1.0f + (level * 0.20f);

        float critChance = 0.0f;
        float critMultiplier = 1.0f;

        if (level >= 5) {
            float progress = Math.min((level - 5) / 40f, 1f);

            critChance =
                    lerp(0.10f, 0.50f, progress);

            critMultiplier =
                    lerp(1.20f, 2.0f, progress);
        }

        float lifesteal = 0.0f;

        if (level >= 20) {
            lifesteal =
                    Math.min(0.01f + ((level - 20) * 0.002f), 0.05f);
        }

        float defense = 0f;

        if (level >= 10) {
            defense = Math.min((level - 10) * 3f, 150f);
        }

        return new CombatStats(
                baseDamage,
                critChance,
                critMultiplier,
                lifesteal,
                defense
        );
    }
    /* =============================================================
     *  UTIL
     * ============================================================= */
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
