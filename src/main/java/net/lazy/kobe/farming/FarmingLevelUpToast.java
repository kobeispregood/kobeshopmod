package net.lazy.kobe.farming;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.ChatFormatting;

public class FarmingLevelUpToast implements Toast {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/gui/toasts.png"
            );

    private final int level;
    private boolean playedSound = false;

    public FarmingLevelUpToast(int level) {
        this.level = level;
    }

    @Override
    public Visibility render(GuiGraphics g, ToastComponent toasts, long time) {

        // background (unchanged)
        g.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());

        // title line (unchanged)
        g.drawString(
                toasts.getMinecraft().font,
                Component.literal("Farming Level Up!")
                        .withStyle(ChatFormatting.GOLD),
                30,
                7,
                0xFFFFFF,
                false
        );

        // subtitle line (FIXED: gray text + gold number)
        g.drawString(
                toasts.getMinecraft().font,
                Component.literal("Reached Level ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(
                                Component.literal(String.valueOf(level))
                                        .withStyle(ChatFormatting.GOLD)
                        ),
                30,
                18,
                0xFFFFFF,
                false
        );

        // sound (unchanged)
        if (!playedSound) {
            playedSound = true;

            if (toasts.getMinecraft().player != null) {
                toasts.getMinecraft().player.playSound(
                        SoundEvents.PLAYER_LEVELUP,
                        1.0f,
                        1.0f
                );
            }
        }

        // lifetime (unchanged)
        return time >= 5000L
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    public void renderIcon(GuiGraphics g, int x, int y) {
        // no icon
    }

    // helper (unchanged)
    public static void show(int level) {
        Minecraft.getInstance().getToasts()
                .addToast(new FarmingLevelUpToast(level));
    }
}
