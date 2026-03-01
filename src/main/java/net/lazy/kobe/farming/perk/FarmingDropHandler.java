package net.lazy.kobe.farming.perk;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

@EventBusSubscriber(modid = "kobe")
public class FarmingDropHandler {

    @SubscribeEvent
    public static void onCropBreak(BlockEvent.BreakEvent event) {

        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockState state = event.getState();

        if (!(state.getBlock() instanceof CropBlock crop)) return;
        if (!crop.isMaxAge(state)) return;

        MasteryProgress progress = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.FARMING);

        int levelNum = progress.getLevel();

        if (levelNum < 25) return;

        float multiplier = FarmingPerks.cropDropMultiplier(progress);
        if (multiplier <= 1.0f) return;

        List<ItemStack> drops = Block.getDrops(
                state,
                level,
                event.getPos(),
                null,
                player,
                player.getMainHandItem()
        );

        for (ItemStack stack : drops) {

            if (stack.is(state.getBlock().asItem())) continue;

            int baseCount = stack.getCount();
            float bonusAmount = baseCount * (multiplier - 1.0f);

            int guaranteed = (int) bonusAmount;
            if (guaranteed > 0) {
                ItemStack extra = stack.copy();
                extra.setCount(guaranteed);
                Block.popResource(level, event.getPos(), extra);
            }

            float fractional = bonusAmount - guaranteed;
            if (level.random.nextFloat() < fractional) {
                ItemStack extra = stack.copy();
                extra.setCount(1);
                Block.popResource(level, event.getPos(), extra);
            }
        }
    }
}