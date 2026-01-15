package net.lazy.kobe.farming;

import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public class FarmingLeveling {

    public static void addXp(ServerPlayer player, int amount) {
        FarmingData data = player.getData(FarmingAttachment.FARMING);
        if (data == null) return;

        int oldLevel = data.getLevel();

        data.addXp(amount);

        int newLevel = data.getLevel();


        if (newLevel > oldLevel) {
            for (int lvl = oldLevel + 1; lvl <= newLevel; lvl++) {

                NetworkHandler.sendToPlayer(
                        new FarmingLevelUpPacket(lvl),
                        player
                );
            }
        }

        NetworkHandler.syncFarming(player, data);
    }
}