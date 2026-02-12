package net.lazy.kobe.mastery;

import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "kobe")
public class MasteryLoginSync {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        MasteryData data = player.getData(MasteryAttachment.MASTERY);

        // ✅ SEND ALL MASTERIES
        for (MasteryType type : MasteryType.values()) {

            MasteryProgress progress = data.getOrCreate(type);

            int claimedMask = 0;
            for (int lvl : progress.getClaimedLevels()) {
                claimedMask |= (1 << lvl);
            }

            player.connection.send(
                    new MasterySyncPacket(
                            type,
                            progress.getLevel(),
                            progress.getXp(),
                            claimedMask
                    )
            );
        }
    }
}
