package net.lazy.kobe.client;

import net.lazy.kobe.block.ModBlockEntities;
import net.lazy.kobe.client.mastery.MasteryScreen;
import net.lazy.kobe.crate.client.CrateRenderer;
import net.lazy.kobe.skills.MasteryMenu;
import net.lazy.kobe.skills.SkillMenus;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "kobe", value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(
                ModBlockEntities.CRATE_BE.get(),
                CrateRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.<MasteryMenu, MasteryScreen>register(
                SkillMenus.MASTERY_MENU.get(),
                MasteryScreen::new
        );
    }
}
