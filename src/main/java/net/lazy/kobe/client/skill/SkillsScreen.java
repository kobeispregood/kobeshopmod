package net.lazy.kobe.client.skill;

import net.lazy.kobe.mastery.MasteryData;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.skills.SkillsMenu;
import net.lazy.kobe.mastery.MasteryAttachment;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillsScreen extends AbstractContainerScreen<SkillsMenu> {

    private static final ResourceLocation CHEST =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/gui/container/generic_54.png"
            );

    private boolean initialized = false;

    public SkillsScreen(SkillsMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    /* ============================================================
     *  INIT / FILL
     * ============================================================ */

    @Override
    protected void init() {
        super.init();
        fillSkills();
    }

    private void fillSkills() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        // NEW mastery system
        MasteryData mastery = player.getData(MasteryAttachment.MASTERY);


        // --------------------------------------------------
        // LEGACY SKILLS (not converted yet)
        // --------------------------------------------------

        set(11, skill(
                "Mining",
                Items.DIAMOND_PICKAXE,
                mastery.getOrCreate(MasteryType.MINING).getLevel(),
                SkillType.MINING
        ));

        set(12, skill(
                "Farming",
                Items.GOLDEN_HOE,
                mastery.getOrCreate(MasteryType.FARMING).getLevel(),
                SkillType.FARMING
        ));

        set(13, skill(
                "Combat",
                Items.NETHERITE_SWORD,
                mastery.getOrCreate(MasteryType.COMBAT).getLevel(),
                SkillType.COMBAT
        ));

        // --------------------------------------------------
        // NEW MASTERY SKILLS
        // --------------------------------------------------

        set(14, skill(
                "Enchanting",
                Items.ENCHANTED_BOOK,
                mastery.getOrCreate(MasteryType.ENCHANTING).getLevel(),
                SkillType.ENCHANTING
        ));

        set(15, skill(
                "Alchemy",
                Items.BREWING_STAND,
                mastery.getOrCreate(MasteryType.ALCHEMY).getLevel(),
                SkillType.ALCHEMY
        ));

        set(20, skill(
                "Fishing",
                Items.FISHING_ROD,
                mastery.getOrCreate(MasteryType.FISHING).getLevel(),
                SkillType.FISHING
        ));

        set(21, skill(
                "Foraging",
                Items.OAK_LOG,
                mastery.getOrCreate(MasteryType.FORAGING).getLevel(),
                SkillType.FORAGING
        ));

        set(22, locked("Locked"));
        set(23, locked("Hunts"));
        set(24, locked("Runecrafting"));

        set(49, close());
    }

    /* ============================================================
     *  STACK BUILDERS
     * ============================================================ */

    private ItemStack skill(String name, Item item, int level, SkillType type) {
        ItemStack stack = new ItemStack(item);

        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));

        stack.set(DataComponents.LORE,
                new ItemLore(List.of(
                        Component.literal("Level: " + level),
                        Component.literal(""),
                        Component.literal("Click to view!")
                )));

        // tag with skill type
        CompoundTag tag = new CompoundTag();
        tag.putString("SkillType", type.name());

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return stack;
    }

    private ItemStack locked(String name) {
        ItemStack stack = new ItemStack(Items.GRAY_DYE);

        stack.set(DataComponents.CUSTOM_NAME,
                Component.literal(name).withStyle(ChatFormatting.DARK_GRAY));

        stack.set(DataComponents.LORE,
                new ItemLore(List.of(
                        Component.literal("Locked")
                                .withStyle(ChatFormatting.RED)
                )));

        return stack;
    }

    private ItemStack close() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Close"));
        return stack;
    }

    private void set(int slot, ItemStack stack) {
        this.menu.getSlot(slot).set(stack);
    }

    /* ============================================================
     *  RENDER
     * ============================================================ */

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(
                this.font,
                this.title,
                8,
                6,
                0x404040,
                false
        );
    }

    @Override
    protected void renderBg(GuiGraphics g, float p, int x, int y) {
        g.blit(CHEST, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        // refresh once after attachments sync
        if (!initialized && Minecraft.getInstance().player != null) {
            fillSkills();
            initialized = true;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        Slot hovered = this.getSlotUnderMouse();
        if (hovered != null && hovered.index < 54) {
            ItemStack stack = hovered.getItem();
            if (!stack.isEmpty()) {
                List<Component> tooltip = buildCleanTooltip(stack);
                if (!tooltip.isEmpty()) {
                    guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                    return;
                }
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /* ============================================================
     *  TOOLTIP
     * ============================================================ */

    private List<Component> buildCleanTooltip(ItemStack stack) {
        List<Component> out = new ArrayList<>();

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return out;

        CompoundTag tag = data.copyTag();
        if (!tag.contains("SkillType")) return out;

        SkillType type = SkillType.valueOf(tag.getString("SkillType"));

        int lvl = type.getLevel();
        int percent = type.getProgressPercent();

        out.add(
                Component.literal(type.displayName)
                        .withStyle(type.getTierColor())
        );

        out.add(
                Component.literal("Level: " + lvl + " (" + percent + "%)")
                        .withStyle(ChatFormatting.GREEN)
        );

        out.add(
                Component.literal("Click to view!")
                        .withStyle(ChatFormatting.YELLOW)
        );

        return out;
    }
}
