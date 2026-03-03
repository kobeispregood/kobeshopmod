package net.lazy.kobe.client.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = "kobe")
public class EntityHealthNameUpdater {

    // Stores original names (Component.empty() = no original name)
    private static final Map<UUID, Component> ORIGINAL_NAMES = new ConcurrentHashMap<>();

    /* ============================================================
       ENTITY SPAWN / LOAD
       ============================================================ */
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Monster mob)) return;

        cacheOriginalName(mob);
        updateName(mob);
    }

    /* ============================================================
       ENTITY DAMAGE
       ============================================================ */
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Monster mob)) return;
        updateName(mob);
    }

    /* ============================================================
       ENTITY HEAL
       ============================================================ */
    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Monster mob)) return;
        updateName(mob);
    }

    /* ============================================================
       ENTITY DEATH → RESTORE ORIGINAL NAME
       ============================================================ */
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Monster mob)) return;

        Component original = ORIGINAL_NAMES.remove(mob.getUUID());

        if (original == null || original.equals(Component.empty())) {
            mob.setCustomName(null);
            mob.setCustomNameVisible(false);
        } else {
            mob.setCustomName(original.copy()); // preserve styling
            mob.setCustomNameVisible(true);
        }
    }

    /* ============================================================
       CORE LOGIC
       ============================================================ */
    private static void updateName(Mob mob) {

        if (!mob.isAlive()) return;

        float max = mob.getMaxHealth();
        if (max <= 0) return;

        int hp = Math.round(mob.getHealth());
        if (hp < 0) hp = 0;

        int color =
                hp > max * 0.66f ? 0x00FF00 :
                        hp > max * 0.33f ? 0xFFFF00 :
                                0xFF5555;

        Component original = ORIGINAL_NAMES.get(mob.getUUID());

        Component baseName;

        if (original != null && !original.equals(Component.empty())) {
            // ✅ Preserve full formatting (tier colors etc.)
            baseName = original.copy();
        } else {
            // Vanilla fallback
            baseName = Component.translatable(mob.getType().getDescriptionId());
        }

        Component display = Component.empty()
                .append(baseName)
                .append(Component.literal("  |  ")
                        .withStyle(ChatFormatting.DARK_GRAY))
                .append(
                        Component.literal("❤ " + formatHp(hp))
                                .withStyle(style -> style.withColor(color))
                );

        mob.setCustomName(display);
        mob.setCustomNameVisible(true);
    }

    /* ============================================================
       NAME CACHE
       ============================================================ */
    private static void cacheOriginalName(Mob mob) {

        UUID id = mob.getUUID();

        ORIGINAL_NAMES.computeIfAbsent(
                id,
                k -> mob.getCustomName() != null
                        ? mob.getCustomName().copy()
                        : Component.empty()
        );
    }

    /* ============================================================
       HP FORMATTER
       ============================================================ */
    private static String formatHp(int hp) {

        if (hp >= 1_000_000)
            return String.format("%.1fM", hp / 1_000_000f);

        if (hp >= 1_000)
            return String.format("%.1fk", hp / 1_000f);

        return String.valueOf(hp);
    }
}