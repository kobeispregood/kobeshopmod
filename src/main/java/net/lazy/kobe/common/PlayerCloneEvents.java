package net.lazy.kobe.common;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mining.MiningAttachment;
import net.lazy.kobe.farming.FarmingAttachment;

import net.lazy.kobe.mastery.MasteryData;
import net.lazy.kobe.mining.MiningData;
import net.lazy.kobe.farming.FarmingData;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "kobe")
public final class PlayerCloneEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {

        if (!event.isWasDeath()) return;

        ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();
        ServerPlayer newPlayer = (ServerPlayer) event.getEntity();

        // =========================
        // MASTERY
        // =========================
        MasteryData oldMastery = oldPlayer.getData(MasteryAttachment.MASTERY);
        MasteryData newMastery = newPlayer.getData(MasteryAttachment.MASTERY);
        newMastery.copyFrom(oldMastery);

        // =========================
        // MINING
        // =========================
        MiningData oldMining = oldPlayer.getData(MiningAttachment.MINING);
        MiningData newMining = newPlayer.getData(MiningAttachment.MINING);
        newMining.copyFrom(oldMining);

        // =========================
        // FARMING
        // =========================
        FarmingData oldFarming = oldPlayer.getData(FarmingAttachment.FARMING);
        FarmingData newFarming = newPlayer.getData(FarmingAttachment.FARMING);
        newFarming.copyFrom(oldFarming);

    }
}
