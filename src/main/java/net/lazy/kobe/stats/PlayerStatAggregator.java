package net.lazy.kobe.stats;

import net.lazy.kobe.combat.CombatStats;
import net.lazy.kobe.combat.CombatStatsCalculator;
import net.lazy.kobe.titles.TitleStatProvider;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerStatAggregator {

    private PlayerStatAggregator() {}

    public static PlayerStats calculate(ServerPlayer player) {

        PlayerStats stats = PlayerStats.base();

        // 1. Combat
        CombatStats combat = CombatStatsCalculator.calculate(player);

        stats = stats.add(new PlayerStats(
                combat,
                1.0,
                1.0,
                0.0,
                1.0,
                1.0
        ));

        // 2. Title
        stats = stats.add(TitleStatProvider.apply(player));

        return stats;
    }
}