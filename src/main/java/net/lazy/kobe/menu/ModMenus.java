package net.lazy.kobe.menu;

import net.lazy.kobe.KobeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, KobeMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BlakeyBagMenu>> BLAKE_BAG =
            MENUS.register("blake_bag",
                    () -> new MenuType<>(BlakeyBagMenu::new, FeatureFlags.DEFAULT_FLAGS));
}