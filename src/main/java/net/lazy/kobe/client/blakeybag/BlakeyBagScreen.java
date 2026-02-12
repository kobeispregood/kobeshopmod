package net.lazy.kobe.client.blakeybag;

import net.lazy.kobe.menu.BlakeyBagMenu;
import net.lazy.kobe.KobeMod;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlakeyBagScreen extends AbstractContainerScreen<BlakeyBagMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    KobeMod.MOD_ID,
                    "textures/gui/blakey_bag.png"
            );

    public BlakeyBagScreen(
            BlakeyBagMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);

        // 176x166 is standard chest size
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    /* =========================================================
     *  RENDER
     * ========================================================= */

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(
                TEXTURE,
                x,
                y,
                0,
                0,
                this.imageWidth,
                this.imageHeight
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    /* =========================================================
     *  LABELS
     * ========================================================= */

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        // Title
        graphics.drawString(
                this.font,
                this.title,
                8,
                6,
                0x404040,
                false
        );

        // Player inventory label
        graphics.drawString(
                this.font,
                this.playerInventoryTitle,
                8,
                this.imageHeight - 94,
                0x404040,
                false
        );
    }
}