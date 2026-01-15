package net.lazy.kobe.oregen.gui;

import net.lazy.kobe.oregen.OreGenAttachment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class OreGenMenu extends AbstractContainerMenu {

    private final Player player;
    private int syncedLevel;

    public OreGenMenu(int id, Inventory inv) {
        super(OreGenMenus.ORE_GEN_MENU.value(), id);

        this.player = inv.player;

        // Sync ore gen level live from attachment
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return player.getData(OreGenAttachment.LEVEL);
            }

            @Override
            public void set(int value) {
                syncedLevel = value;
            }
        });
    }

    /** Client-safe accessor */
    public int getLevel() {
        return syncedLevel;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
