package net.lazy.kobe.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record MiningClaimSyncPacket(Set<Integer> claimed) implements CustomPacketPayload {

    public static final Type<MiningClaimSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "mining_claim_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MiningClaimSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeVarInt(pkt.claimed.size());
                        for (int lvl : pkt.claimed) buf.writeVarInt(lvl);
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        Set<Integer> set = new HashSet<>();
                        for (int i = 0; i < size; i++) set.add(buf.readVarInt());
                        return new MiningClaimSyncPacket(set);
                    }
            );

    @Override
    public Type<MiningClaimSyncPacket> type() {
        return TYPE;
    }

    public static void handle(MiningClaimSyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            MiningData data = mc.player.getData(MiningAttachment.MINING);

            // detect if any new claim exists BEFORE we replace set
            boolean newClaim = false;
            for (int lvl : pkt.claimed) {
                if (!data.isClaimed(lvl)) {
                    newClaim = true;
                    break;
                }
            }

            // apply server truth
            data.setClaimedLevels(pkt.claimed);

            // sound only when something newly claimed
            if (newClaim) {
                mc.player.playSound(
                        net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                        0.6f,
                        1.8f
                );
            }
        });
    }
}
