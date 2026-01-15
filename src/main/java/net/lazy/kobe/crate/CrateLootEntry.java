package net.lazy.kobe.crate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public record CrateLootEntry(
        String itemId,
        int amount,
        int weight
) {

    public CrateReward toReward() {
        Item item = BuiltInRegistries.ITEM.get(
                ResourceLocation.tryParse(itemId)
        );

        if (item == null) {
            // Failsafe: give nothing instead of crashing
            return new CrateReward(ItemStack.EMPTY);
        }

        ItemStack stack = new ItemStack(item, amount);
        return new CrateReward(stack);
    }
}
