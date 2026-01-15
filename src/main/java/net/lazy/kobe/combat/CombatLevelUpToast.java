package net.lazy.kobe.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.ChatFormatting;

public class CombatLevelUpToast implements Toast {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/gui/toasts.png"
            );

    private final int level;
    private boolean playedSound = false;

    public CombatLevelUpToast(int level) {
        this.level = level;
    }

    @Override
    public Visibility render(GuiGraphics g, ToastComponent toasts, long time) {

        g.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());

        g.drawString(
                toasts.getMinecraft().font,
                Component.literal("Combat Level Up!")
                        .withStyle(ChatFormatting.RED),
                30,
                7,
                0xFFFFFF,
                false
        );

        g.drawString(
                toasts.getMinecraft().font,
                Component.literal("Reached Level " + level)
                        .withStyle(ChatFormatting.YELLOW),
                30,
                18,
                0xFFFFFF,
                false
        );

        if (!playedSound) {
            playedSound = true;
            toasts.getMinecraft().player.playSound(
                    SoundEvents.PLAYER_LEVELUP,
                    1.0f,
                    1.0f
            );
        }

        return time >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }

    /* ============================================================
     * CLIENT HELPER
     * ============================================================ */
    public static void show(int level) {
        Minecraft.getInstance()
                .getToasts()
                .addToast(new CombatLevelUpToast(level));
    }
}
