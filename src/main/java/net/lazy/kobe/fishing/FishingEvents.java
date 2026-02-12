package net.lazy.kobe.fishing;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXpCentral;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

@EventBusSubscriber(modid = "kobe")
public class FishingEvents {

    private static final int BASE_FISH_XP = 15;

    @SubscribeEvent
    public static void onFishCaught(ItemFishedEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (event.getDrops().isEmpty()) return;

        int xp = BASE_FISH_XP + (event.getDrops().size() * 5);

        if (xp <= 0) return;

        // 🚨 DEFER XP UNTIL NEXT TICK (CRITICAL)
        player.server.execute(() -> {
            MasteryXpCentral.addXp(
                    player,
                    MasteryType.FISHING,
                    xp
            );
        });
    }
}
