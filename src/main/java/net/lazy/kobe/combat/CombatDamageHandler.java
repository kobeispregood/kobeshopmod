package net.lazy.kobe.combat;

import net.lazy.kobe.curios.CurioHelper;
import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.econ.MoneyAttachment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public final class CombatDamageHandler {

    private static final RandomSource RANDOM = RandomSource.create();

    private CombatDamageHandler() {}

    public static CombatResult apply(
            ServerPlayer player,
            LivingEntity target,
            float baseDamage
    ) {
        int level = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.COMBAT)
                .getLevel();

        CombatStats stats = CombatStats.fromLevel(level);
        stats = CombatStatsCalculator.applyCurioBonuses(player, stats);

        float damage = baseDamage;
        boolean crit = false;

        // -------------------------------------------------
        // BASE DAMAGE
        // -------------------------------------------------
        damage *= stats.baseDamageMultiplier();

        // -------------------------------------------------
        // ATTACK COOLDOWN (ANTI-SPAM)
        // -------------------------------------------------
        float attackStrength = player.getAttackStrengthScale(0.5f);
        boolean fullyCharged = attackStrength > 0.9f;

        // -------------------------------------------------
        // CUSTOM CRIT (cooldown-gated)
        // -------------------------------------------------
        if (fullyCharged &&
                stats.critChance() > 0 &&
                RANDOM.nextFloat() < stats.critChance()) {

            damage *= stats.critMultiplier();
            crit = true;
        }

        // -------------------------------------------------
        // 🎰 JESS’S JACKPOT (also cooldown-gated)
        // -------------------------------------------------
        if (fullyCharged &&
                CurioHelper.hasJessJackpot(player)) {

            int money = player
                    .getData(MoneyAttachment.MONEY)
                    .get();

            if (money < 500 && RANDOM.nextFloat() < 0.10f) {

                // 💥 DOUBLE DAMAGE
                damage *= 2.0f;

                // ✨ VISUAL + AUDIO FEEDBACK
                if (player.level() instanceof ServerLevel serverLevel) {

                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                            target.getX(),
                            target.getY() + target.getBbHeight() * 0.6,
                            target.getZ(),
                            12,
                            0.3, 0.3, 0.3,
                            0.0
                    );

                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.GLOW,
                            target.getX(),
                            target.getY() + target.getBbHeight() * 0.6,
                            target.getZ(),
                            6,
                            0.2, 0.2, 0.2,
                            0.05
                    );

                    serverLevel.playSound(
                            null,
                            target.blockPosition(),
                            net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            1.0f,
                            1.2f
                    );
                }
            }
        }

        // -------------------------------------------------
        // LIFESTEAL (scales with final damage)
        // -------------------------------------------------
        if (stats.lifestealPercent() > 0) {
            float heal = damage * (stats.lifestealPercent() / 100f);
            if (heal > 0) player.heal(heal);
        }

        return new CombatResult(damage, crit);
    }
}
