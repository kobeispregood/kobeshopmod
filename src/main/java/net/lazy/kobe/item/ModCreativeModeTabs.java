package net.lazy.kobe.item;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.item.music.ModMusicDiscs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.lazy.kobe.block.ModBlocks;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KobeMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KOBE_TAB =
            TABS.register("kobe_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.literal("Kobe Items"))
                            .icon(() -> ModItems.LEGENDARY_KEY.get().getDefaultInstance())
                            .displayItems((params, output) -> {
                                output.accept(ModItems.BLACK_OPAL.get());
                                output.accept(ModItems.RAW_BLACK_OPAL.get());

                                output.accept(ModItems.COMMON_KEY.get());
                                output.accept(ModItems.RARE_KEY.get());
                                output.accept(ModItems.LEGENDARY_KEY.get());

                                // 🧱 CRATES
                                output.accept(ModBlocks.COMMON_CRATE.get());
                                output.accept(ModBlocks.RARE_CRATE.get());
                                output.accept(ModBlocks.LEGENDARY_CRATE.get());

                                // Curios
                                output.accept(ModItems.DINOS_DOLLAR.get());
                                output.accept(ModItems.DUNHAMDICE.get());
                                output.accept(ModItems.JESS_JACKPOT.get());
                                output.accept(ModItems.RILEYS_RAMEN.get());
                                output.accept(ModItems.BLAKEY_BAG.get());
                                output.accept(ModItems.SWIFT_SOCKS.get());
                                output.accept(ModMusicDiscs.SLEEP_TIGHT.get());
                            })

                            .build()
            );
}
