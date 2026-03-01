package net.lazy.kobe.farming.perk;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.net.MasterySyncPacket;
import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

@EventBusSubscriber(modid = "kobe")
public class FarmingHarvestHandler {

    private static final int REQUIRED_LEVEL = 25;

    @SubscribeEvent
    public static void onRightClickCrop(PlayerInteractEvent.RightClickBlock event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof CropBlock crop)) return;
        if (!crop.isMaxAge(state)) return;

        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.FARMING);

        if (progress.getLevel() < REQUIRED_LEVEL) return;

        List<ItemStack> drops = Block.getDrops(
                state,
                level,
                pos,
                null,
                player,
                player.getMainHandItem()
        );

        for (ItemStack stack : drops) {
            Block.popResource(level, pos, stack.copy());
        }

        player.swing(event.getHand(), true);

        level.playSound(
                null,
                pos,
                state.getSoundType().getBreakSound(),
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        progress.addXp(2);

        NetworkHandler.sendToPlayer(
                new MasterySyncPacket(
                        MasteryType.FARMING,
                        progress.getLevel(),
                        progress.getXpIntoLevel(),
                        new java.util.ArrayList<>(progress.getClaimedLevels())
                ),
                player
        );

        level.setBlock(pos, crop.getStateForAge(0), Block.UPDATE_ALL);

        event.setCanceled(true);
    }
}