package net.lazy.kobe.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

import net.lazy.kobe.econ.MoneyAttachment;

public class MoneyHudOverlay implements LayeredDraw.Layer {

    public static final MoneyHudOverlay INSTANCE = new MoneyHudOverlay();

    @Override
    public void render(GuiGraphics gfx, DeltaTracker partialTick) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int money = mc.player.getData(MoneyAttachment.MONEY).get();

        gfx.drawString(
                mc.font,
                "Money: $" + money,
                10,
                mc.getWindow().getGuiScaledHeight() - 20,
                0xFFDFA63A
        );
    }
}