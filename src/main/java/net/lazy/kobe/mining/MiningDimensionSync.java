package net.lazy.kobe.mining;

import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class MiningDimensionSync {

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MiningData data = player.getData(MiningAttachment.MINING);

        // 🔁 Sync XP
        NetworkHandler.sendToPlayer(
                new MiningSyncPacket(data.getXp()),
                player
        );

        // 🔁 Sync claimed levels
        NetworkHandler.sendToPlayer(
                new MiningClaimSyncPacket(data.getClaimedLevels()),
                player
        );
    }
}
