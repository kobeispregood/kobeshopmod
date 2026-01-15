package net.lazy.kobe.farming;

import net.lazy.kobe.econ.MoneyAttachment;
import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FarmingClaimAllPacket() implements CustomPacketPayload {

    public static final Type<FarmingClaimAllPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "farming_claim_all"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmingClaimAllPacket> STREAM_CODEC =
            StreamCodec.unit(new FarmingClaimAllPacket());

    @Override
    public Type<FarmingClaimAllPacket> type() {
        return TYPE;
    }

    public static void handle(FarmingClaimAllPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            FarmingData data = player.getData(FarmingAttachment.FARMING);
            int playerLevel = data.getLevel();

            int totalMoney = 0;
            boolean claimedAny = false;

            for (int lvl = 1; lvl <= playerLevel; lvl++) {
                if (data.isClaimed(lvl)) continue;

                FarmingLevelReward reward =
                        FarmingRewardManager.getReward(lvl);

                data.claimLevel(lvl);
                claimedAny = true;

                if (reward == null) continue;

                // ===== ITEMS =====
                for (var item : reward.items()) {
                    player.getInventory().add(
                            new ItemStack(
                                    BuiltInRegistries.ITEM.get(item.item()),
                                    item.count()
                            )
                    );
                }

                // ===== MONEY =====
                totalMoney += reward.money();
            }

            if (!claimedAny) return;

            // ===== PAY MONEY ONCE =====
            if (totalMoney > 0) {
                player.getData(MoneyAttachment.MONEY)
                        .add(totalMoney);

                player.sendSystemMessage(
                        Component.literal("[Farming] ")
                                .withStyle(ChatFormatting.GREEN)
                                .append(
                                        Component.literal("+$" + totalMoney)
                                                .withStyle(ChatFormatting.GOLD)
                                )
                );
            }

            // ===== SYNC CLAIMED LEVELS ONLY =====
            NetworkHandler.sendToPlayer(
                    new FarmingClaimSyncPacket(data.getClaimedLevels()),
                    player
            );

            // 🚫 DO NOT send FarmingSyncPacket here
        });
    }
}
