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

public record MasteryClaimAllPacket(
        MasteryType masteryType
) implements CustomPacketPayload {

    public static final Type<MasteryClaimAllPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "mastery_claim_all"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, MasteryClaimAllPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeEnum(pkt.masteryType()),
                    buf -> new MasteryClaimAllPacket(
                            buf.readEnum(MasteryType.class)
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MasteryClaimAllPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            MasteryProgress progress = player
                    .getData(MasteryAttachment.MASTERY)
                    .getOrCreate(pkt.masteryType());

            int max = progress.getLevel();

            int totalMoney = 0;
            int highestLevel = -1;
            MasteryReward lastReward = null;

            for (int lvl = 1; lvl <= max; lvl++) {
                if (progress.isClaimed(lvl)) continue;

                progress.claim(lvl);

                MasteryReward reward =
                        MasteryRewardRegistry.get(pkt.masteryType(), lvl);

                if (reward == null) continue;

                totalMoney += reward.money;
                highestLevel = lvl;
                lastReward = reward;

                if (reward.xp > 0) {
                    progress.addXp(reward.xp);
                }

                for (ItemStack stack : reward.items) {
                    player.getInventory().add(stack.copy());
                }
            }

            if (totalMoney > 0) {
                MoneyData money = player.getData(MoneyAttachment.MONEY);
                money.add(totalMoney);

                player.sendSystemMessage(
                        Component.literal("+$" + totalMoney)
                                .withStyle(ChatFormatting.GREEN)
                );

                NetworkHandler.sendToPlayer(
                        new MoneySyncPacket(money.get()),
                        player
                );
            }

            if (lastReward != null && highestLevel > 0) {
                ChatFormatting color = masteryColor(pkt.masteryType());

                player.sendSystemMessage(
                        Component.literal("[" + displayName(pkt.masteryType()) + "] ")
                                .withStyle(color)
                                .append(
                                        Component.literal("Level Up! Level " + highestLevel)
                                                .withStyle(ChatFormatting.YELLOW)
                                )
                );

                if (lastReward.perk != null && lastReward.perk.isValid()) {
                    player.sendSystemMessage(
                            Component.literal("➤ Perk Unlocked: ")
                                    .withStyle(ChatFormatting.GOLD)
                                    .append(
                                            Component.literal(lastReward.perk.name())
                                                    .withStyle(ChatFormatting.YELLOW)
                                    )
                    );

                    if (!lastReward.perk.description().isEmpty()) {
                        player.sendSystemMessage(
                                Component.literal(lastReward.perk.description())
                                        .withStyle(ChatFormatting.GRAY)
                        );
                    }
                }

                if (lastReward.title != null && lastReward.title.isValid()) {
                    player.sendSystemMessage(
                            Component.literal("➤ Title Unlocked: ")
                                    .withStyle(ChatFormatting.LIGHT_PURPLE)
                                    .append(
                                            Component.literal(lastReward.title.name())
                                                    .withStyle(lastReward.title.getColor())
                                    )
                    );
                }
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

    private static int buildClaimedMask(MasteryProgress progress) {
        int mask = 0;
        for (int lvl : progress.getClaimedLevels()) {
            mask |= (1 << (lvl - 1)); // ✅ FIX
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
