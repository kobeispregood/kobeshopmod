package net.lazy.kobe.skills;

import net.lazy.kobe.KobeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SkillMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, KobeMod.MOD_ID);

    // =============================================================
    // GENERIC MASTERY MENU (NO CONTEXT HERE)
    // =============================================================

    public static final DeferredHolder<MenuType<?>, MenuType<MasteryMenu>> MASTERY_MENU =
            MENUS.register(
                    "mastery",
                    () -> IMenuTypeExtension.create(MasteryMenu::new)
            );


    // =============================================================
    // UNCHANGED LEGACY MENUS
    // =============================================================

    public static final DeferredHolder<MenuType<?>, MenuType<SkillsMenu>> SKILLS_MENU =
            MENUS.register("skills", () -> new MenuType<>(SkillsMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<MiningMenu>> MINING_MENU =
            MENUS.register("mining", () -> new MenuType<>(MiningMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<FarmingMenu>> FARMING_MENU =
            MENUS.register("farming", () -> new MenuType<>(FarmingMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<CombatMenu>> COMBAT_MENU =
            MENUS.register("combat", () -> new MenuType<>(CombatMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<HuntsMenu>> HUNTS_MENU =
            MENUS.register("hunts", () -> new MenuType<>(HuntsMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<RunecraftingMenu>> RUNECRAFTING_MENU =
            MENUS.register("runecrafting", () -> new MenuType<>(RunecraftingMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
