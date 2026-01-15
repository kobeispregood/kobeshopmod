package net.lazy.kobe.block;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.crate.CrateBlockEntity;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KobeMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateBlockEntity>> CRATE_BE =
            BLOCK_ENTITIES.register("crate",
                    () -> BlockEntityType.Builder
                            .of(
                                    CrateBlockEntity::new,
                                    ModBlocks.COMMON_CRATE.get(),
                                    ModBlocks.RARE_CRATE.get(),
                                    ModBlocks.LEGENDARY_CRATE.get()
                            )
                            .build(null)
            );
}
