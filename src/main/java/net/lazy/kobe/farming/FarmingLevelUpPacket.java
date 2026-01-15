package net.lazy.kobe.farming;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FarmingLevelUpPacket(int level) implements CustomPacketPayload {

    public static final Type<FarmingLevelUpPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "farming_level_up"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmingLevelUpPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeInt(pkt.level()),
                    buf -> new FarmingLevelUpPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // ✅ EXACTLY MATCHES MINING
    public static void handle(FarmingLevelUpPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // 🔔 Ding (same category + timing as mining)
            mc.player.playSound(
                    SoundEvents.PLAYER_LEVELUP,
                    0.8f,
                    1.2f
            );

            mc.player.sendSystemMessage(
                    Component.literal("[Farming] ")
                            .withStyle(net.minecraft.ChatFormatting.DARK_GREEN)
                            .append(
                                    Component.literal("Level Up! ")
                                            .withStyle(net.minecraft.ChatFormatting.GREEN)
                            )
                            .append(
                                    Component.literal("Level " + pkt.level())
                                            .withStyle(net.minecraft.ChatFormatting.YELLOW)
                            )
            );

            // ✨ Toast
            mc.getToasts().addToast(
                    new FarmingLevelUpToast(pkt.level())
            );
        });
    }
}
