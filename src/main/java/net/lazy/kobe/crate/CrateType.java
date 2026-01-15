package net.lazy.kobe.crate;

import net.lazy.kobe.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.Item;

public enum CrateType {

    COMMON(
            "common",
            "Common Crate",
            ModItems.COMMON_KEY,
            "common_rewards"
    ),

    RARE(
            "rare",
            "Rare Crate",
            ModItems.RARE_KEY,
            "rare_rewards"
    ),

    LEGENDARY(
            "legendary",
            "Legendary Crate",
            ModItems.LEGENDARY_KEY,
            "legendary_rewards"
    );

    public final String id;
    public final String display;
    public final DeferredHolder<Item, Item> key;
    public final ResourceLocation lootFile;

    CrateType(String id, String display, DeferredHolder<Item, Item> key, String fileName) {
        this.id = id;
        this.display = display;
        this.key = key;

        // IMPORTANT: Do NOT include "crate/" in the ResourceLocation.
        this.lootFile = ResourceLocation.fromNamespaceAndPath("kobe", fileName);
    }

    public Component displayName() {
        return Component.literal(display);
    }
}
