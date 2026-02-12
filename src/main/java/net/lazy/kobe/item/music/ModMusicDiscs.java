package net.lazy.kobe.item.music;

import net.lazy.kobe.registry.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMusicDiscs {

    public static final DeferredRegister<Item> MUSIC_DISCS =
            DeferredRegister.create(Registries.ITEM, "kobe");

    public static final DeferredHolder<Item, Item> SLEEP_TIGHT =
            MUSIC_DISCS.register(
                    "sleep_tight",
                    () -> new Item(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .jukeboxPlayable(ModSounds.SLEEP_TIGHT_KEY)
                    )
            );
}