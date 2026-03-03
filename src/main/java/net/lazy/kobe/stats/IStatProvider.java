package net.lazy.kobe.stats;

import net.lazy.kobe.stats.PlayerStats;
import net.minecraft.server.level.ServerPlayer;

public interface IStatProvider {
    PlayerStats apply(ServerPlayer player, PlayerStats current);
}