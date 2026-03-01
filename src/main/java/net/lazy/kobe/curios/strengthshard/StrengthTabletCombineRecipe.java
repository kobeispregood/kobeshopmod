package net.lazy.kobe.curios.strengthshard;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.item.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class StrengthTabletCombineRecipe extends CustomRecipe {

    public StrengthTabletCombineRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {

        if (input.width() != 3 || input.height() != 3) {
            return false;
        }

        for (int i = 0; i < input.size(); i++) {

            ItemStack stack = input.getItem(i);

            if (stack.isEmpty()) return false;

            // Center slot
            if (i == 4) {

                if (!stack.is(ModItems.STRENGTH_SHARD.get())) return false;

                if (StrengthShard.getLevel(stack) < StrengthShard.MAX_LEVEL) {
                    return false;
                }

            }
            // Outer 8 slots
            else {
                if (!stack.is(ModItems.AETHERIUM.get())) return false;
                if (stack.getCount() != 1) return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(ModItems.STRENGTH_TABLET.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItems.STRENGTH_TABLET.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRENGTH_TABLET_COMBINE_SERIALIZER.get();
    }
}