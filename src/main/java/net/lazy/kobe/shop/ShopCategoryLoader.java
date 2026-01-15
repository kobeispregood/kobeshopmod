package net.lazy.kobe.shop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ShopCategoryLoader {

    private static final ResourceLocation CATEGORIES_RL =
            ResourceLocation.fromNamespaceAndPath("kobe", "shop/categories.json");

    private ShopCategoryLoader() {}

    /**
     * Loads categories from the ResourceManager (datapacks/mod resources).
     * This works on BOTH client + server.
     */
    public static void load(ResourceManager manager) {
        // Reset registry
        ShopCategory.clear();

        JsonArray array = null;

        // ============================================================
        // 1) PRIMARY: load from ResourceManager (client + server safe)
        // ============================================================
        try {
            // In 1.21.x ResourceManager can return Optional<Resource> or list.
            // We'll use getResource(...) if available via optional try pattern.
            var opt = manager.getResource(CATEGORIES_RL);
            if (opt.isPresent()) {
                Resource res = opt.get();
                try (BufferedReader reader = res.openAsReader()) {
                    array = JsonParser.parseReader(reader).getAsJsonArray();
                }
            }
        } catch (Throwable t) {
            System.out.println("[KobeShop] Failed to load categories from ResourceManager: " + t);
        }

        // ============================================================
        // 2) FALLBACK: load from config if ResourceManager missing it
        //    (keeps backward compatibility)
        // ============================================================
        if (array == null) {
            try {
                Path path = FMLPaths.CONFIGDIR.get()
                        .resolve("kobe")
                        .resolve("shop")
                        .resolve("categories.json");

                if (Files.exists(path)) {
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        array = JsonParser.parseReader(reader).getAsJsonArray();
                        System.out.println("[KobeShop] Loaded categories from config fallback: " + path);
                    }
                }
            } catch (Exception e) {
                System.out.println("[KobeShop] Failed config fallback categories load: " + e);
            }
        }

        // ============================================================
        // 3) FINAL FALLBACK: load directly from jar stream
        // ============================================================
        if (array == null) {
            try (var is = ShopCategoryLoader.class.getClassLoader()
                    .getResourceAsStream("data/kobe/shop/categories.json")) {

                if (is != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, StandardCharsets.UTF_8))) {
                        array = JsonParser.parseReader(reader).getAsJsonArray();
                        System.out.println("[KobeShop] Loaded categories from jar fallback.");
                    }
                }
            } catch (Exception e) {
                System.out.println("[KobeShop] Failed jar fallback categories load: " + e);
            }
        }

        if (array == null) {
            System.out.println("[KobeShop] categories.json not found anywhere. No categories loaded.");
            return;
        }

        // ============================================================
        // REGISTER CATEGORIES
        // ============================================================
        for (int i = 0; i < array.size(); i++) {
            try {
                JsonObject obj = array.get(i).getAsJsonObject();

                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                String iconId = obj.get("icon").getAsString();

                ResourceLocation rl = ResourceLocation.tryParse(iconId);
                if (rl == null) {
                    System.out.println("[KobeShop] BAD icon id: " + iconId);
                    continue;
                }

                Item iconItem = BuiltInRegistries.ITEM.get(rl);
                if (iconItem == null || iconItem == net.minecraft.world.item.Items.AIR) {
                    System.out.println("[KobeShop] ICON ITEM NOT FOUND: " + iconId);
                    continue;
                }

                ShopCategory.register(
                        id,
                        Component.literal(name),
                        new ItemStack(iconItem)
                );

                System.out.println("[KobeShop] Registered category: " + id);

            } catch (Exception e) {
                System.out.println("[KobeShop] Failed category entry at index " + i + ": " + e);
            }
        }

        System.out.println("[KobeShop] Loaded " + ShopCategory.values().length + " categories");
    }

    /**
     * Back-compat helper if you still call ShopCategoryLoader.load() somewhere.
     * Prefer calling load(manager).
     */
    public static void load() {
        try {
            // Try server ResourceManager if available (dedicated/integrated server)
            var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                load(server.getResourceManager());
                return;
            }

            // Client ResourceManager (for non-host clients)
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc != null) {
                load(mc.getResourceManager());
                return;
            }

        } catch (Throwable t) {
            System.out.println("[KobeShop] load() fallback failed: " + t);
        }

        // Worst-case: do nothing (no categories)
        System.out.println("[KobeShop] load() could not access a ResourceManager. No categories loaded.");
        ShopCategory.clear();
    }
}
