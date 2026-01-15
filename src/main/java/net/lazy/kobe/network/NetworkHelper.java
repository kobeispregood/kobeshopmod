package net.lazy.kobe.network;

import net.minecraft.server.level.ServerPlayer;
import net.lazy.kobe.shop.net.SyncShopDataPacket;

public class NetworkHelper {

    public static void openShop(ServerPlayer player) {

        // 🔑 1️⃣ Sync shop data FIRST
        NetworkHandler.sendToPlayer(
                SyncShopDataPacket.fromServerState(),
                player
        );

        // 🔑 2️⃣ THEN open the shop GUI
        NetworkHandler.sendToPlayer(
                new OpenShopPacket(),
                player
        );
    }
}
