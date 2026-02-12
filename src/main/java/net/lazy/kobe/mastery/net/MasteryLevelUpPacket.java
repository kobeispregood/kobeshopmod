package net.lazy.kobe.mastery.net;

import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MasteryLevelUpPacket(
        MasteryType masteryType,
        int level
) implements CustomPacketPayload {

    public static final Type<MasteryLevelUpPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "mastery_level_up"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MasteryLevelUpPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeEnum(pkt.masteryType());
                        buf.writeVarInt(pkt.level());
                    },
                    buf -> new MasteryLevelUpPacket(
                            buf.readEnum(MasteryType.class),
                            buf.readVarInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MasteryLevelUpPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            mc.player.playSound(SoundEvents.PLAYER_LEVELUP, 0.8f, 1.2f);

            mc.player.sendSystemMessage(
                    Component.literal("[")
                            .append(
                                    Component.literal(pkt.masteryType().getSerializedName().toUpperCase())
                                            .withStyle(net.minecraft.ChatFormatting.GOLD)
                            )
                            .append(Component.literal("] "))
                            .append(
                                    Component.literal("Level Up! Level " + pkt.level())
                                            .withStyle(net.minecraft.ChatFormatting.GREEN)
                            )
            );
        });
    }
}
