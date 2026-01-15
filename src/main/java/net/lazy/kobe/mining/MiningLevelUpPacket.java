package net.lazy.kobe.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;

public record MiningLevelUpPacket(int level) implements CustomPacketPayload {

    public static final Type<MiningLevelUpPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "mining_level_up"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MiningLevelUpPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeVarInt(pkt.level),
                    buf -> new MiningLevelUpPacket(buf.readVarInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MiningLevelUpPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) return;

            // 🔊 Sound
            minecraft.player.playSound(
                    SoundEvents.PLAYER_LEVELUP,
                    0.8f,
                    1.2f
            );

            minecraft.player.sendSystemMessage(
                    Component.literal("[Mining] ")
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
            MiningLevelUpToast.show(pkt.level());
        });
    }
}
