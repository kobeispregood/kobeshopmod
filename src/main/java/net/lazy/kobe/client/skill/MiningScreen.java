package net.lazy.kobe.client.skill;

import net.lazy.kobe.client.overlay.MiningPerkDisplay;
import net.lazy.kobe.client.MiningRewardTooltip;
import net.lazy.kobe.client.mastery.MasteryScreenBase;
import net.lazy.kobe.mining.*;
import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.skills.MiningMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class MiningScreen extends MasteryScreenBase<MiningMenu> {

    public MiningScreen(MiningMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    private MiningData data() {
        return minecraft.player.getData(MiningAttachment.MINING);
    }

    /* ============================================================
     *  MASTERY DATA
     * ============================================================ */

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
     *  CLAIM ACTIONS
     * ============================================================ */

    @Override
    protected void onClaimLevel(int level) {
        if (data().getLevel() < level) return;
        if (data().isClaimed(level)) return;

        NetworkHandler.sendToServer(new MiningClaimLevelPacket(level));
    }

    @Override
    protected boolean hasClaimAll() {
        return true;
    }

    @Override
    protected void onClaimAll() {
        NetworkHandler.sendToServer(new MiningClaimAllPacket());
    }

    /* ============================================================
     *  UI TEXT
     * ============================================================ */

    @Override
    protected Component getTitleText() {
        return Component.literal("MINING MASTERY • Level " + getPlayerLevel());
    }

    /* ============================================================
     *  TOOLTIP (REWARDS + PERKS)
     * ============================================================ */

    @Override
    protected List<Component> getPerkTooltip(int level) {

        List<Component> tooltip = new ArrayList<>();

        // -------------------------
        // ALWAYS show rewards if present
        // -------------------------
        List<Component> rewards = MiningRewardTooltip.getTooltipForLevel(level);
        if (!rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("Rewards:")
                            .withStyle(ChatFormatting.GOLD)
            );
            tooltip.addAll(rewards);
            tooltip.add(Component.empty());
        }

        // -------------------------
        // PERK UNLOCKS (optional)
        // -------------------------
        boolean hasUnlocks = false;
        for (MiningPerkDisplay perk : MiningPerkDisplay.values()) {
            if (perk.unlockLevel == level) {
                hasUnlocks = true;
                tooltip.add(
                        Component.literal("• " + perk.title)
                                .withStyle(ChatFormatting.GREEN)
                );
            }
        }

        // -------------------------
        // NOTHING ELSE HERE
        // (Locked / Claimed text is handled by MasteryScreenBase)
        // -------------------------

        return tooltip;
        }
    }