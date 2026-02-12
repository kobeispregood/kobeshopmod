package net.lazy.kobe.mining;

import net.lazy.kobe.mastery.MasteryXp;
import net.lazy.kobe.oregen.CobbleGenEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class MiningEvents {

    private static final int GEN_XP = 5;

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {

        // -----------------------------
        // VALIDATION
        // -----------------------------
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        Block block = event.getState().getBlock();

        int xp = 0;

        // -----------------------------
        // GENERATOR XP
        // -----------------------------
        if (CobbleGenEvents.isCobbleGenerator(level, event.getPos())) {
            xp += GEN_XP;
        }

        // -----------------------------
        // ORE XP (GLOBAL)
        // -----------------------------
        int oreXp = getOreXp(block);

        if (oreXp > 0) {
            int playerLevel = player.getData(MiningAttachment.MINING).getLevel();
            float xpMultiplier = MiningPerks.getOreXpMultiplier(playerLevel);
            oreXp = Math.round(oreXp * xpMultiplier);
        }

        xp += oreXp;

        if (xp <= 0) return;

        // -----------------------------
        // APPLY XP (SINGLE SOURCE OF TRUTH)
        // -----------------------------
        MasteryXp.addMiningXp(player, xp);

        // -----------------------------
        // EXTRA DROP PERK (ORES ONLY)
        // -----------------------------
        int currentLevel = player.getData(MiningAttachment.MINING).getLevel();
        if (oreXp > 0 && MiningPerks.rollExtraDrop(currentLevel, level.random)) {
            Block.popResource(
                    level,
                    event.getPos(),
                    new ItemStack(block.asItem())
            );
        }
    }

    // =================================================
    // ORE XP TABLE
    // =================================================
    private static int getOreXp(Block block) {

        // Low tier
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) return 10;
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) return 10;
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return 10;

        // Mid tier
        if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return 15;
        if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return 15;
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return 15;

        // High tier
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return 25;
        if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return 25;

        // Ultra
        if (block == Blocks.NETHERITE_BLOCK) return 100;

        return 0;
    }
}
