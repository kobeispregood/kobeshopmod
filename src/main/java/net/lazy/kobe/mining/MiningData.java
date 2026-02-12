package net.lazy.kobe.mining;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MiningData implements INBTSerializable<CompoundTag> {

    private int xp = 0;

    // Server-truth claimed set (persisted!)
    private final Set<Integer> claimedLevels = new HashSet<>();

    // --------------------
    // BASIC ACCESS
    // --------------------
    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    public void setXpClient(int xp) {
        this.xp = xp;
    }

    public void addXp(int amount) {
        this.xp = Math.max(0, this.xp + amount);
    }

    public void reset() {
        this.xp = 0;
        this.claimedLevels.clear();
    }

    // --------------------
    // CLAIMED LEVELS
    // --------------------
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

    // --------------------
    // LEVEL MATH
    // --------------------
    public int getLevel() {
        int level = 0;
        while (xp >= getXpRequiredForLevel(level + 1)) {
            level++;
        }
        return level;
    }

    public int getXpRequiredForLevel(int level) {
        return level * level * 50;
    }

    public int getXpIntoLevel() {
        int level = getLevel();
        return xp - getXpRequiredForLevel(level);
    }

    public int getXpForNextLevel() {
        int level = getLevel();
        return getXpRequiredForLevel(level + 1) - getXpRequiredForLevel(level);
    }

    public float getProgress() {
        int denom = getXpForNextLevel();
        if (denom <= 0) return 0f;
        return (float) getXpIntoLevel() / (float) denom;
    }

    public void setLevel(int level) {
        if (level < 0) level = 0;
        this.xp = getXpRequiredForLevel(level);
    }

    // --------------------
    // NBT
    // --------------------
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
            for (int lvl : arr) claimedLevels.add(lvl);
        }
    }
    public void copyFrom(MiningData other) {
        this.xp = other.xp;
        this.claimedLevels.clear();
        this.claimedLevels.addAll(other.claimedLevels);
    }

}
