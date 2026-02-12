package net.lazy.kobe.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class JessJackpot extends Item {

    public JessJackpot(Properties properties) {
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
                Component.translatable("tooltip.kobe.jess_jackpot.condition")
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable("tooltip.kobe.jess_jackpot.effect")
                        .withStyle(ChatFormatting.AQUA)
        );

        tooltip.add(
                Component.translatable("tooltip.kobe.jess_jackpot.flavor")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );
    }
}
