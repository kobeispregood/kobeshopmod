package net.lazy.kobe.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.lazy.kobe.shop.net.ShopNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class ShopAdminCommand {

    private static final SuggestionProvider<CommandSourceStack> CATEGORY_SUGGEST =
            (ctx, builder) -> {
                for (ShopCategory cat : ShopCategory.values()) {
                    builder.suggest(cat.getId());
                }
                return builder.buildFuture();
            };

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /* ============================================================
     * REGISTER
     * ============================================================ */
    private static final SuggestionProvider<CommandSourceStack> ITEM_SUGGEST =
            (ctx, builder) -> {
                BuiltInRegistries.ITEM.keySet()
                        .forEach(id -> builder.suggest(id.toString()));
                return builder.buildFuture();
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("shop")
                        .requires(src -> src.hasPermission(2))

                        // /shop reload
                        .then(Commands.literal("reload")
                                .executes(ctx -> {
                                    reload(ctx.getSource());
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("Shop reloaded"),
                                            true
                                    );
                                    return 1;
                                })
                        )

                        // /shop add <category> <item> <buy> <sell> [--force]
                        .then(Commands.literal("add")
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .suggests(CATEGORY_SUGGEST)
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .suggests(ITEM_SUGGEST)
                                                .then(Commands.argument("buy", DoubleArgumentType.doubleArg(0))
                                                        .then(Commands.argument("sell", DoubleArgumentType.doubleArg(0))
                                                                .executes(ctx -> addItem(
                                                                        ctx.getSource(),
                                                                        ctx.getArgument("category", String.class),
                                                                        ResourceLocationArgument.getId(ctx, "item"),
                                                                        ctx.getArgument("buy", Double.class),
                                                                        ctx.getArgument("sell", Double.class),
                                                                        false
                                                                ))
                                                                .then(Commands.literal("--force")
                                                                        .executes(ctx -> addItem(
                                                                                ctx.getSource(),
                                                                                ctx.getArgument("category", String.class),
                                                                                ResourceLocationArgument.getId(ctx, "item"),
                                                                                ctx.getArgument("buy", Double.class),
                                                                                ctx.getArgument("sell", Double.class),
                                                                                true
                                                                        ))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                        // /shop remove <category> <item>
                        .then(Commands.literal("remove")
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .suggests(CATEGORY_SUGGEST)
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .suggests(ITEM_SUGGEST)
                                                .executes(ctx -> removeItem(
                                                        ctx.getSource(),
                                                        ctx.getArgument("category", String.class),
                                                        ResourceLocationArgument.getId(ctx, "item")
                                                ))
                                        )
                                )
                        )

                        // /shop set <category> <item> buy|sell <value>
                        .then(Commands.literal("set")
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .suggests(CATEGORY_SUGGEST)
                                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                                .suggests(ITEM_SUGGEST)
                                                .then(Commands.literal("buy")
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0))
                                                                .executes(ctx -> setPrice(
                                                                        ctx.getSource(),
                                                                        ctx.getArgument("category", String.class),
                                                                        ResourceLocationArgument.getId(ctx, "item"),
                                                                        true,
                                                                        ctx.getArgument("value", Double.class)
                                                                ))
                                                        )
                                                )
                                                .then(Commands.literal("sell")
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0))
                                                                .executes(ctx -> setPrice(
                                                                        ctx.getSource(),
                                                                        ctx.getArgument("category", String.class),
                                                                        ResourceLocationArgument.getId(ctx, "item"),
                                                                        false,
                                                                        ctx.getArgument("value", Double.class)
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    /* ============================================================
     * ADD ITEM
     * ============================================================ */

    private static int addItem(
            CommandSourceStack source,
            String categoryId,
            ResourceLocation itemId,
            double buy,
            double sell,
            boolean force
    ) {
        try {
            ShopCategory category = requireCategory(source, categoryId);
            if (category == null) return 0;

            String key = itemId.toString();

            ItemStack stack = ShopData.buildFromKey(key);
            if (stack.isEmpty() || stack.is(Items.AIR)) {
                source.sendFailure(
                        Component.literal("Item resolved to AIR: " + key)
                );
                return 0;
            }

            Path file = getCategoryFile(source, category.getId());
            JsonObject root = readOrCreate(file);
            JsonObject items = root.getAsJsonObject("items");

            if (items.has(key) && !force) {
                source.sendFailure(
                        Component.literal("Item already exists. Use --force to overwrite.")
                );
                return 0;
            }

            JsonObject obj = new JsonObject();
            obj.addProperty("material", key);
            obj.addProperty("buy", buy);
            obj.addProperty("sell", sell);

            items.add(key, obj);
            write(file, root);

            reload(source);
            source.sendSuccess(
                    () -> Component.literal("Added " + key + " to " + category.getId()),
                    true
            );
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            source.sendFailure(Component.literal("Failed to add item"));
            return 0;
        }
    }

    /* ============================================================
     * REMOVE ITEM
     * ============================================================ */

    private static int removeItem(
            CommandSourceStack source,
            String categoryId,
            ResourceLocation itemId
    ) {
        try {
            ShopCategory category = requireCategory(source, categoryId);
            if (category == null) return 0;

            String key = itemId.toString();
            Path file = getCategoryFile(source, category.getId());

            if (!Files.exists(file)) {
                source.sendFailure(Component.literal("No overrides for this category"));
                return 0;
            }

            JsonObject root = read(file);
            JsonObject items = root.getAsJsonObject("items");

            if (!items.has(key)) {
                source.sendFailure(Component.literal("Item not found"));
                return 0;
            }

            items.remove(key);
            write(file, root);

            reload(source);
            source.sendSuccess(
                    () -> Component.literal("Removed " + key),
                    true
            );
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            source.sendFailure(Component.literal("Failed to remove item"));
            return 0;
        }
    }

    /* ============================================================
     * SET PRICE
     * ============================================================ */

    private static int setPrice(
            CommandSourceStack source,
            String categoryId,
            ResourceLocation itemId,
            boolean buy,
            double value
    ) {
        try {
            ShopCategory category = requireCategory(source, categoryId);
            if (category == null) return 0;

            String key = itemId.toString();
            Path file = getCategoryFile(source, category.getId());

            if (!Files.exists(file)) {
                source.sendFailure(Component.literal("No overrides for this category"));
                return 0;
            }

            JsonObject root = read(file);
            JsonObject items = root.getAsJsonObject("items");

            if (!items.has(key)) {
                source.sendFailure(Component.literal("Item not found"));
                return 0;
            }

            items.getAsJsonObject(key)
                    .addProperty(buy ? "buy" : "sell", value);

            write(file, root);
            reload(source);

            source.sendSuccess(
                    () -> Component.literal("Updated price for " + key),
                    true
            );
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            source.sendFailure(Component.literal("Failed to set price"));
            return 0;
        }
    }

    /* ============================================================
     * HELPERS
     * ============================================================ */

    private static Path getCategoryFile(CommandSourceStack source, String category) throws Exception {

        if (source.getServer() == null) {
            throw new IllegalStateException("Server not available");
        }

        Path datapacks = source.getServer()
                .getWorldPath(net.minecraft.world.level.storage.LevelResource.DATAPACK_DIR);

        Path dir = datapacks
                .resolve("kobe_shop")
                .resolve("data")
                .resolve("kobe")
                .resolve("shop")
                .resolve("overrides");

        Files.createDirectories(dir);

        return dir.resolve(category + ".json");
    }

    private static ShopCategory requireCategory(
            CommandSourceStack source,
            String categoryId
    ) {
        ShopCategory cat = ShopCategory.valueOf(categoryId);
        if (cat == null) {
            source.sendFailure(
                    Component.literal("Unknown shop category: " + categoryId)
            );
            return null;
        }
        return cat;
    }

    private static JsonObject readOrCreate(Path path) throws Exception {
        if (!Files.exists(path)) {
            JsonObject root = new JsonObject();
            root.add("items", new JsonObject());
            return root;
        }
        return read(path);
    }

    private static JsonObject read(Path path) throws Exception {
        try (BufferedReader r = Files.newBufferedReader(path)) {

            JsonObject obj = GSON.fromJson(r, JsonObject.class);

            // 🔑 Gson returns null for empty files
            if (obj == null) {
                obj = new JsonObject();
            }

            if (!obj.has("items") || !obj.get("items").isJsonObject()) {
                obj.add("items", new JsonObject());
            }

            return obj;
        }
    }

    private static void write(Path path, JsonObject obj) throws Exception {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            GSON.toJson(obj, w);
        }
    }

    private static void reload(CommandSourceStack source) {

        // SERVER ONLY
        if (source.getServer() == null) return;

        ShopCategoryLoader.load(source.getServer().getResourceManager());
        ShopData.loadAll(source.getServer().getResourceManager());

        // 🔁 SYNC TO ALL PLAYERS (next step)
        ShopNetwork.syncToAll(source.getServer());
    }
}
