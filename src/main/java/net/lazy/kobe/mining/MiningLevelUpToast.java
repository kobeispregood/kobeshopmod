package net.lazy.kobe.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public final class MiningLevelUpToast {

    // PNG size
    private static final int WIDTH = 160;
    private static final int HEIGHT = 32;

    // Timing
    private static final long DISPLAY_TIME = 4500L;
    private static final long ANIM_TIME = 250L;

    // Background PNG
    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "textures/gui/toast/skill_level.png"
            );

    private static long startTime = -1;
    private static int level = 0;
    private static boolean playedSound = false;

    private MiningLevelUpToast() {}

    /* ============================================================
     *  API
     * ============================================================ */

    public static void show(int lvl) {
        level = lvl;
        startTime = System.currentTimeMillis();
        playedSound = false;
    }

    /* ============================================================
     *  RENDER
     * ============================================================ */

    public static void render(GuiGraphics g) {
        if (startTime < 0) return;

        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

        if (elapsed > DISPLAY_TIME + ANIM_TIME) {
            startTime = -1;
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int screenW = mc.getWindow().getGuiScaledWidth();

        // ----------------------------
        // Slide animation
        // ----------------------------
        float anim;
        if (elapsed < ANIM_TIME) {
            anim = elapsed / (float) ANIM_TIME;
        } else if (elapsed > DISPLAY_TIME) {
            anim = 1f - ((elapsed - DISPLAY_TIME) / (float) ANIM_TIME);
        } else {
            anim = 1f;
        }

        // ⬆️ Up + ➡️ Right
        int x = screenW - (int) (WIDTH * anim) - 2;
        int y = 10;

        // ----------------------------
        // Play vanilla toast woosh
        // ----------------------------
        if (!playedSound) {
            playedSound = true;
            mc.player.playSound(
                    SoundEvents.UI_TOAST_IN,
                    1.0f,
                    1.0f
            );
        }

        // ----------------------------
        // Background (PNG)
        // ----------------------------
        g.blit(
                BG,
                x,
                y,
                0,
                0,
                WIDTH,
                HEIGHT,
                WIDTH,
                HEIGHT
        );

        // ----------------------------
        // Text (shifted right)
        // ----------------------------
        int textX = x + 42; // ⬅️ clears pickaxe icon cleanly

        g.drawString(
                mc.font,
                Component.literal("Mining Level Up!")
                        .withStyle(ChatFormatting.GREEN),
                textX,
                y + 7,
                0xFFFFFF,
                false
        );

        g.drawString(
                mc.font,
                Component.literal("Reached Level ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(
                                Component.literal(String.valueOf(level))
                                        .withStyle(ChatFormatting.GOLD)
                        ),
                textX,
                y + 18,
                0xFFFFFF,
                false
        );
    }
}
