package net.lazy.kobe.mobs;

public enum MobTier {
    TIER_1(20, 3),
    TIER_2(35, 5),
    TIER_3(60, 8),
    TIER_4(100, 12),
    TIER_5(200, 18);

    public final double health;
    public final double damage;

    MobTier(double health, double damage) {
        this.health = health;
        this.damage = damage;
    }
}