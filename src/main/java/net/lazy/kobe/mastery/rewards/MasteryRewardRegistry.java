package net.lazy.kobe.mastery.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class MasteryRewardRegistry {

    // mastery_id -> (level -> reward)
    private static final Map<String, Map<Integer, MasteryReward>> REWARDS =
            new HashMap<>();

    private MasteryRewardRegistry() {}

    public static void clear() {
        REWARDS.clear();
    }

    public static int size() {
        return REWARDS.size();
    }

    /**
     * Called by MasteryRewardLoader for each JSON file under:
     * data/kobe/mastery/*.json
     */
    public static void load(ResourceLocation id, JsonElement json) {

        // With folder="mastery", id path is "fishing", "alchemy", etc.
        String masteryId = id.getPath();

        JsonObject root = json.getAsJsonObject();

        // ✅ THIS is the important change
        JsonObject rewardsObj = root.getAsJsonObject("rewards");

        Map<Integer, MasteryReward> levelRewards = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : rewardsObj.entrySet()) {

            String key = entry.getKey();

            // Defensive guard (prevents future crashes)
            if (!key.chars().allMatch(Character::isDigit)) {
                continue;
            }

            int level = Integer.parseInt(key);
            JsonObject obj = entry.getValue().getAsJsonObject();

            levelRewards.put(level, MasteryReward.fromJson(obj));
        }

        REWARDS.put(masteryId, levelRewards);

        // ✅ DEBUG
        System.out.println(
                "[MASTERY] Loaded rewards for " + masteryId +
                        " -> " + levelRewards.keySet()
        );
    }

    public static MasteryReward get(String masteryId, int level) {
        Map<Integer, MasteryReward> map = REWARDS.get(masteryId);
        return map == null ? null : map.get(level);
    }

    // ✅ Convenience overload (fixes packet + screen usage)
    public static MasteryReward get(MasteryType type, int level) {
        return get(type.getSerializedName(), level);
    }
}
