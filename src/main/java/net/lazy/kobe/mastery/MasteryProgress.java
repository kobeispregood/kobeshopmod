package net.lazy.kobe.mastery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashSet;
import java.util.Set;

public class MasteryProgress {

    public static final Codec<MasteryProgress> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.fieldOf("level").forGetter(MasteryProgress::getLevel),
                    Codec.INT.fieldOf("xp").forGetter(MasteryProgress::getXp),
                    Codec.INT.listOf()
                            .optionalFieldOf("claimedLevels", java.util.List.of())
                            .forGetter(p -> p.claimedLevels.stream().toList())
            ).apply(inst, (level, xp, claimedList) -> {
                MasteryProgress p = new MasteryProgress(level, xp);
                p.claimedLevels.addAll(claimedList);
                return p;
            }));

    private int level;
    private int xp; // xp INTO current level
    private final Set<Integer> claimedLevels = new HashSet<>();
    public static final int MAX_LEVELS = 45;

    public MasteryProgress() {
        this(0, 0);
    }

    public MasteryProgress(int level, int xp) {
        this.level = Math.max(0, level);
        this.xp = Math.max(0, xp);
    }

    /* ===============================
     *  LEVEL / XP
     * =============================== */

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpIntoLevel() {
        return xp;
    }

    public int getXpForNextLevel() {
        return 100 + (level * 50);
    }

    public float getProgress() {
        int denom = getXpForNextLevel();
        if (denom <= 0) return 0f;
        return (float) xp / (float) denom;
    }

    public void addXp(int amount) {
        if (amount <= 0) return;

        xp += amount;

        while (xp >= getXpForNextLevel()) {
            xp -= getXpForNextLevel();
            level++;
        }
    }

    public void setLevel(int level) {
        this.level = Math.max(0, level);
        this.xp = 0;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    /* ===============================
     *  CLAIMED LEVELS
     * =============================== */

    public boolean isClaimed(int level) {
        return claimedLevels.contains(level);
    }

    public void claim(int level) {
        claimedLevels.add(level);
    }

    public Set<Integer> getClaimedLevels() {
        return claimedLevels;
    }

    public void resetClaims() {
        claimedLevels.clear();
    }
    private static int buildClaimedMask(MasteryProgress progress) {
        int mask = 0;
        for (int lvl : progress.getClaimedLevels()) {
            mask |= (1 << lvl);
        }
        return mask;
    }
}
