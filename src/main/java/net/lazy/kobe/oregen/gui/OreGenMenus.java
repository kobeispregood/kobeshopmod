package net.lazy.kobe.oregen.gui;

import net.lazy.kobe.KobeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OreGenMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, KobeMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<OreGenMenu>> ORE_GEN_MENU =
            MENUS.register(
                    "ore_gen",
                    () -> new MenuType<>(OreGenMenu::new, FeatureFlagSet.of())
            );
}
