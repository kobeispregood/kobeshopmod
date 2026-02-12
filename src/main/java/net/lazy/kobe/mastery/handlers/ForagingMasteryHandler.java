package net.lazy.kobe.mastery.handlers;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryHandler;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public class ForagingMasteryHandler implements MasteryHandler {

    @Override
    public void reset(ServerPlayer player) {
        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.FORAGING);

        progress.setLevel(0);
        progress.setXp(0);
        progress.resetClaims();

        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        MasteryType.FORAGING,
                        progress.getLevel(),
                        progress.getXpIntoLevel(),
                        buildClaimedMask(progress)
                ),
                player
        );
    }

    @Override
    public void setLevel(ServerPlayer player, int level) {
        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.FORAGING);

        progress.setLevel(level);

        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        MasteryType.FORAGING,
                        progress.getLevel(),
                        progress.getXpIntoLevel(),
                        buildClaimedMask(progress)
                ),
                player
        );
    }

    private static int buildClaimedMask(MasteryProgress progress) {
        int mask = 0;
        for (int lvl : progress.getClaimedLevels()) {
            mask |= (1 << lvl);
        }
        return mask;
    }
}
