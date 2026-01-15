package net.lazy.kobe.mining;

import net.lazy.kobe.econ.MoneyAPI;
import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record MiningClaimAllPacket() implements CustomPacketPayload {

    public static final Type<MiningClaimAllPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "claim_all_mining_levels"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, MiningClaimAllPacket> STREAM_CODEC =
            StreamCodec.unit(new MiningClaimAllPacket());

    @Override
    public Type<MiningClaimAllPacket> type() {
        return TYPE;
    }

    public static void handle(MiningClaimAllPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            MiningData data = player.getData(MiningAttachment.MINING);
            int playerLevel = data.getLevel();

            int totalMoney = 0;
            boolean claimedAny = false;

            for (int lvl = 1; lvl <= playerLevel; lvl++) {
                if (data.isClaimed(lvl)) continue;

                data.claimLevel(lvl);
                claimedAny = true;

                // ===== ITEMS =====
                List<ItemStack> rewards =
                        MiningRewardManager.createStacks(lvl);

                for (ItemStack stack : rewards) {
                    player.getInventory().add(stack);
                }

                // ===== MONEY =====
                totalMoney += MiningRewardManager.getMoney(lvl);
            }

            if (!claimedAny) return;

            // Pay money once (clean UX)
            if (totalMoney > 0) {
                MoneyAPI.add(player, totalMoney);

                player.sendSystemMessage(
                        Component.literal("+$" + totalMoney)
                                .withStyle(ChatFormatting.GREEN)
                );
            }

            // ===== SYNC =====
            NetworkHandler.sendToPlayer(
                    new MiningClaimSyncPacket(data.getClaimedLevels()),
                    player
            );

            NetworkHandler.sendToPlayer(
                    new MiningSyncPacket(data.getXp()),
                    player
            );
        });
    }
}
