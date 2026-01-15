package net.lazy.kobe.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Set;

public class CombatData {

    public static final int MAX_LEVEL = 45;

    public static final Codec<CombatData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("level").forGetter(d -> d.level),
                    Codec.FLOAT.fieldOf("xp").forGetter(d -> d.xp),
                    Codec.INT.listOf().fieldOf("claimedRewards")
                            .forGetter(d -> d.claimedRewards.stream().toList())
            ).apply(instance, (level, xp, claimedList) -> {
                CombatData data = new CombatData();
                data.level = Math.max(0, level);
                data.xp = Math.max(0, xp);
                data.claimedRewards.addAll(claimedList);
                return data;
            }));


    private int level = 0;
    private float xp = 0;

    private final Set<Integer> claimedRewards = new HashSet<>();

    /* ===============================
     *  LEVEL / XP
     * =============================== */

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(0, level);
    }

    public float getXp() {
        return xp;
    }

    public int getXpIntoLevel() {
        return (int) xp;
    }

    public int getXpForNextLevel() {
        return (int) xpForNextLevel();
    }

    public float getProgress() {
        return Mth.clamp(xp / xpForNextLevel(), 0f, 1f);
    }

    public void addXp(float amount) {
        // ⛔ block phantom XP after reset
        if (amount <= 0) return;
        if (level == 0 && xp == 0 && amount >= xpForNextLevel()) return;

        if (level >= MAX_LEVEL) return;

        xp += amount;

        while (xp >= xpForNextLevel() && level < MAX_LEVEL) {
            xp -= xpForNextLevel();
            level++;
        }
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setClaimedRewards(Set<Integer> claimed) {
        this.claimedRewards.clear();
        this.claimedRewards.addAll(claimed);
    }

    private float xpForNextLevel() {
        return 100 + (level * 25);
    }

    /* ===============================
     *  REWARD CLAIMING
     * =============================== */

    public boolean isRewardClaimed(int level) {
        return claimedRewards.contains(level);
    }

    public void claimReward(int level) {
        claimedRewards.add(level);
    }

    public void reset() {
        this.level = 0;
        this.xp = 0;
        this.claimedRewards.clear();
    }

    public void claimAllUpTo(int level) {
        for (int i = 1; i <= level; i++) {
            claimedRewards.add(i);
        }
    }

    public Set<Integer> getClaimedRewards() {
        return claimedRewards;
    }

    public void copyFrom(int level, float xp, Set<Integer> claimed) {
        this.level = Math.max(0, level);
        this.xp = Math.max(0, xp);
        this.claimedRewards.clear();
        this.claimedRewards.addAll(claimed);
    }
}
