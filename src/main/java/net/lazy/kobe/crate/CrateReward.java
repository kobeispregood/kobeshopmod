package net.lazy.kobe.crate;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrateReward {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrateReward> STREAM_CODEC =
            ItemStack.STREAM_CODEC.map(CrateReward::new, CrateReward::getStack);

    private final ItemStack stack;

    public CrateReward(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void give(ServerPlayer player) {
        player.getInventory().placeItemBackInInventory(stack.copy());
    }
}
