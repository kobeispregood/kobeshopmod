package net.lazy.kobe.item;

import net.lazy.kobe.KobeMod;

import net.lazy.kobe.curios.*;
import net.lazy.kobe.curios.strengthshard.StrengthShard;
import net.lazy.kobe.curios.strengthshard.StrengthTablet;
import net.lazy.kobe.item.weapon.EthansEnd;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
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

    public static final DeferredHolder<Item, Item> COMFORT_CLOAK =
            ITEMS.register("comfort_cloak",
                    () -> new ComfortCloak(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON) //
                    ));

    public static final DeferredHolder<Item, Item> STRENGTH_SHARD =
            ITEMS.register("strength_shard",
                    () -> new StrengthShard(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.COMMON) //
                    ));

    public static final DeferredHolder<Item, Item> FATAL_FORTUNE =
            ITEMS.register("fatal_fortune",
                    () -> new FatalFortune(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.COMMON) //
                    ));

    public static final DeferredHolder<Item, Item> HAZARDOUS_HAND =
            ITEMS.register("hazardous_hand",
                    () -> new HazardousHand(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.COMMON) //
                    ));

    public static final DeferredHolder<Item, Item> STRENGTH_TABLET =
            ITEMS.register("strength_tablet",
                    () -> new StrengthTablet(
                            new Item.Properties(),
                            0.15f,
                            5f,
                            ChatFormatting.RED
                    ));

    public static final DeferredHolder<Item, Item> ZENITH_STRENGTH_TABLET =
            ITEMS.register("zenith_strength_tablet",
                    () -> new StrengthTablet(
                            new Item.Properties(),
                            0.20f,
                            10f,
                            ChatFormatting.BLUE
                    ));

    public static final DeferredHolder<Item, Item> ASCENDITE_STRENGTH_TABLET =
            ITEMS.register("ascendite_strength_tablet",
                    () -> new StrengthTablet(
                            new Item.Properties(),
                            0.25f,
                            15f,
                            ChatFormatting.DARK_PURPLE
                    ));
    public static final DeferredHolder<Item, Item> BLACK_OPAL =
            ITEMS.register("black_opal", () ->
                    new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> RAW_BLACK_OPAL =
            ITEMS.register("raw_black_opal", () ->
                    new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ETHANS_END =
            ITEMS.register("ethans_end",
                    () -> new EthansEnd(
                            new Item.Properties().stacksTo(1)
                    ));

    //  ORES

    public static final DeferredHolder<Item, Item> ASCENDITE =
            ITEMS.register("ascendite",
                    () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> AETHERIUM =
            ITEMS.register("aetherium",
                    () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ZENITH =
            ITEMS.register("zenith",
                    () -> new Item(new Item.Properties()));

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
    public static final DeferredHolder<Item, Item> ASCENDITE_ORE_ITEM =
            ITEMS.register("ascendite_ore", () ->
                    new BlockItem(ModBlocks.ASCENDITE_ORE.get(), new Item.Properties())
            );

    public static final DeferredHolder<Item, Item> AETHERIUM_ORE_ITEM =
            ITEMS.register("aetherium_ore", () ->
                    new BlockItem(ModBlocks.AETHERIUM_ORE.get(), new Item.Properties())
            );

    public static final DeferredHolder<Item, Item> ZENITH_ORE_ITEM =
            ITEMS.register("zenith_ore", () ->
                    new BlockItem(ModBlocks.ZENITH_ORE.get(), new Item.Properties())
            );


    // ----------------------
    //  REGISTER CALL
    // ----------------------

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}