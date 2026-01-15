package net.lazy.kobe.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record MiningPerkUnlockPacket(List<String> perks) implements CustomPacketPayload {

    public static final Type<MiningPerkUnlockPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "mining_perk_unlock"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MiningPerkUnlockPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeVarInt(pkt.perks.size());
                        for (String s : pkt.perks) buf.writeUtf(s);
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        var list = new java.util.ArrayList<String>();
                        for (int i = 0; i < size; i++) list.add(buf.readUtf());
                        return new MiningPerkUnlockPacket(list);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MiningPerkUnlockPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // softer perk sound
            mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6f, 1.2f);

            for (String perk : pkt.perks) {
                mc.player.sendSystemMessage(
                        Component.literal("§6✦ Perk Unlocked: §e" + perk)
                );

                mc.getToasts().addToast(
                        new MiningPerkUnlockToast(perk)
                );
            }
        });
    }
}
