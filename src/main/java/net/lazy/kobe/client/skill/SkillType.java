package net.lazy.kobe.client.skill;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryData;
import net.lazy.kobe.mastery.MasteryType;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public enum SkillType {

    // =============================================================
    // NEW MASTERY SKILLS
    // =============================================================

    ENCHANTING("Enchanting", MasteryType.ENCHANTING),
    MINING("Mining", MasteryType.MINING),
    ALCHEMY("Alchemy", MasteryType.ALCHEMY),
    FISHING("Fishing", MasteryType.FISHING),
    FORAGING("Foraging", MasteryType.FORAGING),
    COMBAT("Combat", MasteryType.COMBAT),
    FARMING("Farming", MasteryType.FARMING),

    HUNTS("Hunts", MasteryType.HUNTS),
    RUNECRAFTING("Runecrafting", MasteryType.RUNECRAFTING);

    // =============================================================
    // SHARED LOGIC FOR MASTERY SKILLS
    // =============================================================

    public final String displayName;
    private final MasteryType masteryType;

    SkillType(String displayName) {
        this.displayName = displayName;
        this.masteryType = null;
    }

    SkillType(String displayName, MasteryType masteryType) {
        this.displayName = displayName;
        this.masteryType = masteryType;
    }

    public int getLevel() {
        if (masteryType == null) return 0;

        var player = Minecraft.getInstance().player;
        if (player == null) return 0;

        MasteryData mastery = player.getData(MasteryAttachment.MASTERY);
        return mastery.getOrCreate(masteryType).getLevel();
    }

    public int getProgressPercent() {
        if (masteryType == null) return 0;

        var player = Minecraft.getInstance().player;
        if (player == null) return 0;

        MasteryData mastery = player.getData(MasteryAttachment.MASTERY);
        var progress = mastery.getOrCreate(masteryType);

        int into = progress.getXpIntoLevel();
        int total = progress.getXpForNextLevel();
        return total <= 0 ? 0 : (int) ((into / (float) total) * 100f);
    }

    public ChatFormatting getTierColor() {
        int lvl = getLevel();

        if (lvl >= 40) return ChatFormatting.GOLD;
        if (lvl >= 25) return ChatFormatting.LIGHT_PURPLE;
        if (lvl >= 10) return ChatFormatting.GREEN;
        return ChatFormatting.GRAY;
    }
}
