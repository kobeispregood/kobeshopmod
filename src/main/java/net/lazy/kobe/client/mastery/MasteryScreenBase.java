package net.lazy.kobe.client.mastery;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class MasteryScreenBase<T extends AbstractContainerMenu>
        extends AbstractContainerScreen<T> {

    protected static final int MAX_LEVELS = 45;

    protected static final ResourceLocation CHEST_BG =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/gui/container/generic_54.png"
            );

    protected List<Component> pendingTooltip = null;

    protected MasteryScreenBase(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 136;
    }

    /* ============================================================
     *  ABSTRACT HOOKS
     * ============================================================ */

    protected abstract int getPlayerLevel();
    protected abstract int getXpIntoLevel();
    protected abstract int getXpForNextLevel();
    protected abstract boolean isLevelClaimed(int level);
    protected abstract void onClaimLevel(int level);
    protected abstract Component getTitleText();
    protected abstract List<Component> getPerkTooltip(int level);

    // Optional Claim All support
    protected boolean hasClaimAll() {
        return false;
    }

    protected void onClaimAll() {}

    /* ============================================================
     *  INIT
     * ============================================================ */

    @Override
    protected void init() {
        super.init();

        if (!hasClaimAll()) return;

        int buttonWidth = 110;
        int buttonHeight = 20;

        // Center horizontally on screen
        int x = (this.width - buttonWidth) / 2;

        // 🔒 Anchor directly under the GUI (fullscreen safe)
        int y = topPos + imageHeight + 8;

        addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(
                        Component.literal("Claim All"),
                        btn -> onClaimAll()
                ).bounds(x, y, buttonWidth, buttonHeight).build()
        );
    }

    /* ============================================================
     *  RENDER
     * ============================================================ */

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {

        pendingTooltip = null;

        int level = getPlayerLevel();
        float progress = Mth.clamp(
                (float) getXpIntoLevel() / Math.max(1, getXpForNextLevel()),
                0f,
                1f
        );

        // Header
        int headerH = 36;
        int headerX = leftPos;
        int headerY = topPos - headerH - 4;

        g.fill(headerX, headerY, headerX + imageWidth, headerY + headerH, 0xFF0E0E0E);
        g.renderOutline(headerX, headerY, imageWidth, headerH, 0xFF000000);

        g.drawCenteredString(
                font,
                getTitleText(),
                leftPos + imageWidth / 2,
                headerY + 6,
                masteryColor(level)
        );

        // XP bar
        int barX = leftPos + 12;
        int barY = headerY + 20;
        int barW = imageWidth - 24;

        g.fill(barX, barY, barX + barW, barY + 10, 0xFF2A2A2A);
        g.fill(barX, barY, barX + (int) (barW * progress), barY + 10, masteryColor(level));
        g.renderOutline(barX, barY, barW, 10, 0xFF000000);

        g.drawCenteredString(
                font,
                getXpIntoLevel() + " / " + getXpForNextLevel() + " XP",
                leftPos + imageWidth / 2,
                barY + 1,
                0xFFFFFFFF
        );

        // Chest background
        g.blit(CHEST_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Panes
        int mx = mouseX - leftPos;
        int my = mouseY - topPos;

        int levelIndex = 1;

        for (int slot = 0; slot < 54 && levelIndex <= MAX_LEVELS; slot++) {

            int row = slot / 9;
            if (row >= 6) break;

            int x = 8 + (slot % 9) * 18;
            int y = 18 + row * 18;

            int lvl = levelIndex;

            boolean reached = level >= lvl;
            boolean claimed = isLevelClaimed(lvl);

            ItemStack pane =
                    !reached ? Items.GRAY_STAINED_GLASS_PANE.getDefaultInstance()
                            : claimed ? Items.LIME_STAINED_GLASS_PANE.getDefaultInstance()
                            : Items.YELLOW_STAINED_GLASS_PANE.getDefaultInstance();

            g.renderItem(pane, leftPos + x, topPos + y);

            if (mx >= x && mx < x + 18 && my >= y && my < y + 18) {

                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal("Level " + lvl).withStyle(ChatFormatting.GOLD));
                tooltip.addAll(getPerkTooltip(lvl));
                tooltip.add(Component.empty());

                if (!reached)
                    tooltip.add(Component.literal("Locked").withStyle(ChatFormatting.RED));
                else if (claimed)
                    tooltip.add(Component.literal("Claimed").withStyle(ChatFormatting.GREEN));
                else
                    tooltip.add(Component.literal("Click to claim").withStyle(ChatFormatting.GOLD));

                pendingTooltip = tooltip;
            }

            levelIndex++;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        if (pendingTooltip != null) {
            g.renderTooltip(font, pendingTooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {}

    /* ============================================================
     *  CLICK HANDLING
     * ============================================================ */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int mx = (int) mouseX - leftPos;
        int my = (int) mouseY - topPos;

        for (int slot = 0; slot < 54 && slot < MAX_LEVELS; slot++) {

            int row = slot / 9;
            int col = slot % 9;

            int x = 8 + col * 18;
            int y = 18 + row * 18;

            if (mx >= x && mx < x + 18 && my >= y && my < y + 18) {
                onClaimLevel(slot + 1);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /* ============================================================
     *  COLOR
     * ============================================================ */

    protected static int masteryColor(int level) {
        if (level >= 40) return 0xFFFF55FF;
        if (level >= 30) return 0xFFAA55FF;
        if (level >= 20) return 0xFF55FFFF;
        if (level >= 10) return 0xFF55FF55;
        if (level >= 5)  return 0xFFDFB04B;
        return 0xFF3AFF3A;
    }
}
