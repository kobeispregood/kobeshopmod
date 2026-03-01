package net.lazy.kobe.curios.strengthshard;

import net.lazy.kobe.combat.CombatStats;
import net.lazy.kobe.combat.ICombatStatProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class StrengthTablet extends Item implements ICombatStatProvider {

    private final float damageBonus;   // 0.15 = +15%
    private final float defenseBonus;
    private final ChatFormatting nameColor;

    public StrengthTablet(Properties properties,
                          float damageBonus,
                          float defenseBonus,
                          ChatFormatting nameColor) {
        super(properties.stacksTo(1));
        this.damageBonus = damageBonus;
        this.defenseBonus = defenseBonus;
        this.nameColor = nameColor;
    }

    @Override
    public CombatStats getCombatStats(ItemStack stack, ServerPlayer player) {

        return new CombatStats(
                1.0f + damageBonus,
                0.0f,
                1.0f,
                0.0f,
                defenseBonus
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        tooltip.add(Component.literal(
                        "+" + (int)(damageBonus * 100) + "% Damage")
                .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(
                        "+" + (int)defenseBonus + " Defense")
                .withStyle(ChatFormatting.BLUE));
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(nameColor);
    }
    @Override
    public boolean allowMultiple() {
        return false;
    }
}