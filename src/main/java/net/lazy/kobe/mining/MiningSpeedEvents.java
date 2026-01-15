package net.lazy.kobe.mining;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;

public class MiningSpeedEvents {

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        int masteryLevel = player.getData(MiningAttachment.MINING).getLevel();
        float multiplier = MiningPerks.getMiningSpeedMultiplier(masteryLevel);

        if (multiplier > 1.0f) {
            event.setNewSpeed(event.getOriginalSpeed() * multiplier);
        }
    }
}
