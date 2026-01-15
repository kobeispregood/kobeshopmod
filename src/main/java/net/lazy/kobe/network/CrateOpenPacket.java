package net.lazy.kobe.network;

import net.lazy.kobe.crate.CrateBlockEntity;
import net.lazy.kobe.crate.CrateType;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CrateOpenPacket(BlockPos pos, String crateId) implements CustomPacketPayload {

    public static final Type<CrateOpenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "crate_open"));

    public CrateOpenPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(crateId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CrateOpenPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            var level = player.level();
            if (!level.hasChunkAt(msg.pos)) return;

            var be = level.getBlockEntity(msg.pos);
            if (!(be instanceof CrateBlockEntity crate)) return;

            CrateType type = null;
            for (CrateType t : CrateType.values()) {
                if (t.id.equals(msg.crateId)) {
                    type = t;
                    break;
                }
            }

            if (type == null) return;

            // ✅ SERVER confirms crate + type
            // Gameplay logic already happens elsewhere
            // (This packet now just validates the open request)

            System.out.println("[CRATE] Valid crate open: " + type.id);
        });
    }
}
