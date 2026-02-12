package net.lazy.kobe.curios;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class BlakeyBagLogic {

    private BlakeyBagLogic() {}

    /* =========================================================
     * CALLED EVERY TICK WHILE BAG IS EQUIPPED
     * ========================================================= */
    public static void tick(SlotContext ctx, ItemStack bag) {
        // Intentionally empty for now
        // Future: ramen cooldown proxy, jackpots, etc
    }

    /* =========================================================
     * ATTRIBUTE PROXY
     * =========================================================
     *
     * IMPORTANT:
     * - Dunham Dice is CRIT-ONLY
     * - Crit logic lives in CombatStatsCalculator
     * - NO vanilla attributes should be applied here for Dice
     *
     * This method must return EMPTY to avoid double application.
     */
    public static Multimap<Holder<Attribute>, AttributeModifier> getAttributes(
            SlotContext ctx,
            ItemStack bag
    ) {
        return HashMultimap.create();
    }
}