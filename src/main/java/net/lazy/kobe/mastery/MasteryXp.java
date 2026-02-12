package net.lazy.kobe.mastery;

import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.mining.*;
import net.lazy.kobe.farming.*;
import net.lazy.kobe.combat.*;

import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public final class MasteryXp {

    private MasteryXp() {}

    /* ============================================================
     *  MINING
     * ============================================================ */
    public static void addMiningXp(ServerPlayer player, int amount) {
        if (amount <= 0) return;

        MiningData data = player.getData(MiningAttachment.MINING);

        int oldLevel = data.getLevel();
        data.addXp(amount);
        int newLevel = data.getLevel();

        // sync xp
        NetworkHandler.sendToPlayer(
                new MiningSyncPacket(data.getXp()),
                player
        );

        // level up
        if (newLevel > oldLevel) {
            NetworkHandler.sendToPlayer(
                    new MiningLevelUpPacket(newLevel),
                    player
            );

            var perks = MiningPerkUnlocks.getUnlockedPerks(oldLevel, newLevel);
            if (!perks.isEmpty()) {
                NetworkHandler.sendToPlayer(
                        new MiningPerkUnlockPacket(perks),
                        player
                );
            }
        }
    }

    /* ============================================================
     *  FARMING (STUB – SAFE TO ADD LATER)
     * ============================================================ */
    public static void addFarmingXp(ServerPlayer player, int amount) {
        if (amount <= 0) return;

        FarmingData data = player.getData(FarmingAttachment.FARMING);
        data.addXp(amount);

        NetworkHandler.sendToPlayer(
                new FarmingSyncPacket(
                        data.getXp(),
                        data.getClaimedLevels()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .toArray()
                ),
                player
        );
    }

    /* ============================================================
     *  COMBAT (STUB – DO NOT TOUCH YET)
     * ============================================================ */

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

        int oldLevel = progress.getLevel();

        // MasteryProgress uses int XP
        progress.addXp(Math.round(amount));

        int newLevel = progress.getLevel();

        // ------------------------------------------------
        // Build claimed reward bitmask
        // ------------------------------------------------
        int claimedMask = 0;
        for (int lvl : progress.getClaimedLevels()) {
            if (lvl >= 1 && lvl < 32) {
                claimedMask |= (1 << lvl);
            }
        }

        // ------------------------------------------------
        // SYNC TO CLIENT (THIS WAS MISSING)
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

        // (Optional) future hook:
        // if (newLevel > oldLevel) { perks / toast / particles }
    }
}
