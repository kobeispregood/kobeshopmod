package net.lazy.kobe.mastery.handlers;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryHandler;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public class AlchemyMasteryHandler implements MasteryHandler {

    @Override
    public void reset(ServerPlayer player) {

        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.ALCHEMY);

        // Full reset
        progress.setLevel(0);
        progress.setXp(0);
        progress.resetClaims();

        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        MasteryType.ALCHEMY,
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
                .getOrCreate(MasteryType.ALCHEMY);

        // Unlock only
        progress.setLevel(level);

        // ❌ do NOT touch claimed levels here

        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        MasteryType.ALCHEMY,
                        progress.getLevel(),
                        progress.getXpIntoLevel(),
                        buildClaimedMask(progress) // unchanged
                ),
                player
        );
    }

    // =============================================================
    // CLAIMED LEVELS → BITMASK
    // =============================================================
    private static int buildClaimedMask(MasteryProgress progress) {
        int mask = 0;
        for (int lvl : progress.getClaimedLevels()) {
            mask |= (1 << lvl);
        }
        return mask;
    }
}
