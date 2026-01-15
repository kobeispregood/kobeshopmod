package net.lazy.kobe.farming;

import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class FarmingDimensionSync {

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        FarmingData data = player.getData(FarmingAttachment.FARMING);
        NetworkHandler.syncFarming(player, data);
    }
}
