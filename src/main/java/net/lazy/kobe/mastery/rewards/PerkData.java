package net.lazy.kobe.mastery.rewards;

/**
 * Represents a mastery perk unlocked at a specific level.
 * This is PURE DATA – no logic here.
 *
 * Logic belongs in perk handlers later.
 */
public record PerkData(
        String id,          // internal id (e.g. "deep_sea_luck")
        String name,        // display name (e.g. "Deep Sea Luck")
        String description  // tooltip / chat description
) {

    public boolean isValid() {
        return id != null && !id.isEmpty();
    }
}
