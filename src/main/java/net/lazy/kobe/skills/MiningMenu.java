package net.lazy.kobe.skills;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class MiningMenu extends AbstractContainerMenu {

    private final SimpleContainer container = new SimpleContainer(54);

    public MiningMenu(int id, Inventory inv) {
        super(SkillMenus.MINING_MENU.get(), id);

        int startX = 8;
        int startY = 18;
        int slotSize = 18;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                this.addSlot(new Slot(
                        container,
                        index,
                        startX + col * slotSize,
                        startY + row * slotSize
                ) {
                    @Override public boolean mayPickup(Player p) { return false; }
                    @Override public boolean mayPlace(ItemStack s) { return false; }
                });
            }
        }
    }

    private ItemStack info(String name) {
        ItemStack stack = new ItemStack(Items.IRON_PICKAXE);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }

    private ItemStack perk(String name, String desc) {
        ItemStack stack = new ItemStack(Items.EMERALD);

        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal(desc),
                Component.literal(""),
                Component.literal("Unlocked by leveling Mining")
        )));

        return stack;
    }

    private ItemStack back() {
        ItemStack stack = new ItemStack(Items.ARROW);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Back"));
        return stack;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (clickType != ClickType.PICKUP) return;

        if (slotId == 49) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new SkillsMenu(id, inv),
                    Component.literal("Skills")
            ));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
