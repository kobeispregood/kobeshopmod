package net.lazy.kobe.client.skill;

import net.lazy.kobe.client.mastery.MasteryScreenBase;
import net.lazy.kobe.mastery.*;
import net.lazy.kobe.mastery.net.*;
import net.lazy.kobe.mastery.rewards.MasteryRewardTooltip;
import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.skills.MasteryMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CombatScreen extends MasteryScreenBase<MasteryMenu> {

    public CombatScreen(MasteryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    private MasteryProgress data() {
        return minecraft.player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.COMBAT);
    }

    @Override protected int getPlayerLevel() { return data().getLevel(); }
    @Override protected int getXpIntoLevel() { return data().getXpIntoLevel(); }
    @Override protected int getXpForNextLevel() { return data().getXpForNextLevel(); }
    @Override protected boolean isLevelClaimed(int level) { return data().isClaimed(level); }

    @Override
    protected void onClaimLevel(int level) {
        NetworkHandler.sendToServer(
                new MasteryClaimLevelPacket(
                        this.menu.getMasteryType(),
                        level
                )
        );
    }

    @Override protected boolean hasClaimAll() { return true; }

    @Override
    protected void onClaimAll() {
        NetworkHandler.sendToServer(
                new MasteryClaimAllPacket(MasteryType.COMBAT)
        );
    }

    @Override
    protected Component getTitleText() {
        return Component.literal("COMBAT MASTERY • Level " + getPlayerLevel())
                .withStyle(ChatFormatting.RED);
    }

    @Override
    protected List<Component> getPerkTooltip(int level) {

        List<Component> tooltip = new ArrayList<>();

        // JSON-driven rewards
        List<Component> rewards =
                MasteryRewardTooltip.getTooltip(MasteryType.COMBAT, level);

        if (!rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("Rewards:")
                            .withStyle(ChatFormatting.GOLD)
            );
            tooltip.addAll(rewards);
            tooltip.add(Component.empty());
        }

        // Example perk unlocks
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

        if (!hasUnlocks && rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("No unlocks")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        return tooltip;
    }
}