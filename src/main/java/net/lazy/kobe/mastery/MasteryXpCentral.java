package net.lazy.kobe.mastery;

import net.lazy.kobe.mastery.net.MasteryLevelUpPacket;
import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

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

        if (newLevel > oldLevel) {
            NetworkHandler.sendToPlayer(
                    new MasteryLevelUpPacket(type, newLevel),
                    player
            );
        }

        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        type,
                        progress.getLevel(),
                        progress.getXpIntoLevel(),
                        new ArrayList<>(progress.getClaimedLevels())
                ),
                player
        );
    }

    public static int getLevel(ServerPlayer player, MasteryType type) {
        MasteryData data = player.getData(MasteryAttachment.MASTERY);
        return data.getOrCreate(type).getLevel();
    }
}