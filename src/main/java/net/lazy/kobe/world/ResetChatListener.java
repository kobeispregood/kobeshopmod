package net.lazy.kobe.world;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

public class ResetChatListener {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String msg = event.getMessage().getString().trim();

        // Not waiting for reset confirmation → ignore
        if (!PlayerIslandStorage.isPendingReset(player))
            return;

        // Player typed 12345 → confirm reset
        if (msg.equals("12345")) {
            IslandCommands.confirmReset(player);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aIsland reset!"));
            event.setCanceled(true);
            return;
        }

        // Player typed something else → cancel reset
        PlayerIslandStorage.setPendingReset(player, false);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cIsland reset cancelled."));
    }
}
