package net.lazy.kobe.mining;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class MiningCloneHandler {

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        var oldData = event.getOriginal().getData(MiningAttachment.MINING);
        var newData = event.getEntity().getData(MiningAttachment.MINING);

        // Copy XP
        newData.deserializeNBT(
                event.getEntity().level().registryAccess(),
                oldData.serializeNBT(event.getEntity().level().registryAccess())
        );
    }
}
