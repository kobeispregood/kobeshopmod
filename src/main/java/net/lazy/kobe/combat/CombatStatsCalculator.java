package net.lazy.kobe.combat;

import net.lazy.kobe.curios.CurioHelper;
import net.minecraft.server.level.ServerPlayer;

public final class CombatStatsCalculator {

    private CombatStatsCalculator() {}

    /* =============================================================
     *  MAIN ENTRY POINT
     * ============================================================= */
    public static CombatStats calculate(ServerPlayer player) {

        // -------------------------------------------------
        // BASE: mastery-derived stats
        // -------------------------------------------------
        int combatLevel = CombatLevelHelper.getCombatLevel(player);
        CombatStats stats = CombatStats.fromLevel(combatLevel);

        // -------------------------------------------------
        // CURIOS / ACCESSORIES
        // -------------------------------------------------
        stats = stats.add(getCurioBonuses(player));

        // -------------------------------------------------
        // FUTURE: perks, potions, buffs, etc
        // -------------------------------------------------
        // stats = stats.add(getPerkBonuses(player));
        // stats = stats.add(getTemporaryBuffs(player));

        return stats;
    }
    public static CombatStats applyCurioBonuses(
            ServerPlayer player,
            CombatStats baseStats
    ) {
        return baseStats.add(getCurioBonuses(player));
    }
    /* =============================================================
     *  CURIOS
     * ============================================================= */
    private static CombatStats getCurioBonuses(ServerPlayer player) {

        CombatStats bonus = CombatStats.empty();

        if (CurioHelper.hasDunhamDice(player)) {
            bonus = bonus.add(
                    new CombatStats(
                            1.0f,
                            0.05f,   // +5% crit chance
                            1.05f,   // +5% crit damage
                            0.0f
                    )
            );
        }

        return bonus;
    }
}
