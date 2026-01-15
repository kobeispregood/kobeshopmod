package net.lazy.kobe.skills;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FarmingMenu extends AbstractContainerMenu {

    public FarmingMenu(int id, Inventory inv) {
        super(SkillMenus.FARMING_MENU.value(), id);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // 🔑 THIS IS THE CRITICAL PART
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
