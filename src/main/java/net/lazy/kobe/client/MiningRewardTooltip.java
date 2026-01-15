package net.lazy.kobe.client;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import net.lazy.kobe.item.ModItems;

import java.io.InputStreamReader;
import java.util.*;

public class MiningRewardTooltip {

    private static final Map<Integer, List<Component>> CACHE = new HashMap<>();
    private static boolean loaded = false;

    public static List<Component> getTooltipForLevel(int level) {
        if (!loaded) load();
        return CACHE.getOrDefault(level, Collections.emptyList());
    }

    private static void load() {
        loaded = true;

        try {
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "mining_rewards/levels.json"
            );

            var res = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(loc)
                    .orElse(null);

            if (res == null) return;

            JsonObject root = JsonParser.parseReader(
                    new InputStreamReader(res.open())
            ).getAsJsonObject();

            for (var entry : root.entrySet()) {
                int level = Integer.parseInt(entry.getKey());

                JsonObject levelObj = entry.getValue().getAsJsonObject();

                // ---- ITEMS ----
                JsonArray rewards = levelObj.getAsJsonArray("items");

                List<Component> lines = new ArrayList<>();

                for (JsonElement el : rewards) {
                    JsonObject obj = el.getAsJsonObject();

                    String itemId = obj.get("item").getAsString();
                    int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

                    ResourceLocation itemKey = ResourceLocation.tryParse(itemId);
                    if (itemKey == null || !BuiltInRegistries.ITEM.containsKey(itemKey)) continue;

                    Item item = BuiltInRegistries.ITEM.get(itemKey);

                    // ---- COLOR LOGIC ----
                    ChatFormatting color = ChatFormatting.GRAY;

                    if (item == ModItems.COMMON_KEY.get()) {
                        color = ChatFormatting.BLUE;
                    } else if (item == ModItems.RARE_KEY.get()) {
                        color = ChatFormatting.LIGHT_PURPLE;
                    } else if (item == ModItems.LEGENDARY_KEY.get()) {
                        color = ChatFormatting.GOLD;
                    }

                    lines.add(
                            Component.literal("• " + count + "× ")
                                    .append(
                                            Component.translatable(item.getDescriptionId())
                                                    .withStyle(color)
                                    )
                    );
                }

                // ---- MONEY ----
                int money = levelObj.has("money") ? levelObj.get("money").getAsInt() : 0;

                if (money > 0) {
                    lines.add(
                            Component.literal("• $" + money)
                                    .withStyle(ChatFormatting.GREEN)
                    );
                }

                CACHE.put(level, lines);
            }
        } catch (Exception ignored) {
            // silent fail — tooltips just won't show
        }
    }
}
