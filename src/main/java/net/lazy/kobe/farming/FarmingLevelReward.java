package net.lazy.kobe.farming;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record FarmingLevelReward(
        List<ItemReward> items,
        int money
) {
    public record ItemReward(ResourceLocation item, int count) {}
}
