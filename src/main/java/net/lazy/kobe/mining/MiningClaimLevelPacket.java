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

public record MiningClaimLevelPacket(int level) implements CustomPacketPayload {

    public static final Type<MiningClaimLevelPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "claim_mining_level"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, MiningClaimLevelPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeInt(pkt.level),
                    buf -> new MiningClaimLevelPacket(buf.readInt())
            );

    @Override
    public Type<MiningClaimLevelPacket> type() {
        return TYPE;
    }

    public static void handle(MiningClaimLevelPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            if (!(ctx.player() instanceof ServerPlayer player)) return;

            MiningData data = player.getData(MiningAttachment.MINING);

            // ===== VALIDATION =====
            if (data.getLevel() < pkt.level()) return;
            if (data.isClaimed(pkt.level())) return;

            // ===== CLAIM =====
            data.claimLevel(pkt.level());

            // ===== ITEMS =====
            List<ItemStack> rewards =
                    MiningRewardManager.createStacks(pkt.level());

            for (ItemStack stack : rewards) {
                player.getInventory().add(stack);
            }

            // ===== MONEY =====
            int money = MiningRewardManager.getMoney(pkt.level());
            if (money > 0) {
                MoneyAPI.add(player, money);

                player.sendSystemMessage(
                        Component.literal("+$" + money)
                                .withStyle(ChatFormatting.GREEN)
                );
            }

            // ===== SYNC CLAIMED =====
            NetworkHandler.sendToPlayer(
                    new MiningClaimSyncPacket(data.getClaimedLevels()),
                    player
            );
        });
    }
}
