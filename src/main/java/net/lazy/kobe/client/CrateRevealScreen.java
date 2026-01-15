package net.lazy.kobe.client;

import net.lazy.kobe.crate.CrateLootData;
import net.lazy.kobe.crate.CrateLootEntry;
import net.lazy.kobe.crate.CrateType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CrateRevealScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(
                    "kobe",
                    "textures/gui/crate_reveal.png"
            );

    private static final int ROLL_TIME = 80;
    private static final int REVEAL_PAUSE = 20;

    private final ItemStack finalStack;
    private final CrateType crateType;

    private ItemStack displayStack = ItemStack.EMPTY;

    private int rollTicks = 0;
    private int nextRollTick = 0;
    private int revealPauseTicks = 0;

    private boolean revealed = false;
    private boolean skipped = false;

    private float revealScale = 1.0f;

    private int leftPos;
    private int topPos;

    // Restore blur only (1.21 supports this)
    private int prevBlur;

    public CrateRevealScreen(ItemStack finalStack, CrateType crateType) {
        super(Component.literal(crateType.id.toUpperCase() + " CRATE"));
        this.finalStack = finalStack;
        this.crateType = crateType;
    }

    // =========================================================
    // INIT
    // =========================================================
    @Override
    protected void init() {
        this.leftPos = (this.width - 176) / 2;
        this.topPos = (this.height - 166) / 2;

        var opts = Minecraft.getInstance().options;

        prevBlur = opts.menuBackgroundBlurriness().get();
        opts.menuBackgroundBlurriness().set(0);
    }

    @Override
    public void removed() {
        Minecraft.getInstance()
                .options
                .menuBackgroundBlurriness()
                .set(prevBlur);
    }

    // =========================================================
    // TICK
    // =========================================================
    @Override
    public void tick() {
        if (skipped && !revealed) {
            revealFinal();
            return;
        }

        if (!revealed) {
            rollTicks++;

            if (rollTicks < ROLL_TIME) {
                float progress = (float) rollTicks / ROLL_TIME;
                int delay = 1 + (int) (progress * progress * 10);

                if (rollTicks >= nextRollTick) {
                    CrateLootEntry entry = CrateLootData.rollWeighted(crateType);
                    Item item = BuiltInRegistries.ITEM.get(
                            ResourceLocation.tryParse(entry.itemId())
                    );

                    if (item != null) {
                        displayStack = new ItemStack(item, entry.amount());
                    }

                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.playNotifySound(
                                SoundEvents.UI_BUTTON_CLICK.value(),
                                SoundSource.MASTER,
                                0.4f,
                                1.8f
                        );
                    }

                    nextRollTick = rollTicks + delay;
                }
            } else {
                revealFinal();
            }
        } else {
            revealPauseTicks++;
            if (revealScale > 1.0f) revealScale -= 0.05f;
        }
    }

    private void revealFinal() {
        revealed = true;
        displayStack = finalStack;
        revealScale = 1.35f;

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.playNotifySound(
                    SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundSource.MASTER,
                    0.8f,
                    1.0f
            );
        }
    }

    // =========================================================
    // RENDER
    // =========================================================
    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {

        // NO DARK OVERLAY, NO GRADIENT

        gfx.blit(
                BACKGROUND,
                leftPos,
                topPos,
                0, 0,
                176, 166,
                176, 166
        );

        gfx.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                topPos + 8,
                0xFFD700
        );

        if (!displayStack.isEmpty()) {
            int centerX = this.width / 2;
            int centerY = this.height / 2 - 14;

            if (revealed) {
                gfx.fill(
                        centerX - 16,
                        centerY - 16,
                        centerX + 16,
                        centerY + 16,
                        0x60FFD700
                );
            }

            gfx.pose().pushPose();
            gfx.pose().translate(centerX, centerY, 0);
            gfx.pose().scale(revealScale, revealScale, 1f);

            gfx.renderItem(displayStack, -8, -8);
            gfx.renderItemDecorations(this.font, displayStack, -8, -8);

            gfx.pose().popPose();

            if (revealed && revealPauseTicks > 6) {
                gfx.drawCenteredString(
                        this.font,
                        displayStack.getHoverName(),
                        this.width / 2,
                        topPos + 150,
                        0xFFD700
                );
            }
        }

        super.render(gfx, mouseX, mouseY, partialTicks);
    }

    // =========================================================
    // INPUT
    // =========================================================
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!revealed) {
            skipped = true;
            return true;
        }
        if (revealPauseTicks >= REVEAL_PAUSE) {
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!revealed) {
            skipped = true;
            return true;
        }
        if (revealPauseTicks >= REVEAL_PAUSE) {
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
