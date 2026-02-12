package net.lazy.kobe.curios;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DinosDollar extends Item {

    public DinosDollar(Properties properties) {
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
                Component.literal("5% off from NPC shops")
                        .withStyle(ChatFormatting.GREEN)
        );
    }
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Dino's Dollar")
                .withStyle(ChatFormatting.GREEN);
    }
}