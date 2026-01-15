package net.lazy.kobe.combat;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStreamReader;
import java.util.*;

public final class CombatRewardManager {

    private static final Map<Integer, CombatLevelReward> CACHE = new HashMap<>();
    private static boolean loaded = false;

    public static CombatLevelReward getReward(int level) {
        if (!loaded) load();
        return CACHE.get(level);
    }

    private static void load() {
        loaded = true;

        try {
            var stream = CombatRewardManager.class
                    .getClassLoader()
                    .getResourceAsStream("data/kobe/rewards/combat_rewards.json");

            if (stream == null) return;

            JsonObject root = JsonParser
                    .parseReader(new InputStreamReader(stream))
                    .getAsJsonObject();

            for (String key : root.keySet()) {
                int level = Integer.parseInt(key);

                JsonArray arr = root.getAsJsonArray(key);

                int money = 0;
                List<CombatLevelReward.ItemReward> items = new ArrayList<>();

                for (JsonElement e : arr) {
                    JsonObject obj = e.getAsJsonObject();
                    String type = obj.get("type").getAsString();

                    if (type.equals("money")) {
                        money += obj.get("amount").getAsInt();
                    }

                    if (type.equals("item")) {
                        items.add(new CombatLevelReward.ItemReward(
                                ResourceLocation.parse(obj.get("item").getAsString()),
                                obj.get("count").getAsInt()
                        ));
                    }
                }

                CACHE.put(level, new CombatLevelReward(items, money));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CombatRewardManager() {}
}
