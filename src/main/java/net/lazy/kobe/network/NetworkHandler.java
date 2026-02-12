package net.lazy.kobe.network;

import net.lazy.kobe.farming.FarmingLevelUpPacket;
import net.lazy.kobe.econ.MoneySyncPacket;
import net.lazy.kobe.farming.*;
import net.lazy.kobe.mastery.net.MasteryClaimAllPacket;
import net.lazy.kobe.mastery.net.MasteryClaimLevelPacket;
import net.lazy.kobe.mining.*;
import net.lazy.kobe.oregen.network.OreGenUpgradePacket;

import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.mastery.net.MasteryLevelUpPacket;

import net.lazy.kobe.shop.net.SyncShopDataPacketHandler;
import net.lazy.kobe.shop.net.SyncShopDataPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.lazy.kobe.combat.*;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NetworkHandler {

    /**
     * Registers ALL custom packets for this mod.
     * Automatically called from MOD event bus:
     *
     *   modEventBus.addListener(NetworkHandler::register);
     */
    public static void register(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar("1"); // protocol version

        // =============================================================
        //  CLIENT → SERVER
        // =============================================================

        registrar.playToServer(
                BuyPacket.TYPE,
                CustomPacketPayload.codec(BuyPacket::write, BuyPacket::new),
                BuyPacket::handle
        );

        registrar.playToServer(
                MiningClaimAllPacket.TYPE,
                MiningClaimAllPacket.STREAM_CODEC,
                MiningClaimAllPacket::handle
        );


        registrar.playToServer(
                MiningClaimLevelPacket.TYPE,
                MiningClaimLevelPacket.STREAM_CODEC,
                MiningClaimLevelPacket::handle
        );


        registrar.playToServer(
                FarmingClaimLevelPacket.TYPE,
                FarmingClaimLevelPacket.STREAM_CODEC,
                FarmingClaimLevelPacket::handle
        );

        registrar.playToServer(
                FarmingClaimAllPacket.TYPE,
                FarmingClaimAllPacket.STREAM_CODEC,
                FarmingClaimAllPacket::handle
        );

        registrar.playToServer(
                CrateOpenPacket.TYPE,
                CustomPacketPayload.codec(CrateOpenPacket::write, CrateOpenPacket::new),
                CrateOpenPacket::handle
        );

        registrar.playToServer(
                OreGenUpgradePacket.TYPE,
                OreGenUpgradePacket.CODEC,
                OreGenUpgradePacket::handle
        );

        registrar.playToServer(
                SellPacket.TYPE,
                CustomPacketPayload.codec(SellPacket::write, SellPacket::new),
                SellPacket::handle
        );

        registrar.playToServer(
                MasteryClaimLevelPacket.TYPE,
                MasteryClaimLevelPacket.STREAM_CODEC,
                MasteryClaimLevelPacket::handle
        );

        registrar.playToServer(
                MasteryClaimAllPacket.TYPE,
                MasteryClaimAllPacket.STREAM_CODEC,
                MasteryClaimAllPacket::handle
        );

        // =============================================================
        //  SERVER → CLIENT
        // =============================================================
        registrar.playToClient(
                MasterySyncPacket.TYPE,
                MasterySyncPacket.CODEC,
                MasterySyncPacket::handle
        );

        registrar.playToClient(
                MasteryLevelUpPacket.TYPE,
                MasteryLevelUpPacket.CODEC,
                MasteryLevelUpPacket::handle
        );

        registrar.playToClient(
                FarmingClaimSyncPacket.TYPE,
                FarmingClaimSyncPacket.STREAM_CODEC,
                FarmingClaimSyncPacket::handle
        );

        registrar.playToClient(
                MiningSyncPacket.TYPE,
                MiningSyncPacket.CODEC,
                MiningSyncPacket::handle
        );

        registrar.playToClient(
                MiningLevelUpPacket.TYPE,
                MiningLevelUpPacket.STREAM_CODEC,
                MiningLevelUpPacket::handle
        );

        registrar.playToClient(
                MiningClaimSyncPacket.TYPE,
                MiningClaimSyncPacket.STREAM_CODEC,
                MiningClaimSyncPacket::handle
        );

        registrar.playToClient(
                MiningPerkUnlockPacket.TYPE,
                MiningPerkUnlockPacket.STREAM_CODEC,
                MiningPerkUnlockPacket::handle
        );

        registrar.playToClient(
                FarmingLevelUpPacket.TYPE,
                FarmingLevelUpPacket.STREAM_CODEC,
                FarmingLevelUpPacket::handle
        );


        registrar.playToClient(
                FarmingSyncPacket.TYPE,
                FarmingSyncPacket.CODEC,
                FarmingSyncPacket::handle
        );

        registrar.playToClient(
                CrateRevealPacket.TYPE,
                CustomPacketPayload.codec(CrateRevealPacket::write, CrateRevealPacket::new),
                CrateRevealPacket::handle
        );

        registrar.playToClient(
                OpenShopPacket.TYPE,
                CustomPacketPayload.codec(OpenShopPacket::write, OpenShopPacket::new),
                OpenShopPacket::handle
        );

        registrar.playToClient(
                MoneySyncPacket.TYPE,
                CustomPacketPayload.codec(MoneySyncPacket::write, MoneySyncPacket::new),
                MoneySyncPacket::handle
        );
        registrar.playToClient(
                SyncShopDataPacket.TYPE,
                SyncShopDataPacket.STREAM_CODEC,
                SyncShopDataPacketHandler::handle
        );
    }

    // =============================================================
    //  CLIENT → SERVER (UTILITY)
    // =============================================================
    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    // =============================================================
    //  SERVER → CLIENT (UTILITY)
    // =============================================================
    public static void sendToPlayer(CustomPacketPayload payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    // =============================================================
    //  FARMING SYNC (SERVER → CLIENT)  ⭐ FIXED ⭐
    // =============================================================
    public static void syncFarming(ServerPlayer player, FarmingData data) {
        int[] claimed = data.getClaimedLevels()
                .stream()
                .mapToInt(i -> i)
                .toArray();

        sendToPlayer(
                new FarmingSyncPacket(data.getXp(), claimed),
                player
        );
    }
}

