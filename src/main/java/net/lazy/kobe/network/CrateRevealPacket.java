package net.lazy.kobe.network;

import net.lazy.kobe.client.CrateRevealScreen;
import net.lazy.kobe.crate.CrateType;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CrateRevealPacket(
        String itemId,
        int amount,
        String crateId
) implements CustomPacketPayload {

    public static final Type<CrateRevealPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "crate_reveal"));

    // DECODE
    public CrateRevealPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readInt(), buf.readUtf());
    }

    // ENCODE
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(itemId);
        buf.writeInt(amount);
        buf.writeUtf(crateId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CrateRevealPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            // Resolve item
            Item item = BuiltInRegistries.ITEM.get(
                    ResourceLocation.tryParse(msg.itemId)
            );
            if (item == null) return;

            ItemStack stack = new ItemStack(item, msg.amount);

            // Resolve crate type (NO helper assumed)
            CrateType type = null;
            for (CrateType t : CrateType.values()) {
                if (t.id.equals(msg.crateId)) {
                    type = t;
                    break;
                }
            }
            if (type == null) return;

            Minecraft.getInstance().setScreen(
                    new CrateRevealScreen(stack, type)
            );
        });
    }
}
