package net.lazy.kobe.mining;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MiningPerkUnlockToast implements Toast {

    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/gui/sprites/toast/system.png"
            );

    private final String perk;
    private long startTime;

    public MiningPerkUnlockToast(String perk) {
        this.perk = perk;
    }

    @Override
    public Visibility render(GuiGraphics g, ToastComponent toastGui, long time) {
        if (startTime == 0) startTime = time;

        g.blit(BG, 0, 0, 0, 0, 160, 32, 160, 32);

        g.drawString(
                toastGui.getMinecraft().font,
                Component.literal("Perk Unlocked"),
                30,
                7,
                0xFFDFB04B,
                false
        );

        g.drawString(
                toastGui.getMinecraft().font,
                Component.literal(perk),
                30,
                18,
                0xFFFFFFFF,
                false
        );

        return time - startTime >= 3000 ? Visibility.HIDE : Visibility.SHOW;
    }
}
