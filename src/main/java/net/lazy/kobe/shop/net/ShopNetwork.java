package net.lazy.kobe.shop.net;

import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ShopNetwork {

    public static void syncToAll(MinecraftServer server) {
        SyncShopDataPacket packet = SyncShopDataPacket.fromServerState();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkHandler.sendToPlayer(packet, player); // ✅ CORRECT ORDER
        }
    }

    public static void syncToPlayer(ServerPlayer player) {
        NetworkHandler.sendToPlayer(
                SyncShopDataPacket.fromServerState(),
                player
        );
    }
}
