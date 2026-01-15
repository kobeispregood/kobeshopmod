package net.lazy.kobe.client;

import net.lazy.kobe.shop.ShopCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CategoryScreen extends Screen {

    private static final int PANEL_W = 360;
    private static final int PANEL_H = 280;

    private static final int SLOT = 42;
    private static final int PAD  = 18;

    // Colors matching PageScreen
    private static final int GOLD_BORDER = 0xFFEBC46F;
    private static final int TEXT_COLOR  = 0xFFFFFFFF; // white
    private static final int SLOT_BORDER = 0xFF8B5A2B;
    private static final int SLOT_BG     = 0xFFF4F4F4;

    private boolean mouseDown = false;

    public CategoryScreen() {
        super(Component.literal("Shop Categories"));
    }

    @Override
    protected void init() {
        Minecraft.getInstance().options.menuBackgroundBlurriness().set(0);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float delta) {

        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int px = (this.width - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        int innerX = px + 4;
        int innerY = py + 4;
        int innerW = PANEL_W - 8;
        int innerH = PANEL_H - 8;

        // ===========================================================
        // MATCHING PAGE SCREEN BACKGROUND — 16px CHEST CHECKERBOARD
        // ===========================================================
        int light = 0xFF3A2E1F;
        int dark  = 0xFF2E2418;
        int tile  = 16;

        for (int ty = 0; ty < innerH; ty += tile) {
            for (int tx = 0; tx < innerW; tx += tile) {
                boolean alt = ((tx / tile) + (ty / tile)) % 2 == 0;
                int color = alt ? light : dark;

                int x2 = Math.min(innerX + tx + tile, innerX + innerW);
                int y2 = Math.min(innerY + ty + tile, innerY + innerH);

                gui.fill(innerX + tx, innerY + ty, x2, y2, color);
            }
        }

        // ===========================================================
        // MATCHING PAGE SCREEN GOLD BORDER (layered + corner accents)
        // ===========================================================
        gui.fill(px, py, px + PANEL_W, py + 4, GOLD_BORDER);
        gui.fill(px, py + PANEL_H - 4, px + PANEL_W, py + PANEL_H, GOLD_BORDER);
        gui.fill(px, py, px + 4, py + PANEL_H, GOLD_BORDER);
        gui.fill(px + PANEL_W - 4, py, px + PANEL_W, py + PANEL_H, GOLD_BORDER);

        int goldDark = 0xFFB8964E;
        gui.fill(px + 4, py + 4, px + PANEL_W - 4, py + 6, goldDark);
        gui.fill(px + 4, py + PANEL_H - 6, px + PANEL_W - 4, py + PANEL_H - 4, goldDark);
        gui.fill(px + 4, py + 4, px + 6, py + PANEL_H - 4, goldDark);
        gui.fill(px + PANEL_W - 6, py + 4, px + PANEL_W - 4, py + PANEL_H - 4, goldDark);

        int goldLight = 0xFFF3DFA8;
        gui.fill(px + 4, py + 4, px + 8, py + 8, goldLight);
        gui.fill(px + PANEL_W - 8, py + 4, px + PANEL_W - 4, py + 8, goldLight);
        gui.fill(px + 4, py + PANEL_H - 8, px + 8, py + PANEL_H - 4, goldLight);
        gui.fill(px + PANEL_W - 8, py + PANEL_H - 8, px + PANEL_W - 4, py + PANEL_H - 4, goldLight);

        // ===========================================================
        // TITLE — centered, larger, cleaner spacing
        // ===========================================================
        String title = "Shop Categories";
        int titleY = py + 32;

        gui.drawString(
                this.font,
                title,
                this.width / 2 - this.font.width(title) / 2,
                titleY,
                TEXT_COLOR,
                false
        );

        // ===========================================================
        // CATEGORY GRID
        // ===========================================================
        ShopCategory[] cats = ShopCategory.values();

        int cols = 4;
        int rows = (int) Math.ceil(cats.length / (double) cols);

        int gridW = cols * SLOT + (cols - 1) * PAD;
        int startX = this.width / 2 - gridW / 2;

        // closer to title, matches PageScreen layout
        int startY = py + 70;

        for (int i = 0; i < cats.length; i++) {

            ShopCategory cat = cats[i];

            int col = i % cols;
            int row = i / cols;

            int x = startX + col * (SLOT + PAD);
            int y = startY + row * (SLOT + PAD);

            gui.fill(x, y, x + SLOT, y + SLOT, SLOT_BORDER);
            gui.fill(x + 2, y + 2, x + SLOT - 2, y + SLOT - 2, SLOT_BG);

            gui.renderItem(cat.getIcon(), x + SLOT / 2 - 8, y + SLOT / 2 - 8);

            boolean hover = mx >= x && mx < x + SLOT && my >= y && my < y + SLOT;

            if (hover) {
                gui.renderTooltip(
                        this.font,
                        cat.getDisplayName(),
                        mx, my
                );
            }

            // Click → open PageScreen
            if (hover && mouseDown) {
                Minecraft.getInstance().setScreen(new PageScreen(cat));
                mouseDown = false;
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        mouseDown = true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        mouseDown = false;
        return super.mouseReleased(mx, my, button);
    }

    public void renderBackground(GuiGraphics gui) {
        // Disable vanilla dark overlay
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
