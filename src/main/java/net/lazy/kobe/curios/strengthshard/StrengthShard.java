package net.lazy.kobe.curios.strengthshard;

import net.lazy.kobe.combat.CombatStats;
import net.lazy.kobe.combat.ICombatStatProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;

import java.util.List;

public class StrengthShard extends Item implements ICombatStatProvider {

    @Override
    public CombatStats getCombatStats(ItemStack stack, ServerPlayer player) {

        float multiplier = getMultiplier(stack);

        return new CombatStats(
                multiplier,
                0.0f,
                1.0f,
                0.0f,
                0.0f

        );
    }

    public static final String KEY_LEVEL = "kobe_strength_shard_level";
    public static final int MAX_LEVEL = 10;

    public StrengthShard(Properties properties) {
        super(properties.stacksTo(1));
    }

    /* ============================================================
     * LEVEL HANDLING (NBT via DataComponents)
     * ============================================================ */

    public static int getLevel(ItemStack stack) {

        CustomData data = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        );

        int level = data.copyTag().getInt(KEY_LEVEL);

        // If no level stored → default to 1
        if (level <= 0) {
            return 1;
        }

        // If somehow above max → auto-fix it
        if (level > MAX_LEVEL) {
            setLevel(stack, MAX_LEVEL);
            return MAX_LEVEL;
        }

        return level;
    }

    public static void setLevel(ItemStack stack, int level) {

        int clamped = Math.min(Math.max(level, 1), MAX_LEVEL);

        CustomData.update(
                DataComponents.CUSTOM_DATA,
                stack,
                tag -> tag.putInt(KEY_LEVEL, clamped)
        );
    }

    /* ============================================================
     * DAMAGE MULTIPLIER
     * ============================================================ */

    public static float getMultiplier(ItemStack stack) {
        int level = getLevel(stack);

        // Each level = +1% damage
        return 1.0f + (level * 0.01f);
    }

    /* ============================================================
     * TOOLTIP
     * ============================================================ */

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        int level = getLevel(stack);

        tooltip.add(
                Component.literal("Strength Shard")
                        .withStyle(ChatFormatting.GOLD)
        );

        tooltip.add(
                Component.literal("Level: " + level + "/" + MAX_LEVEL)
                        .withStyle(ChatFormatting.YELLOW)
        );

        tooltip.add(
                Component.literal("+" + level + "% Damage")
                        .withStyle(ChatFormatting.GREEN)
        );

        if (level >= MAX_LEVEL) {
            tooltip.add(
                    Component.literal("Max Level Reached")
                            .withStyle(ChatFormatting.AQUA)
            );
        }
    }
}