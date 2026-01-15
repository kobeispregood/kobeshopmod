package net.lazy.kobe.mining;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MiningSyncPacket(int xp) implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("kobe", "mining_sync");

    public static final Type<MiningSyncPacket> TYPE = new Type<>(ID);

    // ✅ THIS is required in NeoForge 1.21
    public static final StreamCodec<RegistryFriendlyByteBuf, MiningSyncPacket> CODEC =
            StreamCodec.of(
                    // encode
                    (buf, packet) -> buf.writeVarInt(packet.xp),
                    // decode
                    buf -> new MiningSyncPacket(buf.readVarInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handle(MiningSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() == null) return;

            var data = ctx.player().getData(MiningAttachment.MINING);
            data.setXpClient(packet.xp());
        });
    }

    private static net.minecraft.nbt.CompoundTag createTag(int xp) {
        var tag = new net.minecraft.nbt.CompoundTag();
        tag.putInt("xp", xp);
        return tag;
    }
}
