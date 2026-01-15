package net.lazy.kobe.farming.perk;

import net.lazy.kobe.farming.FarmingAttachment;
import net.lazy.kobe.farming.FarmingData;

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

        // =========================
        // SERVER ONLY
        // =========================
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockState state = event.getState();
        Block block = state.getBlock();

        // =========================
        // CROPS ONLY
        // =========================
        if (!(block instanceof CropBlock crop)) return;
        if (!crop.isMaxAge(state)) return;

        FarmingData data = player.getData(FarmingAttachment.FARMING);
        int levelNum = data.getLevel();

        // =========================
        // REQUIRE LEVEL 25+
        // =========================
        if (levelNum < 25) return;

        float multiplier = FarmingPerks.cropDropMultiplier(data);
        if (multiplier <= 1.0f) return;

        // =========================
        // GET BASE DROPS
        // =========================
        List<ItemStack> drops = Block.getDrops(
                state,
                level,
                event.getPos(),
                null,
                player,
                player.getMainHandItem()
        );

        // =========================
        // BONUS LOGIC
        // =========================
        for (ItemStack stack : drops) {

            // Skip seeds / crop block item
            if (stack.is(block.asItem())) continue;

            int baseCount = stack.getCount();
            float bonusAmount = baseCount * (multiplier - 1.0f);

            // Guaranteed bonus
            int guaranteed = (int) bonusAmount;
            if (guaranteed > 0) {
                ItemStack extra = stack.copy();
                extra.setCount(guaranteed);
                Block.popResource(level, event.getPos(), extra);
            }

            // Fractional chance bonus (+1)
            float fractional = bonusAmount - guaranteed;
            if (level.random.nextFloat() < fractional) {
                ItemStack extra = stack.copy();
                extra.setCount(1);
                Block.popResource(level, event.getPos(), extra);
            }
        }
    }
}
