package net.lazy.kobe.econ;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MoneySyncPacket(int balance) implements CustomPacketPayload {

    public static final Type<MoneySyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "money_sync"));

    public MoneySyncPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(balance);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MoneySyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getData(MoneyAttachment.MONEY).set(packet.balance());
            }
        });
    }
}