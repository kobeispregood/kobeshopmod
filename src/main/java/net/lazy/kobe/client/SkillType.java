package net.lazy.kobe.client;

import net.lazy.kobe.combat.CombatAttachment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

import net.lazy.kobe.mining.MiningAttachment;
import net.lazy.kobe.farming.FarmingAttachment;

public enum SkillType {

    COMBAT("Combat") {
        @Override
        public int getLevel() {
            return Minecraft.getInstance()
                    .player
                    .getData(CombatAttachment.COMBAT)
                    .getLevel();
        }

        @Override
        public int getProgressPercent() {
            var data = Minecraft.getInstance()
                    .player
                    .getData(CombatAttachment.COMBAT);

            int into = data.getXpIntoLevel();
            int total = data.getXpForNextLevel();
            return total <= 0 ? 0 : (int) ((into / (float) total) * 100f);
        }

        @Override
        public ChatFormatting getTierColor() {
            return ChatFormatting.RED;
        }
    },

    MINING("Mining") {
        @Override
        public int getLevel() {
            return Minecraft.getInstance()
                    .player
                    .getData(MiningAttachment.MINING)
                    .getLevel();
        }

        @Override
        public int getProgressPercent() {
            var data = Minecraft.getInstance()
                    .player
                    .getData(MiningAttachment.MINING);

            int into = data.getXpIntoLevel();
            int total = data.getXpForNextLevel();
            return total <= 0 ? 0 : (int) ((into / (float) total) * 100f);
        }
    },

    FARMING("Farming") {
        @Override
        public int getLevel() {
            return Minecraft.getInstance()
                    .player
                    .getData(FarmingAttachment.FARMING)
                    .getLevel();
        }

        @Override
        public int getProgressPercent() {
            var data = Minecraft.getInstance()
                    .player
                    .getData(FarmingAttachment.FARMING);

            int into = data.getXpIntoLevel();
            int total = data.getXpForNextLevel();
            return total <= 0 ? 0 : (int) ((into / (float) total) * 100f);
        }
    };


    public final String displayName;

    SkillType(String displayName) {
        this.displayName = displayName;
    }

    public abstract int getLevel();
    public abstract int getProgressPercent();

    public ChatFormatting getTierColor() {
        int lvl = getLevel();

        if (lvl >= 40) return ChatFormatting.GOLD;
        if (lvl >= 25) return ChatFormatting.LIGHT_PURPLE;
        if (lvl >= 10) return ChatFormatting.GREEN;
        return ChatFormatting.GRAY;
    }
}
