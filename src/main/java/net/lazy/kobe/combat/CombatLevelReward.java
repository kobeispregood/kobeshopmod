package net.lazy.kobe.combat;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record CombatLevelReward(
        List<ItemReward> items,
        int money
) {
    public record ItemReward(ResourceLocation item, int count) {}
}
