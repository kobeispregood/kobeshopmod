package net.lazy.kobe.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CombatLevelUpPacket(int level) implements CustomPacketPayload {

    public static final Type<CombatLevelUpPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "combat_level_up"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CombatLevelUpPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeInt(pkt.level()),
                    buf -> new CombatLevelUpPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CombatLevelUpPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            mc.execute(() -> {
                mc.player.playSound(
                        SoundEvents.PLAYER_LEVELUP,
                        0.75f,
                        1.0f
                );

                mc.player.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal(
                                "§c⬆ Combat Mastery Level Up! §7(Level " + pkt.level() + ")"
                        )
                );

                // Optional later:
                // mc.getToasts().addToast(new CombatLevelUpToast(pkt.level()));
            });
        });
    }
}
