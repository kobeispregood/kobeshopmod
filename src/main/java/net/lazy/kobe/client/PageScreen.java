package net.lazy.kobe.client;

import net.lazy.kobe.network.BuyPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.network.SellPacket;
import net.lazy.kobe.shop.PriceEntry;
import net.lazy.kobe.shop.ShopCategory;
import net.lazy.kobe.shop.ShopData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PageScreen extends Screen {

    private final ShopCategory category;
    private final List<ItemStack> items;

    private static final int COLUMNS = 5;
    private static final int ROWS = 5;
    private static final int ITEMS_PER_PAGE = COLUMNS * ROWS;

    private static final int SLOT = 42;
    private static final int PAD  = 12;

    private static final int PANEL_W = 360;
    private static final int PANEL_H = 360;

    private int page = 0;

    // Colors
    private static final int GOLD_BORDER  = 0xFFEBC46F;
    private static final int TEXT_COLOR   = 0xFFFFFFFF; // WHITE text
    private static final int SLOT_BORDER  = 0xFF8B5A2B;
    private static final int SLOT_BG      = 0xFFF4F4F4;
    private static final int HOVER_COLOR  = 0x44EBC46F;

    public PageScreen(ShopCategory category) {
        super(category.getDisplayName());
        this.category = category;
        this.items = ShopData.getItems(category);
    }

    @Override
    protected void init() {

        Minecraft.getInstance().options.menuBackgroundBlurriness().set(0);

        int cx = this.width / 2;
        int bottom = this.height / 2 + PANEL_H / 2 - 35;

        addRenderableWidget(Button.builder(Component.literal("← Prev"), b -> changePage(-1))
                .bounds(cx - 120, bottom, 70, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Back"),
                        b -> Minecraft.getInstance().setScreen(new CategoryScreen()))
                .bounds(cx - 35, bottom, 70, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Next →"), b -> changePage(1))
                .bounds(cx + 50, bottom, 70, 20).build());
    }

    private void changePage(int dir) {
        int max = (int) Math.ceil(items.size() / (double) ITEMS_PER_PAGE);
        page = Math.max(0, Math.min(page + dir, max - 1));
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float delta) {

        int px = (this.width - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        int innerX = px + 4;
        int innerY = py + 4;
        int innerW = PANEL_W - 8;
        int innerH = PANEL_H - 8;

        // ===========================================================
        // CHEST-LIKE CHECKERBOARD BACKGROUND (FAST)
        // ===========================================================
        int light = 0xFF3A2E1F;
        int dark  = 0xFF2E2418;
        int tile = 16;

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
        // ENHANCED GOLD BORDER (layered, corner highlights)
        // ===========================================================

        // Outer border
        gui.fill(px, py, px + PANEL_W, py + 4, GOLD_BORDER);
        gui.fill(px, py + PANEL_H - 4, px + PANEL_W, py + PANEL_H, GOLD_BORDER);
        gui.fill(px, py, px + 4, py + PANEL_H, GOLD_BORDER);
        gui.fill(px + PANEL_W - 4, py, px + PANEL_W, py + PANEL_H, GOLD_BORDER);

        // Inner darker gold
        int goldDark = 0xFFB8964E;
        gui.fill(px + 4, py + 4, px + PANEL_W - 4, py + 6, goldDark);
        gui.fill(px + 4, py + PANEL_H - 6, px + PANEL_W - 4, py + PANEL_H - 4, goldDark);
        gui.fill(px + 4, py + 4, px + 6, py + PANEL_H - 4, goldDark);
        gui.fill(px + PANEL_W - 6, py + 4, px + PANEL_W - 4, py + PANEL_H - 4, goldDark);

        // Corner highlights
        int goldLight = 0xFFF3DFA8;
        gui.fill(px + 4, py + 4, px + 8, py + 8, goldLight);
        gui.fill(px + PANEL_W - 8, py + 4, px + PANEL_W - 4, py + 8, goldLight);
        gui.fill(px + 4, py + PANEL_H - 8, px + 8, py + PANEL_H - 4, goldLight);
        gui.fill(px + PANEL_W - 8, py + PANEL_H - 8, px + PANEL_W - 4, py + PANEL_H - 4, goldLight);

        // ===========================================================
        // TITLE + PAGE LABEL (moved slightly DOWN for spacing)
        // ===========================================================

        int titleY = py + 22;      // was 10 → lowered
        int pageLabelY = py + 40;  // was 28 → lowered

        String title = category.getDisplayName().getString();
        gui.drawString(this.font, title,
                this.width / 2 - this.font.width(title) / 2,
                titleY, TEXT_COLOR, false);

        String pageLabel = "Page " + (page + 1);
        gui.drawString(this.font, pageLabel,
                this.width / 2 - this.font.width(pageLabel) / 2,
                pageLabelY, TEXT_COLOR, false);

        // ===========================================================
        // ITEM GRID
        // ===========================================================
        int gridW = COLUMNS * SLOT + (COLUMNS - 1) * PAD;
        int startX = this.width / 2 - gridW / 2;
        int startY = py + 60; //

        int startIndex = page * ITEMS_PER_PAGE;

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {

            int index = startIndex + i;
            if (index >= items.size()) break;

            ItemStack stack = items.get(index);

            int col = i % COLUMNS;
            int row = i / COLUMNS;

            int x = startX + col * (SLOT + PAD);
            int y = startY + row * (SLOT + PAD);

            // Slot frame + inner
            gui.fill(x, y, x + SLOT, y + SLOT, SLOT_BORDER);
            gui.fill(x + 2, y + 2, x + SLOT - 2, y + SLOT - 2, SLOT_BG);

            boolean hover = mx >= x && mx < x + SLOT && my >= y && my < y + SLOT;
            if (hover)
                gui.fill(x, y, x + SLOT, y + SLOT, HOVER_COLOR);

            gui.renderItem(stack, x + SLOT / 2 - 8, y + SLOT / 2 - 8);

            if (hover) {
                String key = ShopData.getKeyForStack(stack);
                PriceEntry price = ShopData.getPrice(key);

                gui.renderComponentTooltip(
                        this.font,
                        List.of(
                                stack.getHoverName(),
                                Component.literal("Buy: $" + price.buy()).withColor(0x55FF55),
                                Component.literal("Sell: $" + price.sell()).withColor(0xFF5555)
                        ),
                        mx, my
                );
            }
        }

        for (var child : this.renderables) {
            if (child instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
                widget.render(gui, mx, my, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {

        int gridW = COLUMNS * SLOT + (COLUMNS - 1) * PAD;
        int startX = this.width / 2 - gridW / 2;
        int startY = this.height / 2 - PANEL_H / 2 + 50;

        int startIndex = page * ITEMS_PER_PAGE;

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {

            int index = startIndex + i;
            if (index >= items.size()) break;

            ItemStack stack = items.get(index);

            int col = i % COLUMNS;
            int row = i / COLUMNS;

            int x = startX + col * (SLOT + PAD);
            int y = startY + row * (SLOT + PAD);

            boolean inside = mx >= x && mx < x + SLOT && my >= y && my < y + SLOT;
            if (!inside) continue;

            boolean right = button == 1;
            boolean shift = hasShiftDown();
            int amount = shift ? 64 : 1;

            String key = ShopData.getKeyForStack(stack);

            if (right) {
                NetworkHandler.sendToServer(new SellPacket(key, amount));
                Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_HAT.value(), 1f, 1.3f);
            } else {
                NetworkHandler.sendToServer(new BuyPacket(key, amount));
                Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 1.9f);
            }

            return true;
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
