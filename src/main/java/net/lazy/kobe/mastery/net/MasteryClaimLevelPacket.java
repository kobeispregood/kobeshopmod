package net.lazy.kobe.mastery.net;

import net.lazy.kobe.econ.MoneyAttachment;
import net.lazy.kobe.econ.MoneyData;
import net.lazy.kobe.econ.MoneySyncPacket;
import net.lazy.kobe.mastery.*;
import net.lazy.kobe.mastery.rewards.MasteryReward;
import net.lazy.kobe.mastery.rewards.MasteryRewardRegistry;
import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MasteryClaimLevelPacket(
        MasteryType masteryType,
        int level
) implements CustomPacketPayload {

    public static final Type<MasteryClaimLevelPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "mastery_claim_level"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, MasteryClaimLevelPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeEnum(pkt.masteryType());
                        buf.writeVarInt(pkt.level());
                    },
                    buf -> new MasteryClaimLevelPacket(
                            buf.readEnum(MasteryType.class),
                            buf.readVarInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MasteryClaimLevelPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            MasteryProgress progress = player
                    .getData(MasteryAttachment.MASTERY)
                    .getOrCreate(pkt.masteryType());

            if (progress.getLevel() < pkt.level()) return;
            if (progress.isClaimed(pkt.level())) return;

            progress.claim(pkt.level());

            MasteryReward reward =
                    MasteryRewardRegistry.get(pkt.masteryType(), pkt.level());

            int moneyGained = 0;

            if (reward != null) {

                if (reward.money > 0) {
                    MoneyData money = player.getData(MoneyAttachment.MONEY);
                    money.add(reward.money);
                    moneyGained += reward.money;
                }

                if (reward.xp > 0) {
                    progress.addXp(reward.xp);
                }

                for (ItemStack stack : reward.items) {
                    player.getInventory().add(stack.copy());
                }
            }

            // =============================
            // CHAT (COMBAT STYLE)
            // =============================
            ChatFormatting color = masteryColor(pkt.masteryType());

            player.sendSystemMessage(
                    Component.literal("[" + displayName(pkt.masteryType()) + "] ")
                            .withStyle(color)
                            .append(
                                    Component.literal("Rewards claimed for level " + pkt.level())
                                            .withStyle(ChatFormatting.GRAY)
                            )
            );

            if (reward != null && reward.perk != null && reward.perk.isValid()) {
                player.sendSystemMessage(
                        Component.literal("➤ Perk Unlocked: ")
                                .withStyle(ChatFormatting.GOLD)
                                .append(
                                        Component.literal(reward.perk.name())
                                                .withStyle(ChatFormatting.YELLOW)
                                )
                );

                if (!reward.perk.description().isEmpty()) {
                    player.sendSystemMessage(
                            Component.literal(reward.perk.description())
                                    .withStyle(ChatFormatting.GRAY)
                    );
                }
            }

            if (reward != null && reward.title != null && reward.title.isValid()) {
                player.sendSystemMessage(
                        Component.literal("➤ Title Unlocked: ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(
                                        Component.literal(reward.title.name())
                                                .withStyle(reward.title.getColor())
                                )
                );
            }

            if (moneyGained > 0) {
                player.sendSystemMessage(
                        Component.literal("+$" + moneyGained)
                                .withStyle(ChatFormatting.GREEN)
                );

                NetworkHandler.sendToPlayer(
                        new MoneySyncPacket(
                                player.getData(MoneyAttachment.MONEY).get()
                        ),
                        player
                );
            }

            NetworkHandler.sendToPlayer(
                    new MasterySyncPacket(
                            pkt.masteryType(),
                            progress.getLevel(),
                            progress.getXpIntoLevel(),
                            buildClaimedMask(progress)
                    ),
                    player
            );
        });
    }

    private static long buildClaimedMask(MasteryProgress progress) {
        long mask = 0L;

        for (int lvl : progress.getClaimedLevels()) {
            mask |= (1L << (lvl - 1)); // ✅ LONG + correct shift
        }

        return mask;
    }


    private static ChatFormatting masteryColor(MasteryType type) {
        return switch (type) {
            case COMBAT -> ChatFormatting.RED;
            case MINING -> ChatFormatting.GREEN;
            case FARMING -> ChatFormatting.YELLOW;
            case FISHING -> ChatFormatting.AQUA;
            case FORAGING -> ChatFormatting.DARK_GREEN;
            case ALCHEMY -> ChatFormatting.LIGHT_PURPLE;
            case ENCHANTING -> ChatFormatting.BLUE;
            default -> ChatFormatting.GRAY;
        };
    }

    private static String displayName(MasteryType type) {
        String s = type.name().toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
