package net.lazy.kobe.mastery.rewards;

import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class MasteryRewardTooltip {

    private MasteryRewardTooltip() {}

    // =============================================================
    // MAIN API (CALLED BY MASTERY SCREENS)
    // =============================================================
    public static List<Component> getTooltip(MasteryType type, int level) {

        List<Component> tooltip = new ArrayList<>();

        MasteryReward reward =
                MasteryRewardRegistry.get(type, level);

        if (reward == null) {
            return tooltip;
        }

        ChatFormatting theme = masteryColor(type);

        // =============================================================
        // HEADER
        // =============================================================
        tooltip.add(
                Component.literal("Level " + level + " Rewards")
                        .withStyle(theme, ChatFormatting.BOLD)
        );

        tooltip.add(Component.literal("")); // spacing line

        // =============================================================
        // MONEY
        // =============================================================
        if (reward.money > 0) {
            tooltip.add(
                    Component.literal("+ $" + reward.money)
                            .withStyle(ChatFormatting.GREEN)
            );
        }

        // =============================================================
        // XP
        // =============================================================
        if (reward.xp > 0) {
            tooltip.add(
                    Component.literal("+ " + reward.xp + " XP")
                            .withStyle(ChatFormatting.AQUA)
            );
        }

        // =============================================================
        // ITEMS
        // =============================================================
        if (!reward.items.isEmpty()) {

            tooltip.add(
                    Component.literal("Items:")
                            .withStyle(theme)
            );

            for (ItemStack stack : reward.items) {
                tooltip.add(
                        Component.literal("• ")
                                .append(stack.getHoverName())
                                .withStyle(theme)
                );
            }
        }

        // =============================================================
        // PERK UNLOCK
        // =============================================================
        if (reward.perk != null) {

            tooltip.add(Component.literal(""));

            tooltip.add(
                    Component.literal("Perk Unlocked:")
                            .withStyle(theme)
            );

            tooltip.add(
                    Component.literal("• " + reward.perk.name())
                            .withStyle(ChatFormatting.GRAY)
            );

            if (reward.perk.description() != null &&
                    !reward.perk.description().isBlank()) {

                tooltip.add(
                        Component.literal("  " + reward.perk.description())
                                .withStyle(ChatFormatting.DARK_GRAY)
                );
            }
        }

        // =============================================================
        // PASSIVE INFO TEXT
        // =============================================================
        if (reward.perk == null &&
                reward.infoDescription != null &&
                !reward.infoDescription.isBlank()) {

            tooltip.add(Component.literal(""));

            for (String line : reward.infoDescription.split("\n")) {

                ChatFormatting color = ChatFormatting.GRAY;

                String lower = line.toLowerCase();

                if (lower.contains("crit chance")) {
                    color = ChatFormatting.GOLD;
                } else if (lower.contains("crit damage")) {
                    color = ChatFormatting.RED;
                } else if (lower.contains("lifesteal")) {
                    color = ChatFormatting.DARK_RED;
                }

                tooltip.add(
                        Component.literal("• " + line)
                                .withStyle(color)
                );
            }
        }

        // =============================================================
        // TITLE UNLOCK
        // =============================================================
        if (reward.title != null) {

            tooltip.add(Component.literal(""));

            tooltip.add(
                    Component.literal("Title Unlocked:")
                            .withStyle(ChatFormatting.LIGHT_PURPLE)
            );

            tooltip.add(
                    Component.literal("• " + reward.title.name())
                            .withStyle(reward.title.getColor())
            );
        }

        return tooltip;
    }

    // =============================================================
    // COLOR THEMING PER MASTERY
    // =============================================================
    private static ChatFormatting masteryColor(MasteryType type) {
        return switch (type) {
            case COMBAT -> ChatFormatting.RED;
            case MINING -> ChatFormatting.GREEN;
            case FARMING -> ChatFormatting.YELLOW;
            case FISHING -> ChatFormatting.AQUA;
            case FORAGING -> ChatFormatting.DARK_GREEN;
            case ALCHEMY -> ChatFormatting.LIGHT_PURPLE;
            case ENCHANTING -> ChatFormatting.BLUE;
            default -> ChatFormatting.GRAY;
        };
    }
}