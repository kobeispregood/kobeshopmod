package net.lazy.kobe.world;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class HubEvents {

    private static final BlockPos HUB = new BlockPos(-72, 4, 0);

    @SubscribeEvent
    public void onFirstJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Skip if they already have an island
        if (player.getPersistentData().getBoolean("has_island")) return;

        // HUB is in the overworld
        ServerLevel hubWorld = player.server.overworld();
        if (hubWorld == null) return;
        
    }
}
