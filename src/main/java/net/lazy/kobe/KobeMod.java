package net.lazy.kobe;

import com.mojang.logging.LogUtils;

import net.lazy.kobe.combat.CombatAttachment;

import net.lazy.kobe.block.ModBlocks;
import net.lazy.kobe.block.ModBlockEntities;

import net.lazy.kobe.client.*;

import net.lazy.kobe.combat.CombatEvents;
import net.lazy.kobe.crate.CrateLootData;

import net.lazy.kobe.econ.EconEvents;
import net.lazy.kobe.econ.MoneyAttachment;

import net.lazy.kobe.farming.FarmingAttachment;
import net.lazy.kobe.farming.FarmingDimensionSync;
import net.lazy.kobe.farming.FarmingJoinSync;
import net.lazy.kobe.item.ModCreativeModeTabs;
import net.lazy.kobe.item.ModItems;

import net.lazy.kobe.mining.*;
import net.lazy.kobe.network.NetworkHandler;

import net.lazy.kobe.oregen.GeneratorItemProtectionHandler;
import net.lazy.kobe.oregen.OreGenAttachment;
import net.lazy.kobe.oregen.gui.OreGenMenus;
import net.lazy.kobe.shop.ShopCategoryLoader;
import net.lazy.kobe.shop.net.ShopSyncEvents;
import net.lazy.kobe.world.*;
import net.lazy.kobe.skills.SkillMenus;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

import net.lazy.kobe.oregen.gui.OreGenScreen;


import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import net.lazy.kobe.oregen.CobbleGenEvents;

import org.slf4j.Logger;

@Mod(KobeMod.MOD_ID)
public class KobeMod {

    public static final String MOD_ID = "kobe";
    public static final Logger LOGGER = LogUtils.getLogger();

    public KobeMod(IEventBus modEventBus, ModContainer modContainer) {

        // --------------------------------------------------
        // REGISTRIES
        // --------------------------------------------------
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.register(new GeneratorItemProtectionHandler());


        // Register custom menu type

        FarmingAttachment.register(modEventBus);
        MoneyAttachment.register(modEventBus);
        MiningAttachment.register(modEventBus);
        CombatAttachment.register(modEventBus);

        // --------------------------------------------------
        // NETWORK + SETUP
        // --------------------------------------------------
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NetworkHandler::register);
        modEventBus.addListener(this::addCreative);

        // --------------------------------------------------
        // GLOBAL EVENTS (server + gameplay systems)
        // --------------------------------------------------
        NeoForge.EVENT_BUS.register(EconEvents.class);
        NeoForge.EVENT_BUS.register(net.lazy.kobe.shop.ShopEvents.class);
        NeoForge.EVENT_BUS.register(CommandEvents.class);
        NeoForge.EVENT_BUS.register(new HubEvents());
        NeoForge.EVENT_BUS.register(ResetChatListener.class);
        NeoForge.EVENT_BUS.register(IslandProtectionEvents.class);
        NeoForge.EVENT_BUS.register(new CobbleGenEvents());
        OreGenAttachment.register(modEventBus);
        IslandOreGenAttachment.register(modEventBus);
        OreGenMenus.MENUS.register(modEventBus);
        SkillMenus.MENUS.register(modEventBus);
        NeoForge.EVENT_BUS.register(new MiningEvents());
        NeoForge.EVENT_BUS.register(new MiningCloneHandler());
        NeoForge.EVENT_BUS.register(new MiningJoinSync());
        NeoForge.EVENT_BUS.register(new MiningSpeedEvents());
        NeoForge.EVENT_BUS.register(new MiningDimensionSync());
        NeoForge.EVENT_BUS.register(new FarmingJoinSync());
        NeoForge.EVENT_BUS.register(new FarmingDimensionSync());
        // ⭐ Register crate JSON reload listener
        NeoForge.EVENT_BUS.addListener(KobeMod::registerReloadListeners);
    }

    // =========================================================
    //  LOAD CRATE LOOT JSON ON PACK RELOAD
    // =========================================================
    private static void registerReloadListeners(AddReloadListenerEvent event) {
        System.out.println("========== JSON LOADERS REGISTERED ==========");
        event.addListener(new CrateLootData());
        event.addListener(new MiningRewardManager()); // ⭐ THIS IS STEP 4
    }


    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[KobeMod] Common setup complete.");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BLACK_OPAL.get());
            event.accept(ModItems.RAW_BLACK_OPAL.get());
        }
    }

    // =========================================================
    //  CLIENT-SIDE (GUI, SCREENS, HUD)
    // =========================================================

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {

            // -----------------------------
            // ORE GEN
            // -----------------------------
            event.register(
                    OreGenMenus.ORE_GEN_MENU.value(),
                    OreGenScreen::new
            );

            // -----------------------------
            // SKILLS – HUB
            // -----------------------------
            event.register(
                    SkillMenus.SKILLS_MENU.value(),
                    SkillsScreen::new
            );

            // -----------------------------
            // SKILLS – PAGES
            // -----------------------------
            event.register(SkillMenus.MINING_MENU.value(), MiningScreen::new);
            event.register(SkillMenus.FARMING_MENU.value(), FarmingScreen::new);
            event.register(SkillMenus.COMBAT_MENU.value(), CombatScreen::new);
            event.register(SkillMenus.FISHING_MENU.value(), FishingScreen::new);
            event.register(SkillMenus.FORAGING_MENU.value(), ForagingScreen::new);
            event.register(SkillMenus.HUNTS_MENU.value(), HuntsScreen::new);
            event.register(SkillMenus.RUNECRAFTING_MENU.value(), RunecraftingScreen::new);
            event.register(SkillMenus.ENCHANTING_MENU.value(), EnchantingScreen::new);
            event.register(SkillMenus.ALCHEMY_MENU.value(), AlchemyScreen::new);
        }

        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "money_hud"),
                    MoneyHudOverlay.INSTANCE
            );
        }
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        System.out.println("[KobeShop] Server about to start — loading shop categories");
        ShopCategoryLoader.load();
    }

    @EventBusSubscriber(
            modid = KobeMod.MOD_ID,
            value = Dist.CLIENT
    )
    public static class ClientReloads {

        @SubscribeEvent
        public static void onRegisterReloadListeners(
                RegisterClientReloadListenersEvent event
        ) {
            event.registerReloadListener(
                    new ShopCategoryClientReloadListener()
            );
        }
    }
}


