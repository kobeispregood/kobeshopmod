package net.lazy.kobe.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICombatStatProvider {

    CombatStats getCombatStats(ItemStack stack, ServerPlayer player);

    /**
     * Whether multiple copies of this item type
     * should stack their effects.
     *
     * Default = false (no stacking).
     */
    default boolean allowMultiple() {
        return false;
    }
}