package net.lazy.kobe.item;

import com.mojang.serialization.MapCodec;
import net.lazy.kobe.KobeMod;
import net.lazy.kobe.curios.strengthshard.AscenditeTabletUpgradeRecipe;
import net.lazy.kobe.curios.strengthshard.StrengthShardCombineRecipe;
import net.lazy.kobe.curios.strengthshard.StrengthTabletCombineRecipe;
import net.lazy.kobe.curios.strengthshard.ZenithTabletUpgradeRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, KobeMod.MOD_ID);

    // =========================================================
    // REGISTER SERIALIZERS
    // =========================================================

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StrengthShardCombineRecipe>>
            STRENGTH_SHARD_COMBINE_SERIALIZER =
            SERIALIZERS.register("strength_shard_combine",
                    StrengthShardCombineSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StrengthTabletCombineRecipe>>
            STRENGTH_TABLET_COMBINE_SERIALIZER =
            SERIALIZERS.register("strength_tablet_combine",
                    StrengthTabletCombineSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ZenithTabletUpgradeRecipe>>
            ZENITH_TABLET_UPGRADE_SERIALIZER =
            SERIALIZERS.register("zenith_tablet_upgrade",
                    ZenithTabletUpgradeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AscenditeTabletUpgradeRecipe>>
            ASCENDITE_TABLET_UPGRADE_SERIALIZER =
            SERIALIZERS.register("ascendite_tablet_upgrade",
                    AscenditeTabletUpgradeSerializer::new);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }

    // =========================================================
    // SHARD SERIALIZER
    // =========================================================

    public static class StrengthShardCombineSerializer
            implements RecipeSerializer<StrengthShardCombineRecipe> {

        private static final CraftingBookCategory DEFAULT_CATEGORY =
                CraftingBookCategory.MISC;

        private static final MapCodec<StrengthShardCombineRecipe> CODEC =
                MapCodec.unit(new StrengthShardCombineRecipe(DEFAULT_CATEGORY));

        private static final StreamCodec<RegistryFriendlyByteBuf, StrengthShardCombineRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {},
                        (buf) -> new StrengthShardCombineRecipe(DEFAULT_CATEGORY)
                );

        @Override
        public MapCodec<StrengthShardCombineRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StrengthShardCombineRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    // =========================================================
    // TABLET SERIALIZER
    // =========================================================

    public static class StrengthTabletCombineSerializer
            implements RecipeSerializer<StrengthTabletCombineRecipe> {

        private static final CraftingBookCategory DEFAULT_CATEGORY =
                CraftingBookCategory.MISC;

        private static final MapCodec<StrengthTabletCombineRecipe> CODEC =
                MapCodec.unit(new StrengthTabletCombineRecipe(DEFAULT_CATEGORY));

        private static final StreamCodec<RegistryFriendlyByteBuf, StrengthTabletCombineRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {},
                        (buf) -> new StrengthTabletCombineRecipe(DEFAULT_CATEGORY)
                );

        @Override
        public MapCodec<StrengthTabletCombineRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StrengthTabletCombineRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    // =========================================================
    // ZENITH SERIALIZER
    // =========================================================

    public static class ZenithTabletUpgradeSerializer
            implements RecipeSerializer<ZenithTabletUpgradeRecipe> {

        private static final CraftingBookCategory DEFAULT_CATEGORY =
                CraftingBookCategory.MISC;

        private static final MapCodec<ZenithTabletUpgradeRecipe> CODEC =
                MapCodec.unit(new ZenithTabletUpgradeRecipe(DEFAULT_CATEGORY));

        private static final StreamCodec<RegistryFriendlyByteBuf, ZenithTabletUpgradeRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {},
                        (buf) -> new ZenithTabletUpgradeRecipe(DEFAULT_CATEGORY)
                );

        @Override
        public MapCodec<ZenithTabletUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ZenithTabletUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    // =========================================================
    // ASCENDITE SERIALIZER
    // =========================================================

    public static class AscenditeTabletUpgradeSerializer
            implements RecipeSerializer<AscenditeTabletUpgradeRecipe> {

        private static final CraftingBookCategory DEFAULT_CATEGORY =
                CraftingBookCategory.MISC;

        private static final MapCodec<AscenditeTabletUpgradeRecipe> CODEC =
                MapCodec.unit(new AscenditeTabletUpgradeRecipe(DEFAULT_CATEGORY));

        private static final StreamCodec<RegistryFriendlyByteBuf, AscenditeTabletUpgradeRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {},
                        (buf) -> new AscenditeTabletUpgradeRecipe(DEFAULT_CATEGORY)
                );

        @Override
        public MapCodec<AscenditeTabletUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AscenditeTabletUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}