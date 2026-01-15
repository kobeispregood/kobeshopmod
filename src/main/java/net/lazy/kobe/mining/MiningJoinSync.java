package net.lazy.kobe.mining;

import net.lazy.kobe.network.NetworkHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class MiningJoinSync {

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        syncAll(player);
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        syncAll(player);
    }

    private static void syncAll(ServerPlayer player) {
        MiningData data = player.getData(MiningAttachment.MINING);

        // xp sync
        NetworkHandler.sendToPlayer(new MiningSyncPacket(data.getXp()), player);

        // claims sync
        NetworkHandler.sendToPlayer(new MiningClaimSyncPacket(new java.util.HashSet<>(data.getClaimedLevels())), player);
    }
}
