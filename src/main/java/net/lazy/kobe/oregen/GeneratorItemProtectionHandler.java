package net.lazy.kobe.oregen;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class GeneratorItemProtectionHandler {

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {

        Entity entity = event.getEntity();

        // Only item drops
        if (!(entity instanceof ItemEntity item)) return;

        // Server side only
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = item.blockPosition();

        // Must be a cobble generator
        if (!CobbleGenEvents.isCobbleGenerator(level, pos)) return;

        // Find nearby player (generator owner context)
        Player player = level.getNearestPlayer(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                5.0,
                false
        );
        if (player == null) return;

        // Mining level gate
        int miningLevel = player
                .getData(MasteryAttachment.MASTERY)
                .getOrCreate(MasteryType.MINING)
                .getLevel();

        if (miningLevel < 10) return;

        // 🔥 FULL IMMUNITY BEFORE FIRST TICK
        item.setInvulnerable(true);
        item.clearFire();
    }
}
