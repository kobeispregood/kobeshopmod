package net.lazy.kobe.network;

import net.lazy.kobe.econ.MoneyAttachment;
import net.lazy.kobe.econ.MoneySyncPacket;
import net.lazy.kobe.shop.PriceEntry;
import net.lazy.kobe.shop.ShopData;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BuyPacket(String key, int amount) implements CustomPacketPayload {

    public static final Type<BuyPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "buy_packet"));

    // -------------------------
    // DECODE
    // -------------------------
    public BuyPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readVarInt());
    }

    // -------------------------
    // ENCODE
    // -------------------------
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(key);
        buf.writeVarInt(amount);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // -------------------------
    // HANDLE ON SERVER
    // -------------------------
    public static void handle(BuyPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();
            if (player == null) return;

            PriceEntry price = ShopData.PRICE_BY_KEY.get(msg.key());
            if (price == null || price.buy() < 0) {
                player.sendSystemMessage(Component.literal("§cYou cannot buy this item."));
                return;
            }

            int cost = (int) price.buy() * msg.amount();

            var money = player.getData(MoneyAttachment.MONEY);
            if (money.get() < cost) {
                player.sendSystemMessage(Component.literal("§cNot enough money!"));
                return;
            }

            // Charge player
            money.remove(cost);

            // ⭐ BUILD THE CORRECT ITEM FROM MATERIAL STRING
            ItemStack toGive = ShopData.buildFromKey(msg.key());
            toGive.setCount(msg.amount());
            System.out.println("[DEBUG] BuyPacket giving key: " + msg.key());
            System.out.println("[DEBUG] ItemStack BEFORE setCount = " + toGive);
            System.out.println("[DEBUG] NBT inside ItemStack = " + toGive.get(DataComponents.BLOCK_ENTITY_DATA));

            if (!player.getInventory().add(toGive)) {
                player.drop(toGive, false);
                System.out.println("[DEBUG] Player received item. Final stack = " + toGive);
            }

            PacketDistributor.sendToPlayer(player, new MoneySyncPacket(money.get()));

            player.sendSystemMessage(
                    Component.literal("§aBought " + msg.amount() + "x " + msg.key() + " §7for §6$" + cost)
            );
        });
    }
}