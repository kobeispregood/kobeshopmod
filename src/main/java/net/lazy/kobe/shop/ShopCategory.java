package net.lazy.kobe.shop;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ShopCategory {

    private static final Map<String, ShopCategory> REGISTRY = new LinkedHashMap<>();

    private final String id;
    private final Component displayName;
    private final ItemStack icon;

    private ShopCategory(String id, Component displayName, ItemStack icon) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
    }

    /* ============================================================
     *  REGISTRATION (ENUM REPLACEMENT)
     * ============================================================ */

    public static ShopCategory register(String id, Component displayName, ItemStack icon) {
        return REGISTRY.computeIfAbsent(id,
                k -> new ShopCategory(k, displayName, icon));
    }

    /* ============================================================
     *  ENUM COMPATIBILITY
     * ============================================================ */

    public static ShopCategory[] values() {
        Collection<ShopCategory> values = REGISTRY.values();
        return values.toArray(new ShopCategory[0]);
    }

    public static ShopCategory valueOf(String id) {
        return REGISTRY.get(id);
    }

    /* ============================================================
     *  ACCESSORS (MATCH OLD ENUM API)
     * ============================================================ */

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public ItemStack getIcon() {
        return icon.copy();
    }

    public static void clear() {
        REGISTRY.clear();
    }

    @Override
    public String toString() {
        return "ShopCategory[" + id + "]";
    }
}
