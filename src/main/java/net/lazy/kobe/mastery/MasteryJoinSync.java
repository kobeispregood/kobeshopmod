package net.lazy.kobe.mastery;

import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

@EventBusSubscriber(modid = "kobe")
public class MasteryJoinSync {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MasteryData data = player.getData(MasteryAttachment.MASTERY);

        for (MasteryType type : MasteryType.values()) {

            MasteryProgress progress = data.getOrCreate(type);

            NetworkHandler.sendToPlayer(
                    new MasterySyncPacket(
                            type,
                            progress.getLevel(),
                            progress.getXpIntoLevel(),
                            new ArrayList<>(progress.getClaimedLevels())
                    ),
                    player
            );
        }
    }
}