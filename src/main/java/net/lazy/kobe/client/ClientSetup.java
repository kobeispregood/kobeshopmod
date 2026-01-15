package net.lazy.kobe.client;

import net.lazy.kobe.block.ModBlockEntities;
import net.lazy.kobe.crate.client.CrateRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "kobe", bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(
                ModBlockEntities.CRATE_BE.get(),
                CrateRenderer::new
        );
    }
}
