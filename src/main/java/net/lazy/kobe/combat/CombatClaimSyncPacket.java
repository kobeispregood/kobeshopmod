package net.lazy.kobe.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record CombatClaimSyncPacket(Set<Integer> claimed) implements CustomPacketPayload {

    public static final Type<CombatClaimSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "combat_claim_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CombatClaimSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeVarInt(pkt.claimed.size());
                        for (int lvl : pkt.claimed) buf.writeVarInt(lvl);
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        Set<Integer> set = new HashSet<>();
                        for (int i = 0; i < size; i++) set.add(buf.readVarInt());
                        return new CombatClaimSyncPacket(set);
                    }
            );

    @Override
    public Type<CombatClaimSyncPacket> type() {
        return TYPE;
    }

    public static void handle(CombatClaimSyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            CombatData data = mc.player.getData(CombatAttachment.COMBAT);

            boolean newClaim = false;
            for (int lvl : pkt.claimed) {
                if (!data.isRewardClaimed(lvl)) {
                    newClaim = true;
                    break;
                }
            }

            // apply server truth
            data.setClaimedRewards(pkt.claimed);

            // 🔔 SAME CLAIM DING AS MINING / FARMING
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
