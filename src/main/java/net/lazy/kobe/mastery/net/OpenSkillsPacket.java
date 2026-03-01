package net.lazy.kobe.mastery.net;

import net.lazy.kobe.skills.SkillsMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenSkillsPacket() implements CustomPacketPayload {

    public static final Type<OpenSkillsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "open_skills"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSkillsPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {},
                    buf -> new OpenSkillsPacket()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenSkillsPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            player.openMenu(new net.minecraft.world.MenuProvider() {
                @Override
                public net.minecraft.network.chat.Component getDisplayName() {
                    return net.minecraft.network.chat.Component.literal("Skills");
                }

                @Override
                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(
                        int id,
                        net.minecraft.world.entity.player.Inventory inv,
                        net.minecraft.world.entity.player.Player p
                ) {
                    return new SkillsMenu(id, inv);
                }
            });
        });
    }
}