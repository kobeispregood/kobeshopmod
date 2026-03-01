package net.lazy.kobe.combat;

import net.lazy.kobe.curios.CurioHelper;
import net.lazy.kobe.curios.strengthshard.StrengthShard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class CombatStatsCalculator {

    private CombatStatsCalculator() {}

    public static CombatStats calculate(ServerPlayer player) {

        int combatLevel = CombatLevelHelper.getCombatLevel(player);

        CombatStats stats = CombatStats.fromLevel(combatLevel);

        stats = stats.add(getCurioBonuses(player));

        return stats;
    }

    public static CombatStats applyCurioBonuses(
            ServerPlayer player,
            CombatStats baseStats
    ) {
        return baseStats.add(getCurioBonuses(player));
    }

    private static CombatStats getCurioBonuses(ServerPlayer player) {

        CombatStats total = CombatStats.empty();

        java.util.Set<Class<?>> appliedTypes =
                new java.util.HashSet<>();

        for (ItemStack stack : CurioHelper.getAllEquippedCurios(player)) {

            if (stack.isEmpty()) continue;

            var item = stack.getItem();

            if (!(item instanceof ICombatStatProvider provider)) continue;

            Class<?> itemClass = item.getClass();

            // If multiple not allowed and this TYPE already applied → skip
            if (!provider.allowMultiple() &&
                    appliedTypes.contains(itemClass)) {
                continue;
            }

            total = total.add(provider.getCombatStats(stack, player));

            appliedTypes.add(itemClass);
        }

        return total;
    }
}