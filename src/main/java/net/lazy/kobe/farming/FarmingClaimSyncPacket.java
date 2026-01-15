package net.lazy.kobe.farming;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record FarmingClaimSyncPacket(Set<Integer> claimed) implements CustomPacketPayload {

    public static final Type<FarmingClaimSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "farming_claim_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmingClaimSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeVarInt(pkt.claimed.size());
                        for (int lvl : pkt.claimed) buf.writeVarInt(lvl);
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        Set<Integer> set = new HashSet<>();
                        for (int i = 0; i < size; i++) set.add(buf.readVarInt());
                        return new FarmingClaimSyncPacket(set);
                    }
            );

    @Override
    public Type<FarmingClaimSyncPacket> type() {
        return TYPE;
    }

    public static void handle(FarmingClaimSyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            FarmingData data = mc.player.getData(FarmingAttachment.FARMING);

            boolean newClaim = false;
            for (int lvl : pkt.claimed) {
                if (!data.isClaimed(lvl)) {
                    newClaim = true;
                    break;
                }
            }

            // apply server truth
            data.setClaimedLevels(pkt.claimed);

            // 🔔 SAME DING AS MINING
            if (newClaim) {
                mc.player.playSound(
                        SoundEvents.PLAYER_LEVELUP,
                        0.6f,
                        1.8f
                );
            }
        });
    }
}
