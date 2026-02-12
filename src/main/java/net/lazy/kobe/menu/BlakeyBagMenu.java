package net.lazy.kobe.menu;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class BlakeyBagMenu extends AbstractContainerMenu {

    public static final int SIZE = 6;

    private final SimpleContainer container;
    private final ItemStack bagStack; // server only (client gets EMPTY)
    private final HolderLookup.Provider provider;

    /* =========================================================
     * CLIENT CONSTRUCTOR (NO EXTRA DATA)
     * This is what MenuType uses on the client.
     * ========================================================= */
    public BlakeyBagMenu(int id, Inventory inv) {
        super(ModMenus.BLAKE_BAG.get(), id);

        this.container = new SimpleContainer(SIZE);
        this.bagStack = ItemStack.EMPTY; // client doesn't need the actual stack
        this.provider = inv.player.level().registryAccess();

        // slots (2x3)
        addBagSlots();

        // player inv
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    /* =========================================================
     * SERVER CONSTRUCTOR (USED BY SimpleMenuProvider)
     * ========================================================= */
    public BlakeyBagMenu(int id, Inventory inv, ItemStack bagStack) {
        super(ModMenus.BLAKE_BAG.get(), id);

        this.container = new SimpleContainer(SIZE);
        this.bagStack = bagStack;
        this.provider = inv.player.level().registryAccess();

        loadFromItem();

        addBagSlots();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    private void addBagSlots() {
        int startX = 62;
        int startY = 17;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                this.addSlot(new Slot(container, index,
                        startX + col * 18,
                        startY + row * 18
                ) {
                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }
                });
            }
        }
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        inv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inv,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    /* =========================================================
     * SHIFT CLICK
     * ========================================================= */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < SIZE) {
            if (!this.moveItemStackTo(stack, SIZE, this.slots.size(), true))
                return empty;
        } else {
            if (!this.moveItemStackTo(stack, 0, SIZE, false))
                return empty;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    /* =========================================================
     * SAVE / LOAD (CUSTOM_DATA component, like your SkillsScreen)
     * ========================================================= */
    private CompoundTag getRootTag() {
        CustomData data = bagStack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    private void setRootTag(CompoundTag tag) {
        bagStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private void loadFromItem() {
        if (bagStack.isEmpty()) return;

        CompoundTag root = getRootTag();
        if (!root.contains("Items", 9)) return; // 9 = TAG_LIST

        ListTag list = root.getList("Items", 10); // 10 = TAG_COMPOUND
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < SIZE) {
                // 1.21.1: parseOptional needs a HolderLookup.Provider
                ItemStack stack = ItemStack.parseOptional(provider, entry);
                container.setItem(slot, stack);
            }
        }
    }

    private void saveToItem() {
        // HARD GUARDS
        if (bagStack.isEmpty()) return;
        if (provider == null) return;

        CompoundTag root = getRootTag();
        ListTag list = new ListTag();

        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = container.getItem(i);

            // ABSOLUTE REQUIREMENT IN 1.21.1
            if (stack.isEmpty()) continue;

            Tag saved = stack.save(provider);
            if (saved instanceof CompoundTag entry) {
                entry.putByte("Slot", (byte) i);
                list.add(entry);
            }
        }
        root.put("Items", list);
        setRootTag(root);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // SERVER ONLY
        if (player.level().isClientSide) return;

        // Player must still be holding THIS bag
        ItemStack held = player.getMainHandItem();
        if (held != bagStack) return;

        saveToItem();
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // don’t tie validity to the stack on client
    }
}