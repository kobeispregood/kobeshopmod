package net.lazy.kobe.foraging;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXpCentral;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class ForagingEvents {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {

        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        if (!event.getState().is(BlockTags.LOGS)) return;

        // ✅ CORRECT XP PATH
        MasteryXpCentral.addXp(
                player,
                MasteryType.FORAGING,
                5
        );
    }
}
