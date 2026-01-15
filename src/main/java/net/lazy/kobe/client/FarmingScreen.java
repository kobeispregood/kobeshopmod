package net.lazy.kobe.client;

import net.lazy.kobe.client.mastery.MasteryScreenBase;
import net.lazy.kobe.farming.*;
import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.skills.FarmingMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class FarmingScreen extends MasteryScreenBase<FarmingMenu> {

    private static final ResourceLocation CHEST_BG =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/gui/container/generic_54.png"
            );

    public FarmingScreen(FarmingMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    /* ============================================================
     *  DATA HOOKS
     * ============================================================ */

    private FarmingData data() {
        return minecraft.player.getData(FarmingAttachment.FARMING);
    }

    @Override
    protected int getPlayerLevel() {
        return data().getLevel();
    }

    @Override
    protected int getXpIntoLevel() {
        return data().getXpIntoLevel();
    }

    @Override
    protected int getXpForNextLevel() {
        return data().getXpForNextLevel();
    }

    @Override
    protected boolean isLevelClaimed(int level) {
        return data().isClaimed(level);
    }

    /* ============================================================
     *  UI TEXT
     * ============================================================ */

    @Override
    protected Component getTitleText() {
        return Component.literal("FARMING MASTERY • Level " + getPlayerLevel());
    }

    /* ============================================================
     *  TOOLTIP (REWARDS + PERKS) — MATCHES MINING
     * ============================================================ */

    @Override
    protected List<Component> getPerkTooltip(int level) {

        List<Component> tooltip = new ArrayList<>();

        // -------------------------
        // REWARDS
        // -------------------------
        List<Component> rewards =
                FarmingRewardTooltip.getTooltipForLevel(level);

        if (!rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("Rewards:")
                            .withStyle(ChatFormatting.GOLD)
            );
            tooltip.addAll(rewards);
            tooltip.add(Component.empty());
        }

        // -------------------------
        // PERK UNLOCKS
        // -------------------------
        boolean hasUnlocks = false;
        for (FarmingPerkDisplay perk : FarmingPerkDisplay.values()) {
            if (perk.unlockLevel == level) {
                hasUnlocks = true;
                tooltip.add(
                        Component.literal("• " + perk.title)
                                .withStyle(ChatFormatting.GREEN)
                );
            }
        }

        // -------------------------
        // NO UNLOCKS
        // -------------------------
        if (!hasUnlocks && rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("No unlocks")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        // Locked / Claimed text handled by MasteryScreenBase
        return tooltip;
    }

    /* ============================================================
     *  CLAIM HANDLING
     * ============================================================ */

    @Override
    protected void onClaimLevel(int level) {

        if (data().getLevel() < level) return;
        if (data().isClaimed(level)) return;

        NetworkHandler.sendToServer(
                new FarmingClaimLevelPacket(level)
        );
    }

    @Override
    protected boolean hasClaimAll() {
        return true;
    }

    @Override
    protected void onClaimAll() {
        NetworkHandler.sendToServer(
                new FarmingClaimAllPacket()
        );
    }

    /* ============================================================
     *  BACKGROUND
     * ============================================================ */

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        super.renderBg(g, partialTick, mouseX, mouseY);

        g.blit(
                CHEST_BG,
                leftPos,
                topPos,
                0,
                0,
                imageWidth,
                imageHeight
        );
    }
}
