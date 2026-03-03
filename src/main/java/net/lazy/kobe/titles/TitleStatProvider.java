package net.lazy.kobe.titles;

import net.lazy.kobe.combat.CombatStats;
import net.lazy.kobe.stats.PlayerStats;
import net.minecraft.server.level.ServerPlayer;

public final class TitleStatProvider {

    private TitleStatProvider() {}

    public static PlayerStats apply(ServerPlayer player) {

        var data = player.getData(TitleAttachment.TITLES);
        if (data == null) return PlayerStats.base();

        String selected = data.getSelected();
        if (selected == null) return PlayerStats.base();

        switch (selected) {

            case "warrior":
                // +10% combat XP (we’ll apply that later in XP logic)
                return new PlayerStats(
                        CombatStats.empty(), // no direct combat stat change
                        1.0,
                        1.0,
                        0.0,
                        1.0,
                        1.0
                );

            case "harvester":
                return new PlayerStats(
                        CombatStats.empty(),
                        1.0,
                        1.0,
                        0.0,
                        1.0,
                        1.10 // +10% farming XP
                );

            default:
                return PlayerStats.base();
        }
    }
}