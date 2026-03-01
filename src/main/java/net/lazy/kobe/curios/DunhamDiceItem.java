package net.lazy.kobe.curios;

import net.lazy.kobe.combat.CombatStats;
import net.lazy.kobe.combat.ICombatStatProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DunhamDiceItem extends Item implements ICombatStatProvider {

    public DunhamDiceItem(Properties properties) {
        super(properties);
    }

    @Override
    public CombatStats getCombatStats(ItemStack stack, ServerPlayer player) {
        return new CombatStats(
                1.0f,
                0.05f,   // +5% crit chance
                1.05f,   // +5% crit damage
                0.0f,
                0.0f
        );
}

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.literal("Increases:")
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.literal("+5% Crit Chance")
                        .withStyle(ChatFormatting.GOLD)
        );

        tooltip.add(
                Component.literal("+5% Crit Damage")
                        .withStyle(ChatFormatting.RED)
        );
    }
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Dunham Dice")
                .withStyle(ChatFormatting.DARK_PURPLE);
    }
}
