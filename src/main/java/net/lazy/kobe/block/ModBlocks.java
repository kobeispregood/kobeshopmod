package net.lazy.kobe.block;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.crate.CrateBlock;
import net.lazy.kobe.crate.CrateType;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
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
    }
