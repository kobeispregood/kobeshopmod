package net.lazy.kobe.client.mastery;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Function;

public class MasteryScreen extends MasteryScreenBase<net.lazy.kobe.skills.MasteryMenu> {

    private final Component title;
    private final IntSupplier level;
    private final IntSupplier xpInto;
    private final IntSupplier xpNext;
    private final LongSupplier claimedMask;
    private final IntConsumer claimLevel;
    private final Runnable claimAll;
    private final Function<Integer, List<Component>> tooltipProvider;

    public MasteryScreen(
            net.lazy.kobe.skills.MasteryMenu menu,
            net.minecraft.world.entity.player.Inventory inv,
            Component title,
            IntSupplier level,
            IntSupplier xpInto,
            IntSupplier xpNext,
            LongSupplier claimedMask,          // ✅ FIX
            IntConsumer claimLevel,
            Runnable claimAll,
            Function<Integer, List<Component>> tooltipProvider
    ) {
        super(menu, inv, title);
        this.title = title;
        this.level = level;
        this.xpInto = xpInto;
        this.xpNext = xpNext;
        this.claimedMask = claimedMask;
        this.claimLevel = claimLevel;
        this.claimAll = claimAll;
        this.tooltipProvider = tooltipProvider;
    }

    @Override
    protected int getPlayerLevel() {
        return level.getAsInt();
    }

    @Override
    protected int getXpIntoLevel() {
        return xpInto.getAsInt();
    }

    @Override
    protected int getXpForNextLevel() {
        return xpNext.getAsInt();
    }

    @Override
    protected boolean isLevelClaimed(int level) {
        return (claimedMask.getAsLong() & (1L << (level - 1))) != 0L;
    }

    @Override
    protected void onClaimLevel(int level) {
        claimLevel.accept(level);
    }

    @Override
    protected boolean hasClaimAll() {
        return true;
    }

    @Override
    protected void onClaimAll() {
        claimAll.run();
    }

    @Override
    protected Component getTitleText() {
        return title.copy()
                .append(
                        Component.literal(" • Level " + getPlayerLevel())
                                .withStyle(ChatFormatting.YELLOW)
                );
    }

    @Override
    protected List<Component> getPerkTooltip(int level) {
        return tooltipProvider.apply(level);
    }

    // =============================================================
    // CONVENIENCE CONSTRUCTOR (AUTO WIRES DATA)
    // =============================================================

    public MasteryScreen(
            net.lazy.kobe.skills.MasteryMenu menu,
            net.minecraft.world.entity.player.Inventory inv,
            Component title
    ) {
        this(
                menu,
                inv,
                title,

                // LEVEL
                () -> inv.player
                        .getData(net.lazy.kobe.mastery.MasteryAttachment.MASTERY)
                        .getOrCreate(menu.getMasteryType())
                        .getLevel(),

                // XP INTO LEVEL
                () -> inv.player
                        .getData(net.lazy.kobe.mastery.MasteryAttachment.MASTERY)
                        .getOrCreate(menu.getMasteryType())
                        .getXpIntoLevel(),

                // XP FOR NEXT LEVEL
                () -> inv.player
                        .getData(net.lazy.kobe.mastery.MasteryAttachment.MASTERY)
                        .getOrCreate(menu.getMasteryType())
                        .getXpForNextLevel(),

                // CLAIMED MASK (LONG — FIXED)
                () -> {
                    var progress = inv.player
                            .getData(net.lazy.kobe.mastery.MasteryAttachment.MASTERY)
                            .getOrCreate(menu.getMasteryType());

                    long mask = 0L;
                    for (int lvl : progress.getClaimedLevels()) {
                        mask |= (1L << (lvl - 1)); // ✅ CORRECT
                    }
                    return mask;
                },

                // CLAIM SINGLE
                lvl -> net.lazy.kobe.network.NetworkHandler.sendToServer(
                        new net.lazy.kobe.mastery.net.MasteryClaimLevelPacket(
                                menu.getMasteryType(), lvl
                        )
                ),

                // CLAIM ALL
                () -> net.lazy.kobe.network.NetworkHandler.sendToServer(
                        new net.lazy.kobe.mastery.net.MasteryClaimAllPacket(
                                menu.getMasteryType()
                        )
                ),

                // TOOLTIP (REWARDS)
                lvl -> {
                    var rewards =
                            net.lazy.kobe.mastery.rewards.MasteryRewardTooltip
                                    .getTooltip(menu.getMasteryType(), lvl);

                    if (rewards.isEmpty()) {
                        return List.of();
                    }

                    var tooltip = new java.util.ArrayList<Component>();
                    tooltip.add(
                            Component.literal("Rewards:")
                                    .withStyle(ChatFormatting.GOLD)
                    );
                    tooltip.addAll(rewards);
                    return tooltip;
                }
        );
    }
}
