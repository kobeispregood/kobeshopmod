package net.lazy.kobe.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class IslandProtectionEvents {

    private static final int PROTECT_RADIUS = 100;

    // ------------------------------------------
    // CHECK IF PLAYER CAN MODIFY
    // ------------------------------------------
    private static boolean canModify(ServerPlayer player, BlockPos pos) {
        ServerLevel level = (ServerLevel) player.level();

        // Only protect island dimension
        if (!level.dimension().equals(IslandDimension.ISLAND_WORLD)) {
            return true;
        }

        // Check all islands to see if pos is inside protected radius
        for (ServerPlayer owner : player.server.getPlayerList().getPlayers()) {

            var data = PlayerIslandStorage.loadIsland(owner);
            if (data == null) continue;

            double centerX = data.x + 12.5;
            double centerZ = data.z + 12.5;

            double dx = pos.getX() - centerX;
            double dz = pos.getZ() - centerZ;

            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > PROTECT_RADIUS)
                continue; // Not inside this island’s protected zone

            // If we reached here → inside protected zone

            // Owner always allowed
            if (player.getUUID().equals(owner.getUUID()))
                return true;

            // Locked island blocks all non-trusted
            if (data.locked &&
                    !PlayerIslandStorage.isTrusted(owner, player.getUUID()) &&
                    !PlayerIslandStorage.isCoop(owner, player.getUUID()))
                return false;

            // Trust / Coop allowed
            return PlayerIslandStorage.isTrusted(owner, player.getUUID()) ||
                    PlayerIslandStorage.isCoop(owner, player.getUUID());
        }

        // No island matched → allow
        return true;
    }

    // ------------------------------------------
    // BLOCK BREAK
    // ------------------------------------------
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        if (!canModify(player, event.getPos())) {
            event.setCanceled(true);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cYou cannot break blocks here."),
                    true
            );
        }
    }

    // ------------------------------------------
    // BLOCK PLACE
    // ------------------------------------------
    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!canModify(player, event.getPos())) {
            event.setCanceled(true);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cYou cannot place blocks here."),
                    true
            );
        }
    }

    // ------------------------------------------
    // RIGHT-CLICK BLOCK
    // ------------------------------------------
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!canModify(player, event.getHitVec().getBlockPos())) {
            event.setCanceled(true);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cYou cannot interact here."),
                    true
            );
        }
    }

    // ------------------------------------------
    // RIGHT-CLICK ITEM (buckets, etc.)
    // ------------------------------------------
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!canModify(player, player.blockPosition())) {
            event.setCanceled(true);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cYou cannot use that here."),
                    true
            );
        }
    }
}