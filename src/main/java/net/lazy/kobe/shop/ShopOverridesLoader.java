package net.lazy.kobe.shop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ShopOverridesLoader {

    private ShopOverridesLoader() {}

    /**
     * Applies config overrides for a single category.
     * This PATCHES existing shop data and NEVER replaces it.
     *
     * Called from ShopData.loadCategory() AFTER base datapack items load.
     */
    public static void applyOverrides(MinecraftServer server, ShopCategory category) {

        Path file;
        try {
            file = getWorldOverridesDir(server)
                    .resolve(category.getId() + ".json");
        } catch (Exception e) {
            System.out.println("[KobeShop] Failed to resolve world overrides for " + category.getId());
            return;
        }

        if (!Files.exists(file)) return;

        System.out.println("[KobeShop] Applying overrides for category: " + category.getId());

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed == null || !parsed.isJsonObject()) return;

            JsonObject root = parsed.getAsJsonObject();
            if (!root.has("items") || !root.get("items").isJsonObject()) return;

            JsonObject items = root.getAsJsonObject("items");

            List<ItemStack> list = ShopData.CATEGORY_ITEMS
                    .computeIfAbsent(category, c -> new java.util.ArrayList<>());

            for (Map.Entry<String, JsonElement> entry : items.entrySet()) {

                if (!entry.getValue().isJsonObject()) continue;

                JsonObject obj = entry.getValue().getAsJsonObject();
                if (!obj.has("material")) continue;

                String key = obj.get("material").getAsString();

                double buy = obj.has("buy") ? obj.get("buy").getAsDouble() : -1;
                double sell = obj.has("sell") ? obj.get("sell").getAsDouble() : -1;

                // ======================================================
                // 1️⃣ OVERRIDE PRICE (PATCH)
                // ======================================================
                ShopData.PRICE_BY_KEY.put(key, new PriceEntry(buy, sell));

                // ======================================================
                // 2️⃣ INJECT STACK IF MISSING
                // ======================================================
                boolean exists = list.stream()
                        .anyMatch(stack -> ShopData.getKeyForStack(stack).equals(key));

                if (!exists) {
                    ItemStack stack = ShopData.buildFromKey(key);
                    if (!stack.isEmpty()) {
                        list.add(stack);
                    } else {
                        System.out.println("[KobeShop] Skipped invalid override item: " + key);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("[KobeShop] ERROR applying overrides for " + category.getId());
            e.printStackTrace();
        }
    }

    public static Path getWorldOverridesDir(MinecraftServer server) throws Exception {
        Path datapacks = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.DATAPACK_DIR);
        Path dir = datapacks
                .resolve("kobe_shop")
                .resolve("data")
                .resolve("kobe")
                .resolve("shop")
                .resolve("overrides");

        Files.createDirectories(dir);
        return dir;
    }

}
