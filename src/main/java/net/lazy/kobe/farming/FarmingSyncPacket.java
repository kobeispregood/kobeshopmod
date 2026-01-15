package net.lazy.kobe.farming;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record FarmingSyncPacket(
        int xp,
        int[] claimedLevels
) implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("kobe", "farming_sync");

    public static final Type<FarmingSyncPacket> TYPE = new Type<>(ID);

    // NeoForge 1.21 StreamCodec
    public static final StreamCodec<RegistryFriendlyByteBuf, FarmingSyncPacket> CODEC =
            StreamCodec.of(
                    // encode
                    (buf, packet) -> {
                        buf.writeVarInt(packet.xp);

                        int[] arr = packet.claimedLevels == null ? new int[0] : packet.claimedLevels;
                        buf.writeVarInt(arr.length);
                        for (int lvl : arr) {
                            buf.writeVarInt(lvl);
                        }
                    },
                    // decode
                    buf -> {
                        int xp = buf.readVarInt();

                        int len = buf.readVarInt();
                        int[] arr = new int[len];
                        for (int i = 0; i < len; i++) {
                            arr[i] = buf.readVarInt();
                        }

                        return new FarmingSyncPacket(xp, arr);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // CLIENT
    public static void handle(FarmingSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() == null) return;

            FarmingData data = ctx.player().getData(FarmingAttachment.FARMING);

            // client-safe setters
            data.setXp(packet.xp());

            Set<Integer> claimed = new HashSet<>();
            for (int lvl : packet.claimedLevels()) {
                claimed.add(lvl);
            }
            data.setClaimedLevels(claimed);
        });
    }
}
