package net.lazy.kobe.mastery;

import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public final class MasteryXp {

    private MasteryXp() {}

    public static void addMasteryXp(
            ServerPlayer player,
            MasteryType type,
            float amount
    ) {
        if (amount <= 0) return;

        MasteryData mastery =
                player.getData(MasteryAttachment.MASTERY);

        MasteryProgress progress =
                mastery.getOrCreate(type);

        progress.addXp(Math.round(amount));

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
}