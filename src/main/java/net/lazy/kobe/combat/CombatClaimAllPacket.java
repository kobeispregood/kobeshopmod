package net.lazy.kobe.combat;

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

public record CombatClaimAllPacket() implements CustomPacketPayload {

    public static final Type<CombatClaimAllPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "combat_claim_all"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, CombatClaimAllPacket> STREAM_CODEC =
            StreamCodec.unit(new CombatClaimAllPacket());

    @Override
    public Type<CombatClaimAllPacket> type() {
        return TYPE;
    }

    public static void handle(CombatClaimAllPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            CombatData data = player.getData(CombatAttachment.COMBAT);
            int playerLevel = data.getLevel();

            int totalMoney = 0;
            boolean claimedAny = false;

            for (int lvl = 1; lvl <= playerLevel; lvl++) {
                if (data.isRewardClaimed(lvl)) continue;

                CombatLevelReward reward =
                        CombatRewardManager.getReward(lvl);

                data.claimReward(lvl);
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

            // ===== PAY MONEY ONCE (CLEAN UX) =====
            if (totalMoney > 0) {
                player.getData(MoneyAttachment.MONEY)
                        .add(totalMoney);

                player.sendSystemMessage(
                        Component.literal("[Combat] ")
                                .withStyle(ChatFormatting.RED)
                                .append(
                                        Component.literal("+$" + totalMoney)
                                                .withStyle(ChatFormatting.GOLD)
                                )
                );
            }

            // ===== SYNC CLAIMED REWARDS ONLY =====
            NetworkHandler.sendToPlayer(
                    new CombatClaimSyncPacket(data.getClaimedRewards()),
                    player
            );

            // 🚫 DO NOT send CombatSyncPacket here
        });
    }
}
