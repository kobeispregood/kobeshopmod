package net.lazy.kobe.network;

import net.lazy.kobe.econ.MoneySyncPacket;
import net.lazy.kobe.farming.*;
import net.lazy.kobe.mastery.net.*;

import net.lazy.kobe.oregen.network.OreGenUpgradePacket;

import net.lazy.kobe.shop.net.SyncShopDataPacketHandler;
import net.lazy.kobe.shop.net.SyncShopDataPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

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

        registrar.playToServer(
                OpenSkillsPacket.TYPE,
                OpenSkillsPacket.CODEC,
                OpenSkillsPacket::handle
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
}


