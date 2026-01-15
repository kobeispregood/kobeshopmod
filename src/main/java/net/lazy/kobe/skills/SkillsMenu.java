package net.lazy.kobe.skills;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SkillsMenu extends AbstractContainerMenu {

    public static final int SIZE = 54;
    private final SimpleContainer container = new SimpleContainer(SIZE);

    public SkillsMenu(int id, Inventory inv) {
        super(SkillMenus.SKILLS_MENU.get(), id);

        // Fake slots backed by our container
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

        populate();
    }

    private void populate() {
        set(11, skill("Mining", Items.IRON_PICKAXE, 12));
        set(12, skill("Farming", Items.GOLDEN_HOE, 9));
        set(13, skill("Combat", Items.IRON_SWORD, 14));
        set(14, skill("Enchanting", Items.ENCHANTED_BOOK, 7));
        set(15, skill("Alchemy", Items.BREWING_STAND, 5));

        set(20, skill("Fishing", Items.FISHING_ROD, 11));
        set(21, skill("Foraging", Items.JUNGLE_SAPLING, 10));
        set(22, skill("Dungeons", Items.SKELETON_SKULL, 3));
        set(23, skill("Slayer", Items.NETHER_STAR, 6));
        set(24, skill("Runecrafting", Items.MAGMA_CREAM, 8));

        set(49, close());
    }

    private void set(int slot, ItemStack stack) {
        container.setItem(slot, stack);
    }

    private ItemStack skill(String name, Item item, int level) {
        ItemStack stack = new ItemStack(item);

        stack.set(DataComponents.CUSTOM_NAME,
                Component.literal(name));

        stack.set(DataComponents.LORE,
                new ItemLore(List.of(
                        Component.literal("Level: " + level),
                        Component.literal(""),
                        Component.literal("Click to view!")
                )));

        return stack;
    }

    private ItemStack close() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        stack.set(DataComponents.CUSTOM_NAME,
                Component.literal("Close"));
        return stack;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (clickType != ClickType.PICKUP) return;
        if (slotId < 0 || slotId >= SIZE) return;

        switch (slotId) {

            case 11 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new MiningMenu(id, inv),
                    Component.literal("Mining")
            ));

            case 12 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new FarmingMenu(id, inv),
                    Component.literal("Farming")
            ));

            case 13 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new CombatMenu(id, inv),
                    Component.literal("Combat")
            ));

            case 14 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new EnchantingMenu(id, inv),
                    Component.literal("Enchanting")
            ));

            case 15 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new AlchemyMenu(id, inv),
                    Component.literal("Alchemy")
            ));

            case 20 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new FishingMenu(id, inv),
                    Component.literal("Fishing")
            ));

            case 21 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new ForagingMenu(id, inv),
                    Component.literal("Foraging")
            ));

            case 23 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new HuntsMenu(id, inv),
                    Component.literal("Hunts")
            ));

            case 24 -> player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new RunecraftingMenu(id, inv),
                    Component.literal("Runecrafting")
            ));

            case 49 -> player.closeContainer();
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
