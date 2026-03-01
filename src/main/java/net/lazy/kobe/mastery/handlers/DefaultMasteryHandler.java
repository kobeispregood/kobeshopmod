package net.lazy.kobe.mastery.handlers;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryHandler;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class DefaultMasteryHandler implements MasteryHandler {

    private final MasteryType type;

    public DefaultMasteryHandler(MasteryType type) {
        this.type = type;
    }

    @Override
    public void reset(ServerPlayer player) {

        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(type);

        progress.setLevel(0);
        progress.setXp(0);
        progress.resetClaims();

        sync(player, progress);
    }

    @Override
    public void setLevel(ServerPlayer player, int level) {

        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(type);

        progress.setLevel(level);

        sync(player, progress);
    }

    private void sync(ServerPlayer player, MasteryProgress progress) {
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