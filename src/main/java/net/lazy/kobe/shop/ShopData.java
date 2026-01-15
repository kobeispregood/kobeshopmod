package net.lazy.kobe.shop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

import java.io.BufferedReader;
import java.util.*;

public class ShopData extends SimplePreparableReloadListener<Void> {

    public static final Map<String, PriceEntry> PRICE_BY_KEY = new HashMap<>();

    // Category → items (order preserved)
    public static final Map<ShopCategory, List<ItemStack>> CATEGORY_ITEMS =
            new LinkedHashMap<>();

    public static ItemStack buildFromKey(String key) {
        return buildStackFromMaterialSafe(key);
    }

    // ================================================================
    // RELOAD LISTENER
    // ================================================================
    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void ignored, ResourceManager manager, ProfilerFiller profiler) {

        ShopCategoryLoader.load(manager);
        loadAll(manager);

        System.out.println("[KobeShop] Reload complete.");
    }

    // ================================================================
    // LOAD ALL CATEGORIES
    // ================================================================
    public static void loadAll(ResourceManager manager) {

        PRICE_BY_KEY.clear();
        CATEGORY_ITEMS.clear();

        for (ShopCategory cat : ShopCategory.values()) {
            loadCategory(manager, cat);
        }

        System.out.println("[KobeShop] Loaded ALL categories");
    }

    // ================================================================
    // LOAD ONE CATEGORY (BASE → OVERRIDES)
    // ================================================================
    private static void loadCategory(ResourceManager manager, ShopCategory category) {

        List<ItemStack> tempList = new ArrayList<>();
        CATEGORY_ITEMS.put(category, tempList);

        // ======================================================
        // LOAD BASE DATAPACK SHOP FILES FIRST
        // ======================================================
        var resources = manager.listResources(
                "shop",
                id -> id.getPath().endsWith(category.getId() + ".json")
        );

        resources.forEach((id, res) -> {
            try (BufferedReader reader = res.openAsReader()) {

                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject items = root.getAsJsonObject("items");
                if (items == null) return;

                for (Map.Entry<String, JsonElement> entry : items.entrySet()) {

                    JsonObject obj = entry.getValue().getAsJsonObject();
                    if (obj == null) continue;

                    if (!obj.has("material") || !obj.has("buy") || !obj.has("sell")) {
                        System.out.println("[KobeShop] BAD entry in " + id + ": " + entry.getKey());
                        continue;
                    }

                    String key = obj.get("material").getAsString();
                    double buy = obj.get("buy").getAsDouble();
                    double sell = obj.get("sell").getAsDouble();

                    PRICE_BY_KEY.put(key, new PriceEntry(buy, sell));

                    boolean exists = tempList.stream()
                            .anyMatch(stack -> getKeyForStack(stack).equals(key));

                    if (!exists) {
                        ItemStack stack = buildStackFromMaterialSafe(key);
                        if (!stack.isEmpty()) {
                            tempList.add(stack);
                        }
                    }

                }

            } catch (Exception e) {
                System.out.println("[KobeShop] ERROR loading base " + category.getId() + ": " + e);
            }
        });

        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            try {
                ShopOverridesLoader.applyOverrides(server, category);
            } catch (Exception e) {
                System.out.println("[KobeShop] OVERRIDES FAILED for " + category.getId() + ": " + e);
            }
        }

        // ======================================================
        // SORT BY BUY PRICE (LOW → HIGH)
        // ======================================================
        tempList.sort(Comparator.comparingDouble(stack -> getPrice(stack).buy()));

        System.out.println("[KobeShop] Loaded + merged category: " + category.getId());
    }

    // ================================================================
    // MATERIAL → STACK (SAFE)
    // ================================================================
    private static ItemStack buildStackFromMaterialSafe(String key) {

        try {
            if (!key.contains("|")) {
                ResourceLocation rl = ResourceLocation.tryParse(key);
                if (rl == null) return ItemStack.EMPTY;

                Item item = BuiltInRegistries.ITEM.get(rl);
                if (item == null || item == Items.AIR) return ItemStack.EMPTY;

                return new ItemStack(item);
            }

            String[] parts = key.split("\\|");
            if (parts.length != 2) return ItemStack.EMPTY;

            return buildSpawner(parts[1]);

        } catch (Exception e) {
            System.out.println("[KobeShop] Failed to build stack for key: " + key + " -> " + e);
            return ItemStack.EMPTY;
        }
    }

    // ================================================================
    // BUILD SPAWNER (1.21 NBT)
    // ================================================================
    private static ItemStack buildSpawner(String mobId) {

        ItemStack stack = new ItemStack(Items.SPAWNER);

        CompoundTag be = new CompoundTag();
        be.putString("id", "minecraft:spawner");

        CompoundTag spawnData = new CompoundTag();
        CompoundTag entity = new CompoundTag();
        entity.putString("id", mobId);
        spawnData.put("entity", entity);
        be.put("SpawnData", spawnData);

        ListTag potentials = new ListTag();
        CompoundTag potential = new CompoundTag();
        CompoundTag potEntity = new CompoundTag();
        potEntity.putString("id", mobId);
        potential.put("entity", potEntity);
        potential.putInt("weight", 1);
        potentials.add(potential);

        be.put("SpawnPotentials", potentials);

        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(be));
        stack.set(
                DataComponents.CUSTOM_NAME,
                Component.literal(formatMobName(mobId) + " Spawner")
        );

        return stack;
    }

    private static String formatMobName(String mobId) {
        String id = mobId.contains(":")
                ? mobId.substring(mobId.indexOf(':') + 1)
                : mobId;

        StringBuilder b = new StringBuilder();
        for (String part : id.split("_")) {
            if (!part.isEmpty()) {
                b.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(" ");
            }
        }
        return b.toString().trim();
    }

    // ================================================================
    // PRICE LOOKUP
    // ================================================================
    public static PriceEntry getPrice(ItemStack stack) {

        if (stack.is(Items.SPAWNER)) {
            String mob = extractSpawnerMob(stack);
            if (mob != null) {
                return PRICE_BY_KEY.getOrDefault(
                        "minecraft:spawner|" + mob,
                        PriceEntry.NONE
                );
            }
        }

        String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return PRICE_BY_KEY.getOrDefault(key, PriceEntry.NONE);
    }

    public static PriceEntry getPrice(String key) {
        return PRICE_BY_KEY.getOrDefault(key, PriceEntry.NONE);
    }

    // ================================================================
    // STACK → MATERIAL KEY
    // ================================================================
    public static String getKeyForStack(ItemStack stack) {

        if (stack.is(Items.SPAWNER)) {
            var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
                try {
                    String mob = data.copyTag()
                            .getCompound("SpawnData")
                            .getCompound("entity")
                            .getString("id");

                    if (!mob.isEmpty()) {
                        return "minecraft:spawner|" + mob;
                    }
                } catch (Exception ignored) {}
            }
            return "minecraft:spawner";
        }

        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    public static String extractSpawnerMob(ItemStack stack) {
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) return null;

        try {
            return data.copyTag()
                    .getCompound("SpawnData")
                    .getCompound("entity")
                    .getString("id");
        } catch (Exception ignored) {}

        return null;
    }

    // ================================================================
    // UI GETTER
    // ================================================================
    public static List<ItemStack> getItems(ShopCategory cat) {
        return CATEGORY_ITEMS.getOrDefault(cat, List.of());
    }
}
