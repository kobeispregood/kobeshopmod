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

public class StrengthShardCombineRecipe extends CustomRecipe {

    public StrengthShardCombineRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {

        int shardCount = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);

            if (!stack.isEmpty()) {
                if (!stack.is(ModItems.STRENGTH_SHARD.get())) {
                    return false;
                }
                shardCount++;
            }
        }

        return shardCount == 2;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack shard = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                shard = stack.copy();
                break;
            }
        }

        if (shard.isEmpty()) return ItemStack.EMPTY;

        int currentLevel = StrengthShard.getLevel(shard);
        int newLevel = Math.min(currentLevel + 1, StrengthShard.MAX_LEVEL);

        StrengthShard.setLevel(shard, newLevel);

        return shard;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItems.STRENGTH_SHARD.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRENGTH_SHARD_COMBINE_SERIALIZER.get();
    }
}