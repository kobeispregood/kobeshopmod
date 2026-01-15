package net.lazy.kobe.skills;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;

public class CombatMenu extends AbstractContainerMenu {

    private final SimpleContainer container = new SimpleContainer(54);

    public CombatMenu(int id, Inventory inv) {
        super(SkillMenus.COMBAT_MENU.get(), id);

        int startX = 8, startY = 18, size = 18;

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 9; c++) {
                int i = r * 9 + c;
                addSlot(new Slot(container, i,
                        startX + c * size,
                        startY + r * size) {
                    @Override public boolean mayPickup(Player p) { return false; }
                    @Override public boolean mayPlace(ItemStack s) { return false; }
                });
            }
        }

        populate();
    }

    private void populate() {
        // combat items
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        // 🔇 Prevent vanilla container click sound
    }

    @Override
    public ItemStack quickMoveStack(Player p, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player p) {
        return true;
    }
}
