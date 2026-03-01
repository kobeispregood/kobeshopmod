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

public class FatalFortune extends Item implements ICombatStatProvider {

    public FatalFortune(Properties properties) {
        super(properties);
    }

    @Override
    public CombatStats getCombatStats(ItemStack stack, ServerPlayer player) {
        return new CombatStats(
                1.0f,
                0.10f,   // +5% crit chance
                1.00f,   // +5% crit damage
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
                Component.literal("+10% Crit Chance")
                        .withStyle(ChatFormatting.GOLD)
        );
    }
    @Override
    public Component getName(ItemStack stack) {

        long time = System.currentTimeMillis();

        float speed = 0.003f;
        float wave = (float) (Math.sin(time * speed) * 0.5f + 0.5f);

        int r = (255); // base gold (223,176,75)
        int g = (165);
        int b = (0);

        int rgb = (r << 16) | (g << 8) | b;

        return Component.literal("Fatal Fortune")
                .withStyle(style -> style.withColor(rgb));
    }
}
