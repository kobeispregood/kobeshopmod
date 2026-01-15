package net.lazy.kobe.farming;

import net.lazy.kobe.econ.MoneyAttachment;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FarmingClaimLevelPacket(int level) implements CustomPacketPayload {

    public static final Type<FarmingClaimLevelPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "farming_claim_level"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmingClaimLevelPacket> STREAM_CODEC =
            StreamCodec.of(
                    FarmingClaimLevelPacket::write,
                    FarmingClaimLevelPacket::read
            );

    public static void write(RegistryFriendlyByteBuf buf, FarmingClaimLevelPacket pkt) {
        buf.writeInt(pkt.level);
    }

    public static FarmingClaimLevelPacket read(RegistryFriendlyByteBuf buf) {
        return new FarmingClaimLevelPacket(buf.readInt());
    }

    public static void handle(FarmingClaimLevelPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            FarmingData data = player.getData(FarmingAttachment.FARMING);

            if (data.getLevel() < msg.level()) return;
            if (data.isClaimed(msg.level())) return;

            // =========================================================
            // 🎁 GIVE JSON REWARDS (EXACTLY LIKE MINING)
            // =========================================================
            FarmingLevelReward reward =
                    FarmingRewardManager.getReward(msg.level());

            if (reward != null) {

                // 💰 MONEY — use the SAME method Mining uses
                if (reward.money() > 0) {

                    player.getData(MoneyAttachment.MONEY)
                            .add(reward.money());

                    // 💬 Chat message (same pattern as Mining)
                    player.sendSystemMessage(
                            Component.literal("[Farming] ")
                                    .withStyle(ChatFormatting.GREEN)
                                    .append(
                                            Component.literal("+$" + reward.money())
                                                    .withStyle(ChatFormatting.GOLD)
                                    )
                    );
                }

                // 📦 ITEMS
                for (var item : reward.items()) {
                    player.getInventory().add(
                            new ItemStack(
                                    BuiltInRegistries.ITEM.get(item.item()),
                                    item.count()
                            )
                    );
                }
            }


            // =========================================================
            // ✅ MARK CLAIMED
            // =========================================================
            data.claimLevel(msg.level());

            // =========================================================
            // 🔔 CLAIM SYNC ONLY (NO FULL FARMING SYNC)
            // =========================================================
            NetworkHandler.sendToPlayer(
                    new FarmingClaimSyncPacket(data.getClaimedLevels()),
                    player
            );

            // 🚫 DO NOT call syncFarming() here
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
