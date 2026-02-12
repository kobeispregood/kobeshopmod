package net.lazy.kobe.curios;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.menu.BlakeyBagData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlakeyBag extends Item {

    public BlakeyBag(Properties properties) {
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
                Component.literal("Holds up to 6 Oddities")
                        .withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    public Component getName(ItemStack stack) {

        ChatFormatting color = ChatFormatting.GRAY;

        if (!stack.isEmpty() && stack.has(DataComponents.CUSTOM_DATA)) {

            var items = BlakeyBagData.getItems(
                    stack,
                    net.minecraft.client.Minecraft.getInstance()
                            .level
                            .registryAccess()
            );

            if (!items.isEmpty()) {
                color = ChatFormatting.GOLD;
            }

            // Example: Dice inside → purple
            for (ItemStack s : items) {
                if (s.is(ModItems.DUNHAMDICE.get())) {
                    color = ChatFormatting.DARK_PURPLE;
                    break;
                }
            }
        }

        return Component.literal("Blakey Bag").withStyle(color);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);

            serverPlayer.openMenu(
                    new net.minecraft.world.SimpleMenuProvider(
                            (id, inv, p) -> new net.lazy.kobe.menu.BlakeyBagMenu(id, inv, stack),
                            net.minecraft.network.chat.Component.literal("Blakey Bag")
                    )
            );
        }

        return InteractionResultHolder.sidedSuccess(
                player.getItemInHand(hand),
                level.isClientSide
        );
    }
}