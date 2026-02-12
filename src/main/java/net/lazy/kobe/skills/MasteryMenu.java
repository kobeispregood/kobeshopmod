package net.lazy.kobe.skills;

import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class MasteryMenu extends AbstractContainerMenu {

    private final MasteryType masteryType;

    /* ============================================================
     *  SERVER CONSTRUCTOR
     * ============================================================ */
    public MasteryMenu(int id, Inventory inv, MasteryType masteryType) {
        super(SkillMenus.MASTERY_MENU.get(), id);
        this.masteryType = masteryType;
    }

    /* ============================================================
     *  CLIENT CONSTRUCTOR (SYNCED)
     * ============================================================ */
    public MasteryMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(SkillMenus.MASTERY_MENU.get(), id);
        this.masteryType = buf.readEnum(MasteryType.class);
    }

    /* ============================================================
     *  ACCESS
     * ============================================================ */
    public MasteryType getMasteryType() {
        return masteryType;
    }

    /* ============================================================
     *  REQUIRED OVERRIDES
     * ============================================================ */

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
