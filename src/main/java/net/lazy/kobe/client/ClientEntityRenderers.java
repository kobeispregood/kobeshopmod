package net.lazy.kobe.client;

import net.lazy.kobe.client.render.CopengamblerRenderer;
import net.lazy.kobe.client.render.GambleScreenRenderer;
import net.lazy.kobe.client.render.RockProjectileRenderer;
import net.lazy.kobe.client.render.StoneColossusRenderer;
import net.lazy.kobe.registry.ModEntities;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
        modid = "kobe",
        value = Dist.CLIENT
)
public final class ClientEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(
                ModEntities.ROCK_PROJECTILE.get(),
                RockProjectileRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.STONE_COLOSSUS.get(),
                StoneColossusRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.COPENGAMBLER.get(),
                CopengamblerRenderer::new
        );

        // ✅ THIS WAS MISSING
        event.registerEntityRenderer(
                ModEntities.GAMBLE_SCREEN.get(),
                GambleScreenRenderer::new
        );
    }
}