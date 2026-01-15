package net.lazy.kobe.mining;

import com.google.gson.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class MiningRewardManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    // Store raw level JSON so we can read items + money
    private static final Map<Integer, JsonObject> RAW_REWARDS = new HashMap<>();

    public MiningRewardManager() {
        super(GSON, "mining_rewards");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsons,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        RAW_REWARDS.clear();

        for (JsonElement element : jsons.values()) {

            JsonObject root = element.getAsJsonObject();

            for (String levelKey : root.keySet()) {
                int level = Integer.parseInt(levelKey);
                JsonObject levelObj = root.getAsJsonObject(levelKey);

                RAW_REWARDS.put(level, levelObj);
            }
        }

        System.out.println("[MiningRewards] Loaded " + RAW_REWARDS.size() + " levels");
    }

    /**
     * Creates item stacks for a given level reward
     */
    public static List<ItemStack> createStacks(int level) {
        List<ItemStack> stacks = new ArrayList<>();

        JsonObject levelObj = RAW_REWARDS.get(level);
        if (levelObj == null) return stacks;

        JsonArray items = levelObj.getAsJsonArray("items");
        if (items == null) return stacks;

        for (JsonElement el : items) {
            JsonObject itemObj = el.getAsJsonObject();

            ResourceLocation id =
                    ResourceLocation.tryParse(itemObj.get("item").getAsString());
            if (id == null) continue;

            int count = itemObj.get("count").getAsInt();

            Item item = BuiltInRegistries.ITEM.get(id);
            if (item != null) {
                stacks.add(new ItemStack(item, count));
            }
        }

        return stacks;
    }

    /**
     * Returns money reward for a given level
     */
    public static int getMoney(int level) {
        JsonObject levelObj = RAW_REWARDS.get(level);
        if (levelObj == null) return 0;

        return levelObj.has("money")
                ? levelObj.get("money").getAsInt()
                : 0;
    }

    public static boolean hasRewards(int level) {
        return RAW_REWARDS.containsKey(level);
    }
}
