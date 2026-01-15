package net.lazy.kobe.crate;

import net.lazy.kobe.block.ModBlockEntities;
import net.lazy.kobe.network.CrateOpenPacket;
import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class CrateBlock extends Block implements EntityBlock {

    private final CrateType type;

    public CrateType getCrateType() {
        return this.type;
    }

    public CrateBlock(Properties props, CrateType type) {
        super(props);
        this.type = type;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.CRATE_BE.get().create(pos, state);
    }

    // ---------------------------------------------------------
    // NORMAL RIGHT CLICK → PASS (item handles it)
    // ---------------------------------------------------------
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        return InteractionResult.PASS;
    }

    // ---------------------------------------------------------
    // RIGHT CLICK WITH ITEM (KEY)
    // ---------------------------------------------------------
    public ItemInteractionResult useItemOn(
            ItemStack held,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!held.is(type.key.get())) {

            if (level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("§cYou need a " + type.id + " crate key."),
                        true
                );
            }

            return ItemInteractionResult.SUCCESS;
        }

        // CLIENT: open reveal screen immediately
        if (level.isClientSide) {
            net.minecraft.client.Minecraft.getInstance().setScreen(
                    new net.lazy.kobe.client.CrateRevealScreen(
                            ItemStack.EMPTY, // placeholder, screen animates anyway
                            type
                    )
            );
            return ItemInteractionResult.SUCCESS;
        }

        // SERVER: give reward
        if (player instanceof ServerPlayer sp) {

            if (!sp.isCreative()) {
                held.shrink(1);
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CrateBlockEntity crate) {
                crate.open(sp, type);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (level.getBlockEntity(pos) instanceof CrateBlockEntity be) {
            be.setCrateType(getCrateType());

            // 🔥 force client refresh immediately
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    // ---------------------------------------------------------
    // CLIENT-SIDE TICKER (NeoForge 1.21)
    // ---------------------------------------------------------
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type
    ) {
        if (!level.isClientSide) return null;

        return (lvl, pos, st, be) -> {
            if (be instanceof CrateBlockEntity crate) {
                CrateBlockEntity.tick(lvl, pos, st, crate);
            }
        };
    }
}
