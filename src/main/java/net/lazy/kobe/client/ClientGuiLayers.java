package net.lazy.kobe.client;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.mining.MiningLevelUpToast;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(
        modid = KobeMod.MOD_ID,
        value = Dist.CLIENT,
        bus = Bus.MOD // 🔑 THIS IS THE FIX
)
public final class ClientGuiLayers {

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {

        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath("kobe", "mining_level_up_overlay"),
                (guiGraphics, delta) -> MiningLevelUpToast.render(guiGraphics)
        );
    }
}
