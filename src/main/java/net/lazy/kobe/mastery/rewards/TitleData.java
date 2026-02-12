package net.lazy.kobe.mastery.rewards;

import net.minecraft.ChatFormatting;

/**
 * Represents a cosmetic title unlocked via mastery.
 * Titles are NOT applied here – this is just the reward definition.
 */
public record TitleData(
        String id,     // internal id (e.g. "master_alchemist")
        String name,   // display name (e.g. "Master Alchemist")
        String color   // chat color name (e.g. "light_purple")
) {

    /**
     * Safe color parsing with fallback.
     */
    public ChatFormatting getColor() {
        try {
            return ChatFormatting.valueOf(color.toUpperCase());
        } catch (Exception e) {
            return ChatFormatting.GRAY;
        }
    }

    public boolean isValid() {
        return id != null && !id.isEmpty();
    }
}
