package net.lazy.kobe.client;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = "kobe")
public class DamagePopupHandler {

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {

        if (!(event.getEntity() instanceof Monster mob)) return;

        Level level = mob.level();
        if (level.isClientSide) return;

        float rawDamage = event.getNewDamage();
        if (rawDamage <= 0) return;

        int damage = Math.round(rawDamage);
        if (damage <= 0) return;

        // =================================================
        // READ REAL CRIT RESULT (FROM CombatEvents)
        // =================================================
        boolean crit = mob.getPersistentData().getBoolean("kobe_crit");
        mob.getPersistentData().remove("kobe_crit");

        // =================================================
        // CALCULATE OFFSET TOWARD ATTACKER (SCREEN-FACING)
        // =================================================
        double dx = 0;
        double dz = 0;

        LivingEntity attacker = mob.getLastHurtByMob();
        if (attacker != null) {
            dx = attacker.getX() - mob.getX();
            dz = attacker.getZ() - mob.getZ();
        }

        double length = Math.sqrt(dx * dx + dz * dz);
        if (length != 0) {
            dx /= length;
            dz /= length;
        }

        double forwardOffset = 0.5;

        // =================================================
        // POSITIONING
        // =================================================
        double yOffset = mob.getY() + mob.getBbHeight() * 0.45;

        // =================================================
        // TEXT STYLE
        // =================================================
        int color = crit ? 0xFFD700 : 0xFFFFFF;
        String text = (crit ? "✧ " : "") + damage;

        AreaEffectCloud cloud = new AreaEffectCloud(
                level,
                mob.getX() + dx * forwardOffset,
                yOffset,
                mob.getZ() + dz * forwardOffset
        );

        cloud.setRadius(0.0F);
        cloud.setDuration(12);
        cloud.setWaitTime(0);
        cloud.setParticle(net.minecraft.core.particles.ParticleTypes.ASH);

        cloud.setCustomName(
                Component.literal(text)
                        .withStyle(style -> style.withColor(color).withBold(crit))
        );
        cloud.setCustomNameVisible(true);

        // =================================================
        // MOTION (READABLE, NOT FLYING OFF SCREEN)
        // =================================================
        cloud.setDeltaMovement(
                (RandomSource.create().nextDouble() - 0.5) * 0.015,
                crit ? 0.10 : 0.08,
                (RandomSource.create().nextDouble() - 0.5) * 0.015
        );

        level.addFreshEntity(cloud);
    }
}
