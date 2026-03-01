package net.lazy.kobe.farming.perk;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "kobe")
public class FarmingTrampleHandler {

    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.FARMING);

        if (FarmingPerks.noTrampleEnabled(progress)) {
            event.setCanceled(true);
        }
    }
}