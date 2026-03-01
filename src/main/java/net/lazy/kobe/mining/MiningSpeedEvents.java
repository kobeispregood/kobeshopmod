package net.lazy.kobe.mining;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class MiningSpeedEvents {

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {

        Player player = event.getEntity();

        int masteryLevel = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.MINING)
                .getLevel();

        float multiplier = MiningPerks.getMiningSpeedMultiplier(masteryLevel);

        if (multiplier > 1.0f) {
            event.setNewSpeed(event.getOriginalSpeed() * multiplier);
        }
    }
}