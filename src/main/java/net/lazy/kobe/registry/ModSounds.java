package net.lazy.kobe.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, "kobe");

    // Fixed-range boss slam sound (NO distance delay)
    public static final DeferredHolder<SoundEvent, SoundEvent> COLOSSUS_SLAM =
            SOUND_EVENTS.register(
                    "boss.colossus_slam",
                    () -> SoundEvent.createFixedRangeEvent(
                            ResourceLocation.fromNamespaceAndPath("kobe", "boss.colossus_slam"),
                            64.0F
                    )
            );

    // Fixed-range boss wind-up sound (NO distance delay)
    public static final DeferredHolder<SoundEvent, SoundEvent> COLOSSUS_WINDUP =
            SOUND_EVENTS.register(
                    "boss.colossus_windup",
                    () -> SoundEvent.createFixedRangeEvent(
                            ResourceLocation.fromNamespaceAndPath("kobe", "boss.colossus_windup"),
                            64.0F
                    )
            );
    public static final DeferredHolder<SoundEvent, SoundEvent> SLEEP_TIGHT =
            SOUND_EVENTS.register(
                    "music.sleep_tight",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath("kobe", "music.sleep_tight")
                    )
            );
    // 🎵 Jukebox song KEY (DATA-DRIVEN)
    public static final ResourceKey<net.minecraft.world.item.JukeboxSong> SLEEP_TIGHT_KEY =
            ResourceKey.create(
                    Registries.JUKEBOX_SONG,
                    ResourceLocation.fromNamespaceAndPath("kobe", "sleep_tight")
            );
}

