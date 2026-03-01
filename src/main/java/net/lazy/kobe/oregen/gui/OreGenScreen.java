package net.lazy.kobe.oregen.gui;

import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.oregen.data.OreGenTierLoader;
import net.lazy.kobe.oregen.network.OreGenUpgradePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OreGenScreen extends AbstractContainerScreen<OreGenMenu> {

    private static final int MAX_TIER = 5;

    // --- COLORS ---
    private static final int BG     = 0xCC0E0E12;
    private static final int BORDER = 0xFF1F1F2A;
    private static final int GOLD   = 0xFFF5C97A;
    private static final int GREEN  = 0xFF55FF55;
    private static final int PURPLE = 0xFFB38CFF;
    private static final int MUTED  = 0xFFB0B0B0;
    private static final int NETHER = 0xFF7F00FF;

    private Button upgradeButton;

    public OreGenScreen(OreGenMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 120;
    }

    /* ------------------------------------------------------------
     * INIT
     * ------------------------------------------------------------ */

    @Override
    protected void init() {
        super.init();

        int level = menu.getLevel();
        boolean isMax = level >= MAX_TIER;

        upgradeButton = addRenderableWidget(
                Button.builder(
                                Component.literal(isMax ? "MAX LEVEL" : "⚡ UPGRADE ⚡"),
                                btn -> {
                                    if (!isMax && minecraft.player != null) {
                                        NetworkHandler.sendToServer(OreGenUpgradePacket.INSTANCE);
                                    }
                                }
                        )
                        .bounds(leftPos + 30, topPos + imageHeight - 28, 116, 20)
                        .build()
        );

        upgradeButton.active = !isMax;
        upgradeButton.setAlpha(isMax ? 0.6f : 1.0f);
    }

    /* ------------------------------------------------------------
     * RENDER
     * ------------------------------------------------------------ */

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        renderUpgradeTooltip(gfx, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        int level = menu.getLevel();

        gfx.drawString(font, "♦ Ore Generator Status", 12, 8, GOLD, false);

        gfx.drawString(
                font,
                "Level: " + level + (level >= MAX_TIER ? " (MAX)" : ""),
                12,
                34,
                GREEN,
                false
        );

        gfx.drawString(
                font,
                "Tier: " + getTierName(level),
                12,
                46,
                PURPLE,
                false
        );

        boolean isMax = level >= MAX_TIER;

        gfx.drawString(
                font,
                isMax
                        ? "Hover to view current output"
                        : "Hover upgrade to view next output",
                12,
                64,
                MUTED,
                false
        );
    }

    /* ------------------------------------------------------------
     * TOOLTIP (JSON-DRIVEN)
     * ------------------------------------------------------------ */

    private void renderUpgradeTooltip(GuiGraphics gfx, int mouseX, int mouseY) {
        if (upgradeButton == null || !upgradeButton.isHovered()) return;

        int currentLevel = menu.getLevel();
        boolean isMax = currentLevel >= MAX_TIER;

        int displayTier = isMax ? currentLevel : currentLevel + 1;

        List<Component> tooltip = new ArrayList<>();

        tooltip.add(
                Component.literal(isMax ? "Current Output (MAX)" : "Next Tier Output")
                        .withStyle(style -> style.withBold(true))
        );

        tooltip.add(Component.literal(" "));

        tooltip.addAll(OreGenTierLoader.getTierTooltip(displayTier));

        // Netherite reminder (only if the displayed tier is final tier)
        if (displayTier >= MAX_TIER) {
            tooltip.add(Component.literal(" "));
            tooltip.add(
                    Component.literal("♦ Netherite Block (0.001%)")
                            .withStyle(style -> style.withColor(NETHER).withBold(true))
            );
        }

        gfx.renderTooltip(
                font,
                tooltip,
                Optional.empty(),
                mouseX,
                mouseY
        );
    }

    /* ------------------------------------------------------------
     * HELPERS
     * ------------------------------------------------------------ */

    private String getTierName(int level) {
        return switch (level) {
            case 0 -> "Basic";
            case 1 -> "Coal Economy";
            case 2 -> "Stoneworks";
            case 3 -> "Industrial";
            case 4 -> "Advanced Industry";
            case 5 -> "Endgame";
            default -> "Unknown";
        };
    }

    /* ------------------------------------------------------------
     * BACKGROUND
     * ------------------------------------------------------------ */

    @Override
    protected void renderBg(GuiGraphics gfx, float partial, int mouseX, int mouseY) {

        gfx.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, BORDER);

        gfx.fill(
                leftPos + 2, topPos + 2,
                leftPos + imageWidth - 2, topPos + imageHeight - 2,
                BG
        );

        gfx.fill(
                leftPos + 2, topPos + 2,
                leftPos + imageWidth - 2, topPos + 20,
                0xFF16161E
        );

        gfx.fill(
                leftPos + 10, topPos + 30,
                leftPos + imageWidth - 10, topPos + 31,
                0x55FFFFFF
        );
    }
}
