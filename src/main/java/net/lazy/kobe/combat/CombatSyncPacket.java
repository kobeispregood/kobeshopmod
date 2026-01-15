package net.lazy.kobe.combat;

import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record CombatSyncPacket(
        int level,
        int xp,
        int[] claimedLevels
) implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("kobe", "combat_sync");

    public static final Type<CombatSyncPacket> TYPE = new Type<>(ID);

    // NeoForge 1.21 StreamCodec
    public static final StreamCodec<RegistryFriendlyByteBuf, CombatSyncPacket> CODEC =
            StreamCodec.of(
                    // encode
                    (buf, packet) -> {
                        buf.writeVarInt(packet.level);
                        buf.writeVarInt(packet.xp);

                        int[] arr = packet.claimedLevels == null ? new int[0] : packet.claimedLevels;
                        buf.writeVarInt(arr.length);
                        for (int lvl : arr) {
                            buf.writeVarInt(lvl);
                        }
                    },
                    // decode
                    buf -> {
                        int level = buf.readVarInt();
                        int xp = buf.readVarInt();

                        int len = buf.readVarInt();
                        int[] arr = new int[len];
                        for (int i = 0; i < len; i++) {
                            arr[i] = buf.readVarInt();
                        }

                        return new CombatSyncPacket(level, xp, arr);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /* ============================================================
     * SERVER → CLIENT SEND (FIXED)
     * ============================================================ */
    public static void send(ServerPlayer player, CombatData data) {

        int[] claimed = data.getClaimedRewards()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray();

        NetworkHandler.sendToPlayer(
                new CombatSyncPacket(
                        data.getLevel(),
                        data.getXpIntoLevel(),
                        claimed
                ),
                player
        );
    }

    /* ============================================================
     * CLIENT RECEIVE
     * ============================================================ */
    public static void handle(CombatSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() == null) return;

            CombatData data = ctx.player().getData(CombatAttachment.COMBAT);

            int oldLevel = data.getLevel();

            data.setLevel(packet.level());
            data.setXp(packet.xp());

            Set<Integer> claimed = new HashSet<>();
            for (int lvl : packet.claimedLevels()) {
                claimed.add(lvl);
            }
            data.setClaimedRewards(claimed);

            int newLevel = packet.level();

            // ===============================
            // LEVEL UP (CLIENT SIDE)
            // ===============================
            if (newLevel > oldLevel) {

                // chat
                ctx.player().sendSystemMessage(
                        Component.literal("[Combat] ")
                                .withStyle(ChatFormatting.RED)
                                .append(
                                        Component.literal("Level Up! ")
                                                .withStyle(ChatFormatting.GREEN)
                                )
                                .append(
                                        Component.literal("Level " + newLevel)
                                                .withStyle(ChatFormatting.YELLOW)
                                )
                );

                // toast
                CombatLevelUpToast.show(newLevel);
            }
        });
    }
}
