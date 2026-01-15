package net.lazy.kobe.farming.perk;

import net.lazy.kobe.farming.FarmingAttachment;
import net.lazy.kobe.farming.FarmingData;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "kobe") // 🔑 automatic registration
public class FarmingTrampleHandler {

    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        FarmingData data = player.getData(FarmingAttachment.FARMING);
        if (data == null) return;

        if (FarmingPerks.noTrampleEnabled(data)) {
            event.setCanceled(true);
        }
    }
}
