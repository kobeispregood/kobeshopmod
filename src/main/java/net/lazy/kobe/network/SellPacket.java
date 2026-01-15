package net.lazy.kobe.network;

import net.lazy.kobe.econ.MoneyAttachment;
import net.lazy.kobe.econ.MoneySyncPacket;
import net.lazy.kobe.shop.PriceEntry;
import net.lazy.kobe.shop.ShopData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SellPacket(String key, int amount) implements CustomPacketPayload {

    public static final Type<SellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "sell_packet"));

    // -------------------------
    // DECODE
    // -------------------------
    public SellPacket(FriendlyByteBuf buf) {
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
    // SERVER HANDLER
    // -------------------------
    public static void handle(SellPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();
            if (player == null) return;

            PriceEntry price = ShopData.PRICE_BY_KEY.get(msg.key());
            if (price == null || price.sell() < 0) {
                player.sendSystemMessage(Component.literal("§cYou cannot sell this item."));
                return;
            }

            int needed = msg.amount();
            int removed = 0;

            // Remove matching items by KEY (spawner-safe)
            for (int i = 0; i < player.getInventory().getContainerSize() && removed < needed; i++) {

                ItemStack slot = player.getInventory().getItem(i);
                if (slot.isEmpty()) continue;

                String slotKey = ShopData.getKeyForStack(slot);
                if (!slotKey.equals(msg.key())) continue;

                int take = Math.min(slot.getCount(), needed - removed);
                slot.shrink(take);
                removed += take;
            }

            if (removed == 0) {
                player.sendSystemMessage(Component.literal("§cYou do not have that item."));
                return;
            }

            int payout = (int) price.sell() * removed;

            var money = player.getData(MoneyAttachment.MONEY);
            money.add(payout);

            PacketDistributor.sendToPlayer(player, new MoneySyncPacket(money.get()));

            player.sendSystemMessage(
                    Component.literal("§aSold " + removed + "x " + msg.key() + " §7for §6$" + payout)
            );
        });
    }
}