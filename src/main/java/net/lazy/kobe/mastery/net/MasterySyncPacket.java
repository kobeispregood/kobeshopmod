package net.lazy.kobe.mastery.net;

import net.lazy.kobe.mastery.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MasterySyncPacket(
        MasteryType masteryType,
        int level,
        int xpIntoLevel,
        List<Integer> claimedLevels   // ✅ NOW LIST
) implements CustomPacketPayload {

    public static final Type<MasterySyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "mastery_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MasterySyncPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeEnum(pkt.masteryType());
                        buf.writeVarInt(pkt.level());
                        buf.writeVarInt(pkt.xpIntoLevel());

                        buf.writeVarInt(pkt.claimedLevels().size());
                        for (int lvl : pkt.claimedLevels()) {
                            buf.writeVarInt(lvl);
                        }
                    },
                    buf -> {
                        MasteryType type = buf.readEnum(MasteryType.class);
                        int level = buf.readVarInt();
                        int xp = buf.readVarInt();

                        int size = buf.readVarInt();
                        List<Integer> claimed = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            claimed.add(buf.readVarInt());
                        }

                        return new MasterySyncPacket(type, level, xp, claimed);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // =============================================================
    // CLIENT APPLY + CLAIM SOUND
    // =============================================================
    public static void handle(MasterySyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            var player = Minecraft.getInstance().player;
            if (player == null) return;

            MasteryData data = player.getData(MasteryAttachment.MASTERY);
            MasteryProgress progress = data.getOrCreate(pkt.masteryType());

            // --------------------------------------------
            // Capture OLD claim state
            // --------------------------------------------
            Set<Integer> oldClaims = new HashSet<>(progress.getClaimedLevels());

            // --------------------------------------------
            // Apply NEW data
            // --------------------------------------------
            progress.setLevel(pkt.level());
            progress.setXp(pkt.xpIntoLevel());

            progress.resetClaims();
            progress.getClaimedLevels().addAll(pkt.claimedLevels());

            // --------------------------------------------
            // Play sound ONLY if something NEW was claimed
            // --------------------------------------------
            for (int lvl : pkt.claimedLevels()) {
                if (!oldClaims.contains(lvl)) {
                    player.playSound(
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            0.7f,
                            1.0f
                    );
                    break;
                }
            }
        });
    }
}