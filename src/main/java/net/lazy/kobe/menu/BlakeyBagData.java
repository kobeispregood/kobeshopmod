package net.lazy.kobe.menu;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

public final class BlakeyBagData {

    private BlakeyBagData() {}

    /* =========================================================
     * READ ITEMS FROM BAG
     * ========================================================= */
    public static List<ItemStack> getItems(ItemStack bag, HolderLookup.Provider provider) {
        List<ItemStack> items = new ArrayList<>();

        if (bag.isEmpty()) return items;

        CustomData data = bag.get(DataComponents.CUSTOM_DATA);
        if (data == null) return items;

        CompoundTag root = data.copyTag();
        if (!root.contains("Items", 9)) return items; // 9 = TAG_LIST

        ListTag list = root.getList("Items", 10); // 10 = TAG_COMPOUND

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ItemStack stack = ItemStack.parseOptional(provider, entry);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        return items;
    }

    /* =========================================================
     * WRITE ITEMS BACK INTO BAG
     * ========================================================= */
    public static void save(
            ItemStack bag,
            List<ItemStack> items,
            HolderLookup.Provider provider
    ) {
        if (bag.isEmpty()) return;

        CompoundTag root;

        CustomData existing = bag.get(DataComponents.CUSTOM_DATA);
        if (existing != null) {
            root = existing.copyTag();
        } else {
            root = new CompoundTag();
        }

        ListTag list = new ListTag();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            CompoundTag entry = (CompoundTag) stack.save(provider);
            entry.putByte("Slot", (byte) i);
            list.add(entry);
        }

        root.put("Items", list);
        bag.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    /* =========================================================
     * SIMPLE CONTAINS CHECK
     * ========================================================= */
    public static boolean contains(ItemStack bag, Item item, HolderLookup.Provider provider) {
        for (ItemStack stack : getItems(bag, provider)) {
            if (stack.is(item)) return true;
        }
        return false;
    }
}