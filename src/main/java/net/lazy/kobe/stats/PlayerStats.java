package net.lazy.kobe.stats;

import net.lazy.kobe.combat.CombatStats;

public class PlayerStats {

    // =========================
    // CORE COMBAT (Wrapped)
    // =========================
    private final CombatStats combat;

    // =========================
    // MINING
    // =========================
    private final double miningSpeedMultiplier;
    private final double oreXpMultiplier;
    private final double extraDropChance;

    // =========================
    // FARMING
    // =========================
    private final double farmingYieldMultiplier;
    private final double farmingXpMultiplier;

    public PlayerStats(
            CombatStats combat,
            double miningSpeedMultiplier,
            double oreXpMultiplier,
            double extraDropChance,
            double farmingYieldMultiplier,
            double farmingXpMultiplier
    ) {
        this.combat = combat;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.oreXpMultiplier = oreXpMultiplier;
        this.extraDropChance = extraDropChance;
        this.farmingYieldMultiplier = farmingYieldMultiplier;
        this.farmingXpMultiplier = farmingXpMultiplier;
    }

    // =========================
    // BASE (NO BONUSES)
    // =========================
    public static PlayerStats base() {
        return new PlayerStats(
                CombatStats.empty(),
                1.0,
                1.0,
                0.0,
                1.0,
                1.0
        );
    }

    // =========================
    // STACKING
    // =========================
    public PlayerStats add(PlayerStats other) {
        return new PlayerStats(
                this.combat.add(other.combat),
                this.miningSpeedMultiplier * other.miningSpeedMultiplier,
                this.oreXpMultiplier * other.oreXpMultiplier,
                this.extraDropChance + other.extraDropChance,
                this.farmingYieldMultiplier * other.farmingYieldMultiplier,
                this.farmingXpMultiplier * other.farmingXpMultiplier
        );
    }

    // =========================
    // GETTERS
    // =========================
    public CombatStats getCombat() {
        return combat;
    }

    public double getMiningSpeedMultiplier() {
        return miningSpeedMultiplier;
    }

    public double getOreXpMultiplier() {
        return oreXpMultiplier;
    }

    public double getExtraDropChance() {
        return extraDropChance;
    }

    public double getFarmingYieldMultiplier() {
        return farmingYieldMultiplier;
    }

    public double getFarmingXpMultiplier() {
        return farmingXpMultiplier;
    }
}