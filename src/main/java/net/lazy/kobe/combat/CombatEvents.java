package net.lazy.kobe.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = "kobe")
public class CombatEvents {

    private static final RandomSource RANDOM = RandomSource.create();

    /* ============================================================
     * DAMAGE MODIFIERS (NEOFORGE-CORRECT)
     * ============================================================ */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {

        // Attacker must be player
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        // Target must be living
        if (!(event.getEntity() instanceof LivingEntity)) return;

        float damage = event.getAmount();
        if (damage <= 0) return;

        CombatData data = player.getData(CombatAttachment.COMBAT);
        int level = data.getLevel();

        // =================================================
        // BASE DAMAGE SCALING (+1% per level)
        // =================================================
        damage *= (1.0f + (level * 0.01f));

        // =================================================
        // CRITICAL HIT (LEVEL 5 → 45)
        // =================================================
        boolean crit = false;

        if (level >= 5) {
            float progress = Mth.clamp((level - 5) / 40f, 0f, 1f);
            float critChance = Mth.lerp(progress, 0.05f, 0.50f);
            float critMultiplier = Mth.lerp(progress, 1.25f, 2.0f);

            if (RANDOM.nextFloat() < critChance) {
                damage *= critMultiplier;
                crit = true;
                if (RANDOM.nextFloat() < critChance) {
                    damage *= critMultiplier;
                    crit = true;

                    // 🔑 mark target for popup system
                    event.getEntity().getPersistentData().putBoolean("kobe_crit", true);
                }
            }
        }

        // APPLY FINAL DAMAGE
        event.setAmount(damage);

        // =================================================
        // LIFESTEAL (LEVEL 20+)
        // =================================================
        if (level >= 20) {
            int lifeStealPercent = Mth.clamp(1 + ((level - 20) / 5), 1, 10);
            float healAmount = damage * (lifeStealPercent / 100f);
            if (healAmount > 0) {
                player.heal(healAmount);
                player.playSound(
                        net.minecraft.sounds.SoundEvents.GENERIC_DRINK,
                        0.3f,
                        1.8f
                );
            }
        }

        // =================================================
        // CRIT FEEDBACK
        // =================================================
        if (crit) {
            player.level().broadcastEntityEvent(player, (byte) 4);
            player.playSound(
                    net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_CRIT,
                    1.0f,
                    1.0f
            );
        }
    }

    /* ============================================================
     * COMBAT XP
     * ============================================================ */
    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {

        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Monster)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        CombatData data = player.getData(CombatAttachment.COMBAT);
        data.addXp(getCombatXp(event.getEntity()));

        CombatSyncPacket.send(player, data);
    }

    private static float getCombatXp(LivingEntity mob) {
        return Math.max(3f, mob.getMaxHealth() * 0.15f);
    }
}
