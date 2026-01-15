package net.lazy.kobe.skills;

import net.lazy.kobe.KobeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SkillMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, KobeMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<SkillsMenu>> SKILLS_MENU =
            MENUS.register(
                    "skills",
                    () -> new MenuType<>(SkillsMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<MiningMenu>> MINING_MENU =
            MENUS.register(
                    "mining",
                    () -> new MenuType<>(MiningMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<FarmingMenu>> FARMING_MENU =
            MENUS.register(
                    "farming",
                    () -> new MenuType<>(FarmingMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<CombatMenu>> COMBAT_MENU =
            MENUS.register(
                    "combat",
                    () -> new MenuType<>(CombatMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<EnchantingMenu>> ENCHANTING_MENU =
            MENUS.register(
                    "enchanting",
                    () -> new MenuType<>(EnchantingMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<AlchemyMenu>> ALCHEMY_MENU =
            MENUS.register(
                    "alchemy",
                    () -> new MenuType<>(AlchemyMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<FishingMenu>> FISHING_MENU =
            MENUS.register(
                    "fishing",
                    () -> new MenuType<>(FishingMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<ForagingMenu>> FORAGING_MENU =
            MENUS.register(
                    "foraging",
                    () -> new MenuType<>(ForagingMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<HuntsMenu>> HUNTS_MENU =
            MENUS.register(
                    "hunts",
                    () -> new MenuType<>(HuntsMenu::new, FeatureFlagSet.of())
            );

    public static final DeferredHolder<MenuType<?>, MenuType<RunecraftingMenu>> RUNECRAFTING_MENU =
            MENUS.register(
                    "runecrafting",
                    () -> new MenuType<>(RunecraftingMenu::new, FeatureFlagSet.of())
            );
}
