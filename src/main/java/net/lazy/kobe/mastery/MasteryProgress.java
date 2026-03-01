package net.lazy.kobe.mastery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MasteryProgress {

    /* ============================================================
     *  CODEC (DATA SAVE / LOAD)
     * ============================================================ */

    public static final Codec<MasteryProgress> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.fieldOf("level").forGetter(MasteryProgress::getLevel),
                    Codec.INT.fieldOf("xp").forGetter(MasteryProgress::getXp),
                    Codec.INT.listOf()
                            .optionalFieldOf("claimedLevels", List.of())
                            .forGetter(p -> p.claimedLevels.stream().toList())
            ).apply(inst, (level, xp, claimedList) -> {
                MasteryProgress p = new MasteryProgress(level, xp);
                p.claimedLevels.addAll(claimedList);
                return p;
            }));

    /* ============================================================
     *  CONSTANTS
     * ============================================================ */

    public static final int MAX_LEVELS = 100;

    /* ============================================================
     *  DATA
     * ============================================================ */

    private int level;
    private int xp; // XP INTO CURRENT LEVEL
    private final Set<Integer> claimedLevels = new HashSet<>();

    /* ============================================================
     *  CONSTRUCTORS
     * ============================================================ */

    public MasteryProgress() {
        this(0, 0);
    }

    public MasteryProgress(int level, int xp) {
        this.level = Math.max(0, level);
        this.xp = Math.max(0, xp);
    }

    /* ============================================================
     *  LEVEL / XP
     * ============================================================ */

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
        // Smooth quadratic scaling for 100 levels
        return 100 + (level * level * 10);
    }

    public float getProgress() {
        int needed = getXpForNextLevel();
        if (needed <= 0) return 0f;
        return (float) xp / (float) needed;
    }

    public void addXp(int amount) {
        if (amount <= 0) return;

        xp += amount;

        while (level < MAX_LEVELS && xp >= getXpForNextLevel()) {
            xp -= getXpForNextLevel();
            level++;
        }

        // Clamp at max
        if (level >= MAX_LEVELS) {
            level = MAX_LEVELS;
            xp = 0;
        }
    }

    public void setLevel(int level) {
        this.level = Math.min(Math.max(0, level), MAX_LEVELS);
        this.xp = 0;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    /* ============================================================
     *  CLAIMED LEVELS
     * ============================================================ */

    public boolean isClaimed(int level) {
        return claimedLevels.contains(level);
    }

    public void claim(int level) {
        if (level > 0 && level <= MAX_LEVELS) {
            claimedLevels.add(level);
        }
    }

    public Set<Integer> getClaimedLevels() {
        return claimedLevels;
    }

    public void resetClaims() {
        claimedLevels.clear();
    }
}