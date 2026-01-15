package net.lazy.kobe.econ;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class MoneyAPI {

    public static int get(ServerPlayer player) {
        return player.getData(MoneyAttachment.MONEY).get();
    }

    public static void set(ServerPlayer player, int amount) {
        player.getData(MoneyAttachment.MONEY).set(amount);
        EconomyStorage.set(player.getUUID(), amount);
        sync(player);
    }

    public static void add(ServerPlayer player, int amount) {
        int newBal = get(player) + amount;
        set(player, newBal);
    }

    public static boolean tryRemove(ServerPlayer player, int amount) {
        int current = get(player);
        if (current < amount)
            return false;

        set(player, current - amount);
        return true;
    }

    public static void sync(ServerPlayer player) {
        int bal = get(player);
        PacketDistributor.sendToPlayer(player, new MoneySyncPacket(bal));
    }

    // Called on login
    public static void loadFromStorage(ServerPlayer player) {
        int stored = EconomyStorage.get(player.getUUID());
        set(player, stored); // sync too
    }

    // Called on logout
    public static void saveToStorage(ServerPlayer player) {
        int bal = get(player);
        EconomyStorage.set(player.getUUID(), bal);
    }
}
