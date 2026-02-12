package net.lazy.kobe.mastery.net;

import net.lazy.kobe.mastery.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MasterySyncPacket(
        MasteryType masteryType,
        int level,
        int xpIntoLevel,
        long claimedMask   // ✅ LONG
) implements CustomPacketPayload {

    public static final Type<MasterySyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "mastery_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MasterySyncPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeEnum(pkt.masteryType());
                        buf.writeVarInt(pkt.level());
                        buf.writeVarInt(pkt.xpIntoLevel());
                        buf.writeLong(pkt.claimedMask()); // ✅ writeLong
                    },
                    buf -> new MasterySyncPacket(
                            buf.readEnum(MasteryType.class),
                            buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readLong() // ✅ readLong
                    )
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
            long oldMask = 0L;
            for (int lvl : progress.getClaimedLevels()) {
                oldMask |= (1L << (lvl - 1));
            }

            // --------------------------------------------
            // Apply NEW data
            // --------------------------------------------
            progress.setLevel(pkt.level());
            progress.setXp(pkt.xpIntoLevel());
            progress.resetClaims();

            long newMask = pkt.claimedMask();

            // 🔧 LEGACY FIX:
            // If level 1 is missing but level 2 bit is set, shift it down
            if ((newMask & 1L) == 0L && (newMask & (1L << 1)) != 0L) {
                newMask |= 1L;
            }

            for (int lvl = 1; lvl <= 45; lvl++) {
                if ((newMask & (1L << (lvl - 1))) != 0L) {
                    progress.claim(lvl);
                }
            }

            // --------------------------------------------
            // Play sound ONLY if something NEW was claimed
            // --------------------------------------------
            if ((newMask & ~oldMask) != 0) {
                player.playSound(
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        0.7f,
                        1.0f
                );
            }
        });
    }
}