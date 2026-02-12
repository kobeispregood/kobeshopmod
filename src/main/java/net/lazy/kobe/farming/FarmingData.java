package net.lazy.kobe.farming;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FarmingData implements INBTSerializable<CompoundTag> {

    // =========================
    // CORE DATA
    // =========================
    private int xp = 0;

    // Server-truth claimed levels
    private final Set<Integer> claimedLevels = new HashSet<>();

    // =========================
    // XP
    // =========================
    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    public void addXp(int amount) {
        this.xp = Math.max(0, this.xp + amount);
    }

    // =========================
    // LEVEL MATH (STARTS AT 0 LIKE MINING)
    // =========================
    public int getLevel() {
        int level = 0;
        int remainingXp = xp;

        while (remainingXp >= getXpRequiredForLevel(level + 1)) {
            remainingXp -= getXpRequiredForLevel(level + 1);
            level++;
        }

        return level;
    }


    public int getXpRequiredForLevel(int level) {
        if (level <= 0) return 0;

        // Tuned to roughly hit your target milestones
        int base = 200;          // base difficulty
        int linear = 120;        // steady increase per level
        int quadratic = 6;       // late-game grind scaler

        return base
                + (level * linear)
                + (level * level * quadratic);
    }

    public int getXpIntoLevel() {
        int level = 0;
        int remainingXp = xp;

        while (remainingXp >= getXpRequiredForLevel(level + 1)) {
            remainingXp -= getXpRequiredForLevel(level + 1);
            level++;
        }

        return remainingXp;
    }


    public int getXpForNextLevel() {
        return getXpRequiredForLevel(getLevel() + 1);
    }


    public float getProgress() {
        int denom = getXpForNextLevel();
        if (denom <= 0) return 0f;
        return (float) getXpIntoLevel() / (float) denom;
    }

    public void setLevel(int targetLevel) {
        if (targetLevel < 0) targetLevel = 0;

        int totalXp = 0;

        for (int lvl = 1; lvl <= targetLevel; lvl++) {
            totalXp += getXpRequiredForLevel(lvl);
        }

        this.xp = totalXp;
    }

    // =========================
    // CLAIMED LEVELS
    // =========================
    public boolean isClaimed(int level) {
        return claimedLevels.contains(level);
    }

    public void claimLevel(int level) {
        claimedLevels.add(level);
    }

    public Set<Integer> getClaimedLevels() {
        return Collections.unmodifiableSet(claimedLevels);
    }

    public void setClaimedLevels(Set<Integer> levels) {
        claimedLevels.clear();
        if (levels != null) claimedLevels.addAll(levels);
    }

    public void resetClaims() {
        claimedLevels.clear();
    }

    // =========================
    // HELPERS (YOU USE THIS)
    // =========================
    public boolean hasReachedLevel(int level) {
        return getLevel() >= level;
    }

    public void reset() {
        this.xp = 0;
        this.claimedLevels.clear();
    }

    // =========================
    // NBT
    // =========================
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("xp", xp);

        int[] arr = claimedLevels.stream().mapToInt(i -> i).toArray();
        tag.put("claimedLevels", new IntArrayTag(arr));

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.xp = tag.getInt("xp");

        claimedLevels.clear();
        if (tag.contains("claimedLevels")) {
            int[] arr = tag.getIntArray("claimedLevels");
            for (int lvl : arr) {
                claimedLevels.add(lvl);
            }
        }
    }
    public void copyFrom(FarmingData other) {
        this.xp = other.xp;
        this.claimedLevels.clear();
        this.claimedLevels.addAll(other.claimedLevels);
    }
}
