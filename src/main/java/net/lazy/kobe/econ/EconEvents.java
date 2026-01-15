package net.lazy.kobe.econ;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public class EconEvents {

    @SubscribeEvent
    public static void onServerStart(ServerStartingEvent event) {
        EconomyStorage.init(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event) {
        EconomyStorage.save();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MoneyAPI.loadFromStorage(player);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MoneyAPI.saveToStorage(player);
        }
    }
}
