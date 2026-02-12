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

public class ForagingScreen extends MasteryScreenBase<MasteryMenu> {

    public ForagingScreen(MasteryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    private MasteryProgress data() {
        return minecraft.player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.FORAGING);
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
                new MasteryClaimAllPacket(MasteryType.FORAGING)
        );
    }

    @Override
    protected Component getTitleText() {
        return Component.literal("FORAGING MASTERY • Level " + getPlayerLevel());
    }

    @Override
    protected List<Component> getPerkTooltip(int level) {
        List<Component> tooltip = new ArrayList<>();
        System.out.println("Fishing tooltip for level " + level);

        // Pull rewards via the unified reward system
        List<Component> rewards =
                MasteryRewardTooltip.getTooltip(MasteryType.FORAGING, level);

        if (!rewards.isEmpty()) {
            tooltip.add(
                    Component.literal("Rewards:")
                            .withStyle(ChatFormatting.GOLD)
            );
            tooltip.addAll(rewards);
        }

        return tooltip;
    }
}
