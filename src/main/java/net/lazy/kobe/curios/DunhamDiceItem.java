package net.lazy.kobe.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DunhamDiceItem extends Item {

    public DunhamDiceItem(Properties properties) {
        super(properties);
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
