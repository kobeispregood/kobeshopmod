package net.lazy.kobe.item;

import net.lazy.kobe.KobeMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.lazy.kobe.block.ModBlocks;

public class ModItems {

    // Register for ITEMS
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, KobeMod.MOD_ID);

    // ----------------------
    //  YOUR EXISTING ITEMS
    // ----------------------

    public static final DeferredHolder<Item, Item> BLACK_OPAL =
            ITEMS.register("black_opal", () ->
                    new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> RAW_BLACK_OPAL =
            ITEMS.register("raw_black_opal", () ->
                    new Item(new Item.Properties()));

    // ----------------------
    //     CRATE KEYS
    // ----------------------

    public static final DeferredHolder<Item, Item> COMMON_KEY =
            ITEMS.register("common_key", () ->
                    new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> RARE_KEY =
            ITEMS.register("rare_key", () ->
                    new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> LEGENDARY_KEY =
            ITEMS.register("legendary_key", () ->
                    new Item(new Item.Properties()));

    // ----------------------
//      BLOCK ITEMS
// ----------------------

    public static final DeferredHolder<Item, Item> COMMON_CRATE_ITEM =
            ITEMS.register("common_crate", () ->
                    new BlockItem(ModBlocks.COMMON_CRATE.get(), new Item.Properties())
            );

    public static final DeferredHolder<Item, Item> RARE_CRATE_ITEM =
            ITEMS.register("rare_crate", () ->
                    new BlockItem(ModBlocks.RARE_CRATE.get(), new Item.Properties())
            );

    public static final DeferredHolder<Item, Item> LEGENDARY_CRATE_ITEM =
            ITEMS.register("legendary_crate", () ->
                    new BlockItem(ModBlocks.LEGENDARY_CRATE.get(), new Item.Properties())
            );

    // ----------------------
    //  REGISTER CALL
    // ----------------------

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}