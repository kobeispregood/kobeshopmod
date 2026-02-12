package net.lazy.kobe.mastery.handlers;

import net.lazy.kobe.mastery.MasteryHandler;
import net.lazy.kobe.mining.MiningAttachment;
import net.lazy.kobe.mining.MiningData;
import net.lazy.kobe.mining.MiningSyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public class MiningMasteryHandler implements MasteryHandler {

    @Override
    public void reset(ServerPlayer player) {
        MiningData data = player.getData(MiningAttachment.MINING);
        data.reset();

        // ✅ SYNC TO CLIENT
        NetworkHandler.sendToPlayer(
                new MiningSyncPacket(data.getXp()),
                player
        );
    }

    @Override
    public void setLevel(ServerPlayer player, int level) {
        MiningData data = player.getData(MiningAttachment.MINING);
        data.setLevel(level);

        // ✅ SYNC TO CLIENT
        NetworkHandler.sendToPlayer(
                new MiningSyncPacket(data.getXp()),
                player
        );
    }
}
