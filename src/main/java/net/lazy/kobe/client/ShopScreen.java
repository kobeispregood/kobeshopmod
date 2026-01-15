package net.lazy.kobe.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ShopScreen extends Screen {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("kobe", "textures/gui/categories.png");

    public ShopScreen() {
        super(Component.literal("Shop"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int x = (this.width - 176) / 2;
        int y = (this.height - 166) / 2;

        graphics.blit(BG_TEXTURE, x, y, 0, 0, 176, 166);
    }
}
