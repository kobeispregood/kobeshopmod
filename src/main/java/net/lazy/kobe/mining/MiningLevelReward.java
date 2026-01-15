package net.lazy.kobe.mining;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MiningLevelReward(
        List<ItemReward> items,
        int money
) {
    public record ItemReward(ResourceLocation item, int count) {}
}
