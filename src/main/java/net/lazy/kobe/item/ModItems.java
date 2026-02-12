package net.lazy.kobe.item;

import net.lazy.kobe.KobeMod;

import net.lazy.kobe.curios.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Rarity;
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
    //  ODDITIES
    // ----------------------
    public static final DeferredHolder<Item, Item> DUNHAMDICE =
            ITEMS.register("dunhamdice",
                    () -> new DunhamDiceItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> RILEYS_RAMEN =
            ITEMS.register("rileys_ramen",
                    () -> new RileysRamen(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)
                    ));

    public static final DeferredHolder<Item, Item> JESS_JACKPOT =
            ITEMS.register("jess_jackpot",
                    () -> new JessJackpot(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.RARE) // aqua name = jackpot vibes
                    ));

    public static final DeferredHolder<Item, Item> BLAKEY_BAG =
            ITEMS.register("blakey_bag",
                    () -> new BlakeyBag(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.COMMON) //
                    ));

    public static final DeferredHolder<Item, Item> DINOS_DOLLAR =
            ITEMS.register("dinos_dollar",
                    () -> new DinosDollar(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.COMMON) //
                    ));

    public static final DeferredHolder<Item, Item> SWIFT_SOCKS =
            ITEMS.register("swift_socks",
                    () -> new SwiftSocks(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.EPIC) //
                    ));

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