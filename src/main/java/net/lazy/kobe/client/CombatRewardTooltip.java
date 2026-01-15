package net.lazy.kobe.client;

import net.lazy.kobe.combat.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CombatRewardTooltip {

    public static List<Component> getTooltipForLevel(int level) {

        List<Component> lines = new ArrayList<>();

        CombatLevelReward reward =
                CombatRewardManager.getReward(level);

        if (reward == null) return lines;

        // ITEMS (AQUA like Mining/Farming)
        for (var item : reward.items()) {
            lines.add(
                    Component.literal("• ")
                            .append(
                                    Component.translatable(
                                            BuiltInRegistries.ITEM
                                                    .get(item.item())
                                                    .getDescriptionId()
                                    )
                            )
                            .append(" x" + item.count())
                            .withStyle(ChatFormatting.AQUA)
            );
        }

        // MONEY (GOLD)
        if (reward.money() > 0) {
            lines.add(
                    Component.literal("• $" + reward.money())
                            .withStyle(ChatFormatting.GOLD)
            );
        }

        return lines;
    }
}
