package net.lazy.kobe.mastery;

import net.lazy.kobe.mastery.net.MasteryLevelUpPacket;
import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public final class MasteryXpCentral {

    private MasteryXpCentral() {}

    public static void addXp(ServerPlayer player, MasteryType type, int amount) {
        if (amount <= 0) return;

        MasteryData data =
                player.getData(MasteryAttachment.MASTERY);

        MasteryProgress progress =
                data.getOrCreate(type);

        int oldLevel = progress.getLevel();

        progress.addXp(amount);

        int newLevel = progress.getLevel();

        // ------------------------------------------------
        // LEVEL-UP FEEDBACK (matches other skills)
        // ------------------------------------------------
        if (newLevel > oldLevel) {
            NetworkHandler.sendToPlayer(
                    new MasteryLevelUpPacket(type, newLevel),
                    player
            );
        }

        // ------------------------------------------------
        // CLAIMED MASK
        // ------------------------------------------------
        int claimedMask = 0;
        for (int lvl : progress.getClaimedLevels()) {
            claimedMask |= (1 << lvl);
        }

        // ------------------------------------------------
        // SYNC
        // ------------------------------------------------
        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        type,
                        progress.getLevel(),
                        progress.getXpIntoLevel(),
                        claimedMask
                ),
                player
        );
    }
}
