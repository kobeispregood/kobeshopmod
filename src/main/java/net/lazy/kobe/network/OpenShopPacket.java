package net.lazy.kobe.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import net.lazy.kobe.client.CategoryScreen;

public record OpenShopPacket() implements CustomPacketPayload {

    public static final Type<OpenShopPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "open_shop"));

    public OpenShopPacket(FriendlyByteBuf buf) {
        this();
    }

    // REQUIRED IN ALL NeoForge PACKETS
    public void write(FriendlyByteBuf buf) {
        // no fields to write
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenShopPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new CategoryScreen());
        });
    }
}