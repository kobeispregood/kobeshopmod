package net.lazy.kobe.block;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.crate.CrateBlock;
import net.lazy.kobe.crate.CrateType;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, KobeMod.MOD_ID);

    public static final DeferredHolder<Block, Block> COMMON_CRATE =
            BLOCKS.register("common_crate",
                    () -> new CrateBlock(Block.Properties.of().strength(2.0f).noOcclusion(), CrateType.COMMON)
            );

    public static final DeferredHolder<Block, Block> RARE_CRATE =
            BLOCKS.register("rare_crate",
                    () -> new CrateBlock(Block.Properties.of().strength(2.0f).noOcclusion(), CrateType.RARE)
            );

    public static final DeferredHolder<Block, Block> LEGENDARY_CRATE =
            BLOCKS.register("legendary_crate",
                    () -> new CrateBlock(Block.Properties.of().strength(2.0f).noOcclusion(), CrateType.LEGENDARY)
            );

    public static final DeferredHolder<Block, Block> AETHERIUM_ORE =
            BLOCKS.register("aetherium_ore",
                    () -> new DropExperienceBlock(
                            UniformInt.of(3, 6),
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(4.0f)
                                    .requiresCorrectToolForDrops()
                                    .sound(SoundType.STONE)
                    ));

    public static final DeferredHolder<Block, Block> ZENITH_ORE =
            BLOCKS.register("zenith_ore",
                    () -> new DropExperienceBlock(
                            UniformInt.of(5, 9),
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(5.5f)
                                    .requiresCorrectToolForDrops()
                                    .sound(SoundType.STONE)
                    ));

    public static final DeferredHolder<Block, Block> ASCENDITE_ORE =
            BLOCKS.register("ascendite_ore",
                    () -> new DropExperienceBlock(
                            UniformInt.of(8, 14),
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(7.0f)
                                    .requiresCorrectToolForDrops()
                                    .sound(SoundType.STONE)
                    ));
    }
