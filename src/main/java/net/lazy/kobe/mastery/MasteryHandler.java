package net.lazy.kobe.mastery;

import net.minecraft.server.level.ServerPlayer;

public interface MasteryHandler {

    void reset(ServerPlayer player);

    void setLevel(ServerPlayer player, int level);
}
