package net.lazy.kobe.oregen.network;

import net.lazy.kobe.shop.ShopEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OreGenUpgradePacket implements CustomPacketPayload {

    public static final OreGenUpgradePacket INSTANCE = new OreGenUpgradePacket();

    public static final Type<OreGenUpgradePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "oregen_upgrade"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OreGenUpgradePacket> CODEC =
            StreamCodec.unit(INSTANCE);

    private OreGenUpgradePacket() {}

    public static void handle(OreGenUpgradePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                ShopEvents.buyOreGenUpgrade(player);
                player.containerMenu.broadcastChanges();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
