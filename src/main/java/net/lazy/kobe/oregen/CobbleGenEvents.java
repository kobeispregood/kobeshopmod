package net.lazy.kobe.oregen;

import net.lazy.kobe.mining.MiningAttachment;
import net.lazy.kobe.oregen.data.OreGenTierLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.List;

import static net.lazy.kobe.oregen.data.OreGenTierLoader.TIERS;

public class CobbleGenEvents {

    /* ============================================================
     *  ORE GENERATION — ON COBBLE FORMATION (PLAYER-BASED TIER)
     * ============================================================ */
    @SubscribeEvent
    public void onCobbleForm(BlockEvent.FluidPlaceBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockState state = event.getNewState();
        if (!state.is(Blocks.COBBLESTONE) && !state.is(Blocks.STONE)) return;

        BlockPos pos = event.getPos();
        if (!isCobbleGenerator(level, pos)) return;

        // 🔑 Find nearest player (generator owner / upgrader)
        ServerPlayer player = null;

        var nearest = level.getNearestPlayer(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                6.0,
                false
        );

        if (nearest instanceof ServerPlayer sp) {
            player = sp;
        }


        int tier = 1;

        if (player != null) {
            tier = player.getData(OreGenAttachment.LEVEL) + 1;
        }


        Block result = rollOre(level.random, tier);

        // 🔒 SAFE replacement — no ghost blocks
        event.setNewState(result.defaultBlockState());
    }

    /* ============================================================
     *  ITEM FIRE PROTECTION (MINING LEVEL 10+)
     * ============================================================ */
    @SubscribeEvent
    public void onItemDrops(LivingDropsEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        BlockPos pos = event.getEntity().blockPosition();
        if (!isCobbleGenerator(level, pos)) return;

        int miningLevel = player.getData(MiningAttachment.MINING).getLevel();
        if (miningLevel < 10) return;

        event.getDrops().forEach(drop -> {
            if (drop instanceof ItemEntity item) {
                item.setRemainingFireTicks(0);
            }
        });
    }

    /* ============================================================
     *  ROLL LOGIC (JSON-DRIVEN)
     * ============================================================ */
    private Block rollOre(RandomSource rand, int tier) {

        if (tier >= 5 && rand.nextInt(100_000) == 0) {
            return Blocks.NETHERITE_BLOCK;
        }

        if (TIERS.isEmpty()) return Blocks.COBBLESTONE;

        int maxTier = TIERS.keySet().stream().max(Integer::compareTo).orElse(1);
        int effectiveTier = Math.min(tier, maxTier);

        List<OreGenTierLoader.WeightedBlock> list = TIERS.get(effectiveTier);
        if (list == null || list.isEmpty()) return Blocks.COBBLESTONE;

        int totalWeight = list.stream()
                .mapToInt(OreGenTierLoader.WeightedBlock::weight)
                .sum();

        int roll = rand.nextInt(totalWeight);

        int current = 0;
        for (OreGenTierLoader.WeightedBlock wb : list) {
            current += wb.weight();
            if (roll < current) {
                return wb.block();
            }
        }

        return Blocks.COBBLESTONE;
    }

    /* ============================================================
     *  GENERATOR DETECTION
     * ============================================================ */
    public static boolean isCobbleGenerator(ServerLevel level, BlockPos pos) {
        boolean hasWater = false;
        boolean hasLava = false;

        for (Direction dir : Direction.values()) {
            BlockState s = level.getBlockState(pos.relative(dir));
            if (s.getFluidState().is(Fluids.WATER)) hasWater = true;
            if (s.getFluidState().is(Fluids.LAVA)) hasLava = true;
        }

        return hasWater && hasLava;
    }
}
