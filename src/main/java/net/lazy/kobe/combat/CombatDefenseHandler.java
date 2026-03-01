package net.lazy.kobe.combat;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = "kobe")
public class CombatDefenseHandler {

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CombatStats stats = CombatStatsCalculator.calculate(player);

        float defense = stats.defence();
        if (defense <= 0) return;

        float incoming = event.getNewDamage();

        float reduced =
                incoming * (100f / (100f + defense));

        event.setNewDamage(reduced);
    }
}