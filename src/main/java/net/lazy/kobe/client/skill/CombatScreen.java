package net.lazy.kobe.client.skill;

import net.lazy.kobe.client.mastery.MasteryScreenBase;
import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.net.MasteryClaimAllPacket;
import net.lazy.kobe.mastery.net.MasteryClaimLevelPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.skills.CombatMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CombatScreen extends MasteryScreenBase<CombatMenu> {

    public CombatScreen(CombatMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    private MasteryProgress combatProgress() {
        return minecraft.player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.COMBAT);
    }


    /* ============================================================
     *  MASTERY DATA (ATTACHMENT ONLY)
     * ============================================================ */

    @Override
    protected int getPlayerLevel() {
        return combatProgress().getLevel();
    }

    @Override
    protected int getXpIntoLevel() {
        return combatProgress().getXpIntoLevel();
    }

    @Override
    protected int getXpForNextLevel() {
        return combatProgress().getXpForNextLevel();
    }

    @Override
    protected boolean isLevelClaimed(int level) {
        return combatProgress().isClaimed(level);
    }

    @Override
    protected void onClaimLevel(int level) {
        NetworkHandler.sendToServer(
                new MasteryClaimLevelPacket(
                        MasteryType.COMBAT,
                        level
                )
        );
    }

    @Override
    protected boolean hasClaimAll() {
        return true;
    }

    @Override
    protected void onClaimAll() {
        NetworkHandler.sendToServer(
                new MasteryClaimAllPacket(MasteryType.COMBAT)
        );
    }


    /* ============================================================
     *  UI TEXT
     * ============================================================ */

    @Override
    protected Component getTitleText() {
        return Component.literal("COMBAT MASTERY • Level " + getPlayerLevel())
                .withStyle(ChatFormatting.RED);
    }

    /* ============================================================
     *  SLOT CLICK HANDLING (CLAIM)
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

                // 🔇 Suppress default slot click
                onClaimLevel(slot + 1);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /* ============================================================
     *  TOOLTIP (REWARDS + PERKS) — MATCHES MINING/FARMING
     * ============================================================ */

    @Override
    protected List<Component> getPerkTooltip(int level) {

        List<Component> tooltip = new ArrayList<>();

        // -------------------------
        // REWARDS (JSON-DRIVEN)
        // -------------------------
        List<Component> rewards =
                net.lazy.kobe.mastery.rewards.MasteryRewardTooltip
                        .getTooltip(MasteryType.COMBAT, level);

        if (!rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("Rewards:")
                            .withStyle(ChatFormatting.GOLD)
            );
            tooltip.addAll(rewards);
            tooltip.add(Component.empty());
        }

        // -------------------------
        // PERK UNLOCKS (EXAMPLES)
        // -------------------------
        boolean hasUnlocks = false;

        if (level == 5) {
            hasUnlocks = true;
            tooltip.add(
                    Component.literal("• +2% Combat Damage")
                            .withStyle(ChatFormatting.GREEN)
            );
        }

        if (level == 10) {
            hasUnlocks = true;
            tooltip.add(
                    Component.literal("• Critical Hits Unlocked")
                            .withStyle(ChatFormatting.GREEN)
            );
        }

        if (level == 20) {
            hasUnlocks = true;
            tooltip.add(
                    Component.literal("• Lifesteal I")
                            .withStyle(ChatFormatting.GREEN)
            );
        }

        // -------------------------
        // FALLBACK
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
}
