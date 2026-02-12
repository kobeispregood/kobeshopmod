package net.lazy.kobe.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class BlakeyBagMenuProvider implements MenuProvider {
    private final ItemStack bag;

    public BlakeyBagMenuProvider(ItemStack bag) {
        this.bag = bag;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Blake Bag");
    }

    @Override
    public AbstractContainerMenu createMenu(
            int id,
            Inventory inv,
            Player player
    ) {
        return new BlakeyBagMenu(id, inv, bag);
    }
}