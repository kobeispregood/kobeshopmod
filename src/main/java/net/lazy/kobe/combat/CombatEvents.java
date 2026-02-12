package net.lazy.kobe.combat;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXp;
import net.lazy.kobe.mastery.MasteryXpCentral;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.sounds.SoundEvents;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = "kobe")
public class CombatEvents {

    /* ============================================================
     * DAMAGE (MASTERED COMBAT)
     * ============================================================ */

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        float baseDamage = event.getAmount();
        if (baseDamage <= 0) return;

        CombatResult result =
                CombatDamageHandler.apply(player, target, baseDamage);

        event.setAmount(result.damage());

        if (result.crit()) {
            target.getPersistentData().putBoolean("kobe_crit", true);
            player.playSound(
                    SoundEvents.PLAYER_ATTACK_CRIT,
                    1.0f,
                    1.0f
            );
        }
    }

    /* ============================================================
     * COMBAT XP → MASTERY
     * ============================================================ */
    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {

        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Monster)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        MasteryXpCentral.addXp(
                player,
                MasteryType.COMBAT,
                Math.round(getCombatXp(event.getEntity()))
        );
    }

    private static float getCombatXp(LivingEntity mob) {
        return Math.max(3f, mob.getMaxHealth() * 0.15f);
    }
}
