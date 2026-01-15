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

public record CombatClaimLevelPacket(int level) implements CustomPacketPayload {

    public static final Type<CombatClaimLevelPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "combat_claim_level"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CombatClaimLevelPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeInt(pkt.level),
                    buf -> new CombatClaimLevelPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CombatClaimLevelPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            CombatData data = player.getData(CombatAttachment.COMBAT);

            if (data.getLevel() < msg.level()) return;
            if (data.isRewardClaimed(msg.level())) return;

            // =========================================================
            // 🎁 GIVE JSON REWARDS
            // =========================================================
            CombatLevelReward reward =
                    CombatRewardManager.getReward(msg.level());

            if (reward != null) {

                // 💰 MONEY
                if (reward.money() > 0) {
                    player.getData(MoneyAttachment.MONEY)
                            .add(reward.money());

                    // Chat message (same feel as Mining/Farming)
                    player.sendSystemMessage(
                            Component.literal("[Combat] ")
                                    .withStyle(ChatFormatting.RED)
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
            data.claimReward(msg.level());

            // =========================================================
            // 🔔 CLAIM SYNC ONLY (NO FULL COMBAT SYNC)
            // =========================================================
            NetworkHandler.sendToPlayer(
                    new CombatClaimSyncPacket(data.getClaimedRewards()),
                    player
            );

            // 🚫 DO NOT call CombatAttachment.sync(player)
        });
    }
}
